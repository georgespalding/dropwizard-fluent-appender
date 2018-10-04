package io.github.geospa.logback.fluent.logger;

import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.FluentEncoder;

public class FluentV0LoggingEncoder extends FluentEncoder<ILoggingEvent, LoggingEventField> {

   public FluentV0LoggingEncoder(
      ImmutableSet<LoggingEventField> fields
   ) {
      super(
         ILoggingEvent::getTimeStamp,
         V0,
         ofNullable(fields).orElse(LoggingEventField.STANDARD));
   }
}
