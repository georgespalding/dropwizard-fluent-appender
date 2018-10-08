package io.github.geospa.logback.fluent.logger;

import static io.github.geospa.logback.fluent.EventFieldFactory.validateConstantEventFieldName;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.EventField;
import io.github.geospa.logback.fluent.EventFieldFactory;
import org.msgpack.jackson.dataformat.Tuple;

public interface LoggingEventField extends EventField<ILoggingEvent> {

   ImmutableSet<LoggingEventField> STANDARD = ImmutableSet.<LoggingEventField>builder()
      .add(StandardLoggingEventField.logger)
      .add(StandardLoggingEventField.levelStr)
      .add(StandardLoggingEventField.timestamp)
      .add(StandardLoggingEventField.threadName)
      .add(StandardLoggingEventField.formattedMessage)
      .build();

   @JsonCreator
   static LoggingEventField fromName(String name) {
      if (name.contains("=")) {
         final Tuple<String, String> tuple = EventField.nameParts(name, '=');
         validateConstantEventFieldName(
            tuple.first(),
            StandardLoggingEventField.values(),
            DynamicLoggingEventFieldFactory.values());
         return new LoggingEventField.ConstantLoggingEventField(tuple.first(), tuple.second());
      } else if (name.contains(".")) {
         final Tuple<String, String> tuple = EventField.nameParts(name, '.');
         return DynamicLoggingEventFieldFactory
            .valueOf(tuple.first())
            .buildGetter(tuple.second());
      } else {
         return StandardLoggingEventField.valueOf(name);
      }
   }

   class ConstantLoggingEventField extends EventFieldFactory.ConstantEventField<ILoggingEvent> implements
      LoggingEventField {

      public ConstantLoggingEventField(String name, String value) {
         super(name, value);
      }

   }

   abstract class DynamicLoggingEventField
      extends EventFieldFactory.DynamicEventField<ILoggingEvent>
      implements LoggingEventField {

      protected DynamicLoggingEventField(String name) {super(name);}
   }

}
