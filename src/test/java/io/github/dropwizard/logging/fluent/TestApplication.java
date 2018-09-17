package io.github.dropwizard.logging.fluent;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestApplication extends Application<Configuration> {

   private static final Logger LOG = LoggerFactory.getLogger(TestApplication.class);

   @Override
   public void initialize(Bootstrap<Configuration> bootstrap) {
      bootstrap.setConfigurationSourceProvider(
         new SubstitutingSourceProvider(
            new ResourceConfigurationSourceProvider(),
            new EnvironmentVariableSubstitutor(true, true)));
   }

   @Override
   public void run(Configuration configuration, Environment environment) {
      environment.jersey().register(new LogResource("com.wikia.dropwizard.logstash.appender.TEST"));
      LOG.info("Logstash test application started!!!");
   }

}
