package io.github.geospa.logback.fluent.logger;

import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.FluentEncoder;

public class FluentV1LoggingEncoder extends FluentEncoder<ILoggingEvent, LoggingEventField> {

   public FluentV1LoggingEncoder(
      ImmutableSet<LoggingEventField> fields
   ) {
      super(
         ILoggingEvent::getTimeStamp,
         FluentEncoder.V1,
         ofNullable(fields).orElse(LoggingEventField.STANDARD));
   }
}
