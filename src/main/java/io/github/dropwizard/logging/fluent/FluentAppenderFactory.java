package io.github.dropwizard.logging.fluent;

import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;

import javax.validation.constraints.NotNull;

@JsonTypeName("fluent")
public class FluentAppenderFactory extends AbstractAppenderFactory<ILoggingEvent> {

   protected final String tag;
   protected final ImmutableMap<String, String> customFields;
   protected final boolean includeCallerData;
   protected final boolean includeContext;
   protected final boolean includeMdc;
   @NotNull
   private final FluentSenderFactory sender;

   @JsonCreator
   public FluentAppenderFactory(
      @JsonProperty("tag")
         String tag,
      @JsonProperty("sender")
         FluentSenderFactory sender,
      @JsonProperty("customFields")
         ImmutableMap<String, String> customFields,
      @JsonProperty("includeCallerData")
         Boolean includeCallerData,
      @JsonProperty("includeContext")
         Boolean includeContext,
      @JsonProperty("includeMdc")
         Boolean includeMdc
   ) {
      this.tag = ofNullable(tag).orElse("dropwizard");
      this.sender = sender;
      this.customFields = customFields;
      this.includeCallerData = ofNullable(includeCallerData).orElse(false);
      this.includeContext = ofNullable(includeContext).orElse(true);
      this.includeMdc = ofNullable(includeMdc).orElse(true);
   }

   @Override
   public Appender<ILoggingEvent> build(
      LoggerContext context,
      String applicationName,
      LayoutFactory<ILoggingEvent> layoutFactory,
      LevelFilterFactory<ILoggingEvent> levelFilterFactory,
      AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory
   ) {
      final FluentAppender appender = new FluentAppender(
         tag + "." + applicationName,
         sender.build(),
         customFields,
         includeCallerData,
         includeContext,
         includeMdc);

      appender.addFilter(levelFilterFactory.build(threshold));
      appender.start();

      return wrapAsync(appender, asyncAppenderFactory);
   }
}
