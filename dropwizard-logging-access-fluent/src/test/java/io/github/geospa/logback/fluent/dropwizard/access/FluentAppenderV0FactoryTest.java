package io.github.geospa.logback.fluent.dropwizard.access;

import static io.dropwizard.testing.ConfigOverride.config;
import static io.github.geospa.logback.fluent.dropwizard.FluentBaseAppenderFactory.FLUENTD_DEFAULT_PORT;
import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.Configuration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.util.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class FluentAppenderV0FactoryTest {

   private static final String TAG = "dropwizard." + new TestApplication().getName();

   @Rule
   public final TestRule chain;
   private final FluentdLog fluentdLog = new FluentdLog();

   private final GenericContainer fluentd = new GenericContainer("fluent/fluentd:v0.12")
      .withClasspathResourceMapping("fluent.conf", "/fluentd/etc/fluent.conf", BindMode.READ_ONLY)
      .withExposedPorts(FLUENTD_DEFAULT_PORT)
      .waitingFor(new LogMessageWaitStrategy().withRegEx(".*?listening fluent socket on 0\\.0\\.0\\.0:24224\n"))
      .withLogConsumer(fluentdLog);
   private URI appUri;
   private DropwizardAppRule<Configuration> appRule;

   public FluentAppenderV0FactoryTest() {
      final EnvironmentVariables environmentVariables = new EnvironmentVariables();
      environmentVariables.set("SERVICE_SERVICE", "LogstashTestService");
      environmentVariables.set("SERVICE_REALM", "junit-test");
      environmentVariables.set("SERVICE_KUBERNETES_CLUSTER", "fake-cluster");

      chain = RuleChain
         .outerRule(fluentd)
         .around(environmentVariables)
         .around((statement, description) -> new Statement() {
            @Override
            public void evaluate() throws Throwable {
               final String fluentdHost = fluentd.getContainerIpAddress();
               final String fluentdPort = Integer.toString(fluentd.getMappedPort(FLUENTD_DEFAULT_PORT));

               // Need to defer creation of appRule until fluentd in docker has started, to be able to configure port
               appRule = new DropwizardAppRule<>(
                  TestApplication.class,
                  "integration-test.yaml",
                  config("server.requestLog.appenders[0].host", fluentdHost),
                  config("server.requestLog.appenders[0].port", fluentdPort),
                  config("server.requestLog.appenders[0].encoder.type", "v0-access"));
               appRule.apply(statement, description).evaluate();
            }
         }).around(new ExternalResource() {
            @Override
            protected void before() {
               appUri = URI.create(String.format("http://localhost:%d", appRule.getPort(0)));
            }
         });
   }

   @Test
   public void testLogAppender() throws InterruptedException {
      final String msg = "this-is-a-an-access-log";
      makeLogRequestAndAssertSuccess("log", msg);
      // GET /log/log%20message%20XYZ%20(fluent) HTTP/1.1
      final String logLine = getLogLine("GET /log/"+ msg +" HTTP/1.1");
      assertThat(logLine).isNotNull();
      // TODO assert contents
   }
   
   private void makeLogRequestAndAssertSuccess(String path, String msg) {
      final Response logResponse = ClientBuilder.newClient()
         .target(appUri)
         .path(path)
         .path(msg)
         .request(MediaType.TEXT_PLAIN_TYPE)
         .buildGet()
         .invoke();
      final String responseBody = logResponse.readEntity(String.class);
      assertThat(logResponse.getStatus())
         .describedAs("Unexpected status code. Body: %s", responseBody)
         .isEqualTo(200);
      assertThat(responseBody)
         .describedAs("Unexpected response body")
         .isEqualTo("Ok");
   }

   protected String getLogLine(String msg) throws InterruptedException {
      
      String logLine;
      while (null != (logLine = fluentdLog.getLogline(Duration.seconds(5)))) {
         if (logLine.contains(msg)) {
            return logLine;
         }
      }
      System.err.println("Failed to find "+msg+" in fluentd output");
      return null;
   }

   private static class FluentdLog implements Consumer<OutputFrame> {

      private final LinkedBlockingDeque<String> logLines = new LinkedBlockingDeque<>();

      @Override
      public void accept(OutputFrame outputFrame) {
         System.out.print(outputFrame.getType() + ": " + outputFrame.getUtf8String());
         switch (outputFrame.getType()) {
            case STDOUT:
               Arrays.stream(outputFrame.getUtf8String().split("\n"))
                  .filter(l -> l.contains(TAG))
                  .forEach(logLines::add);
               break;
         }
      }

      String getLogline(Duration timeout) throws InterruptedException {
         return logLines.poll(timeout.getQuantity(), timeout.getUnit());
      }
   }
}
