package io.github.geospa.logback.fluent.dropwizard.logger;

import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.dropwizard.FluentEncoderFactory;
import io.github.geospa.logback.fluent.logger.FluentV0LoggingEncoder;
import io.github.geospa.logback.fluent.logger.FluentV1LoggingEncoder;
import io.github.geospa.logback.fluent.logger.LoggingEventField;

public abstract class FluentLoggingEncoderFactory implements FluentEncoderFactory<ILoggingEvent, LoggingEventField> {

   protected final ImmutableSet<LoggingEventField> fields;

   protected FluentLoggingEncoderFactory(ImmutableSet<LoggingEventField> fields) {
      this.fields = ofNullable(fields).orElse(LoggingEventField.STANDARD);
   }

   @JsonTypeName("v0")
   public static class V0 extends FluentLoggingEncoderFactory {

      @JsonCreator
      public V0(
         @JsonProperty("fields") ImmutableSet<LoggingEventField> fields
      ) {
         super(fields);
      }

      @Override
      public FluentV0LoggingEncoder build(String appName) {
         return new FluentV0LoggingEncoder(fields);
      }
   }

   @JsonTypeName("v1")
   public static class V1 extends FluentLoggingEncoderFactory {

      @JsonCreator
      public V1(
         @JsonProperty("fields") ImmutableSet<LoggingEventField> fields
      ) {
         super(fields);
      }

      @Override
      public FluentV1LoggingEncoder build(String appName) {
         return new FluentV1LoggingEncoder(fields);
      }
   }
}
