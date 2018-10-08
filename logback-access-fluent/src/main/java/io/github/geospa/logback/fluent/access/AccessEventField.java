package io.github.geospa.logback.fluent.access;

import static io.github.geospa.logback.fluent.EventFieldFactory.validateConstantEventFieldName;

import ch.qos.logback.access.spi.IAccessEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.EventField;
import io.github.geospa.logback.fluent.EventFieldFactory;
import org.msgpack.jackson.dataformat.Tuple;

public interface AccessEventField extends EventField<IAccessEvent> {

   // common: %h %l %u %user %date "%r" %s %b
   ImmutableSet<AccessEventField> COMMON = ImmutableSet.<AccessEventField>builder()
      .add(StandardAccessEventField.remoteAddr)
      .add(StandardAccessEventField.remoteUser)
      .add(StandardAccessEventField.remoteUser)
      .add(StandardAccessEventField.timestamp)
      .add(StandardAccessEventField.requestUrl)
      .add(StandardAccessEventField.status)
      .add(StandardAccessEventField.contentLength)
      .build();
   // combined: %h %l %u [%t] "%r" %s %b "%i{Referer}" "%i{User-Agent}"
   ImmutableSet<AccessEventField> COMBINED = ImmutableSet.<AccessEventField>builder()
      .add(StandardAccessEventField.remoteAddr)
      .add(StandardAccessEventField.remoteUser)
      .add(StandardAccessEventField.timestamp)
      .add(StandardAccessEventField.requestUrl)
      .add(StandardAccessEventField.status)
      .add(StandardAccessEventField.contentLength)
      .add(DynamicAccessEventFieldFactory.requestHeader.buildGetter("Referer"))
      .add(DynamicAccessEventFieldFactory.requestHeader.buildGetter("User-Agent"))
      .build();

   @JsonCreator
   static AccessEventField fromName(String name) {
      if (name.contains("=")) {
         final Tuple<String, String> tuple = EventField.nameParts(name, '=');
         validateConstantEventFieldName(
            tuple.first(),
            StandardAccessEventField.values(),
            DynamicAccessEventFieldFactory.values());
         return new ConstantAccessEventField(tuple.first(), tuple.second());
      } else if (name.contains(".")) {
         final Tuple<String, String> tuple = EventField.nameParts(name, '.');
         return DynamicAccessEventFieldFactory
            .valueOf(tuple.first())
            .buildGetter(tuple.second());
      } else {
         return StandardAccessEventField.valueOf(name);
      }
   }

   class ConstantAccessEventField extends EventFieldFactory.ConstantEventField<IAccessEvent> implements AccessEventField {

      public ConstantAccessEventField(String name, String value) {
         super(name, value);
      }

   }

   abstract class DynamicAccessEventField
      extends EventFieldFactory.DynamicEventField<IAccessEvent>
      implements AccessEventField {

      protected DynamicAccessEventField(String name) {super(name);}
   }
}
