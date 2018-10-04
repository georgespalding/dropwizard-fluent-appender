package io.github.geospa.logback.fluent.dropwizard.logger;

import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import io.dropwizard.util.Duration;
import io.github.geospa.logback.fluent.dropwizard.FluentBaseAppenderFactory;
import io.github.geospa.logback.fluent.logger.FluentLoggingAppender;
import io.github.geospa.logback.fluent.logger.LoggingEventField;

@JsonTypeName("fluent")
public class FluentLoggingAppenderFactory extends FluentBaseAppenderFactory<ILoggingEvent, LoggingEventField> {

   @JsonCreator
   public FluentLoggingAppenderFactory(
      @JsonProperty("tag") String tag,
      @JsonProperty("host") String host,
      @JsonProperty("port") Integer port,
      @JsonProperty("reconnectionDelay") Duration reconnectionDelay,
      @JsonProperty("acceptConnectionTimeout") Duration acceptConnectionTimeout,
      @JsonProperty("encoder") FluentLoggingEncoderFactory encoder
   ) {
      super(tag, host, port, reconnectionDelay, acceptConnectionTimeout, encoder);
   }

   @Override
   public Appender<ILoggingEvent> build(
      LoggerContext context,
      String applicationName,
      LayoutFactory<ILoggingEvent> layoutFactory,
      LevelFilterFactory<ILoggingEvent> levelFilterFactory,
      AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory
   ) {
      final FluentLoggingAppender appender = new FluentLoggingAppender(
         ofNullable(tag).orElseGet(() -> "dropwizard." + applicationName),
         host,
         port,
         reconnectionDelay.toMilliseconds(),
         acceptConnectionTimeoutMillis,
         ofNullable(encoder)
            .orElseGet(() -> new FluentLoggingEncoderFactory.V0(null))
            .build(applicationName));

      appender.setName("fluent");
      appender.addFilter(levelFilterFactory.build(threshold));
      appender.setContext(context);
      appender.start();

      return wrapAsync(appender, asyncAppenderFactory);
   }
}
