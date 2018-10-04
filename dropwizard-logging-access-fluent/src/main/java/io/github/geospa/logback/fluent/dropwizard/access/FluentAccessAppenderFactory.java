package io.github.geospa.logback.fluent.dropwizard.access;

import static java.util.Optional.ofNullable;

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
import io.github.geospa.logback.fluent.access.AccessEventField;
import io.github.geospa.logback.fluent.access.FluentAccessAppender;
import io.github.geospa.logback.fluent.dropwizard.FluentBaseAppenderFactory;

@JsonTypeName("fluent-access")
public class FluentAccessAppenderFactory extends FluentBaseAppenderFactory<IAccessEvent, AccessEventField> {

   @JsonCreator
   public FluentAccessAppenderFactory(
      @JsonProperty("tag") String tag,
      @JsonProperty("host") String host,
      @JsonProperty("port") Integer port,
      @JsonProperty("reconnectionDelay") Duration reconnectionDelay,
      @JsonProperty("acceptConnectionTimeout") Duration acceptConnectionTimeout,
      @JsonProperty("encoder") FluentAccessEncoderFactory encoder
   ) {
      super(tag, host, port, reconnectionDelay, acceptConnectionTimeout, encoder);
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
         ofNullable(tag).orElseGet(() -> "dropwizard." + applicationName),
         host,
         port,
         reconnectionDelay.toMilliseconds(),
         acceptConnectionTimeoutMillis,
         ofNullable(encoder)
            .orElseGet(() -> new FluentAccessEncoderFactory.V0(null))
            .build(applicationName));

      appender.setName("fluent");
      appender.addFilter(levelFilterFactory.build(threshold));
      appender.setContext(context);
      appender.start();

      return wrapAsync(appender, asyncAppenderFactory);
   }
}
