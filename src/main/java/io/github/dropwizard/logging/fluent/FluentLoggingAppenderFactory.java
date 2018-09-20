package io.github.dropwizard.logging.fluent;

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

@JsonTypeName("fluent")
public class FluentLoggingAppenderFactory extends FluentBaseAppenderFactory<ILoggingEvent> {

   @JsonCreator
   public FluentLoggingAppenderFactory(
      @JsonProperty("host")
      String host,
      @JsonProperty("port")
      Integer port,
      @JsonProperty("tag")
      String tag,
      @JsonProperty("reconnectionDelay")
      Duration reconnectionDelay,
      @JsonProperty("acceptConnectionTimeout")
      Duration acceptConnectionTimeout
   ) {
      super(host, port, tag, reconnectionDelay, acceptConnectionTimeout);
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
         host,
         port,
         tag.orElse("dropwizard." + applicationName),
         reconnectionDelay.toMilliseconds(),
         acceptConnectionTimeoutMillis);

      appender.addFilter(levelFilterFactory.build(threshold));
      appender.start();

      return wrapAsync(appender, asyncAppenderFactory);
   }
}
