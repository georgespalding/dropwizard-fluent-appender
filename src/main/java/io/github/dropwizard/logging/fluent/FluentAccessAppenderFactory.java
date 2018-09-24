package io.github.dropwizard.logging.fluent;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import io.dropwizard.util.Duration;

@JsonTypeName("fluent-access")
public class FluentAccessAppenderFactory extends FluentBaseAppenderFactory<IAccessEvent> {

   @JsonCreator
   public FluentAccessAppenderFactory(
      @JsonProperty("host")
         String host,
      @JsonProperty("port")
         Integer port,
      @JsonProperty("reconnectionDelay")
         Duration reconnectionDelay,
      @JsonProperty("acceptConnectionTimeout")
         Duration acceptConnectionTimeout,
      @JsonProperty("encoder")
         FluentEncoderFactory encoder
   ) {
      super(host, port, reconnectionDelay, acceptConnectionTimeout, encoder);
   }

   @Override
   public Appender<IAccessEvent> build(
      LoggerContext context,
      String applicationName,
      LayoutFactory<IAccessEvent> layoutFactory,
      LevelFilterFactory<IAccessEvent> levelFilterFactory,
      AsyncAppenderFactory<IAccessEvent> asyncAppenderFactory
   ) {
      final FluentAccessAppender appender = new FluentAccessAppender(
         host,
         port,
         reconnectionDelay.toMilliseconds(),
         acceptConnectionTimeoutMillis,
         encoder.build(applicationName));

      appender.setName("fluent-access");
      appender.setContext(context);
      appender.addFilter(levelFilterFactory.build(threshold));
      appender.start();

      return wrapAsync(appender, asyncAppenderFactory);
   }
}
