package io.github.geospa.logback.fluent;

import static java.util.Arrays.stream;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EventFieldFactory<E extends DeferredProcessingAware, EF extends EventField<E>> {

   static void validateConstantEventFieldName(String name, Enum<?>[] standardFields, Enum<?>[] dynamicFields) {
      // Validate the name does not conflict with predefined ones
      if (stream(standardFields)
         .anyMatch(std -> std.name().equals(name))) {
         throw new IllegalArgumentException(name + " is a standard logging event field");
      }
      if (stream(dynamicFields)
         .anyMatch(dyn -> name.startsWith(dyn.name() + "."))) {
         throw new IllegalArgumentException(name + " has a prefix of a dynamic logging event field");
      }
   }

   EF buildGetter(String property);

   abstract class DynamicEventField<E extends DeferredProcessingAware> implements EventField<E> {

      private final String name;

      protected DynamicEventField(String name) {this.name = name;}

      public String name() {
         return name;
      }
   }

   abstract class ConstantEventField<E extends DeferredProcessingAware> implements EventField<E> {

      private final String name;
      private final String value;

      public ConstantEventField(String name, String value) {
         this.name = name;
         this.value = value;
      }

      @Override
      public String name() {
         return name;
      }

      @Override
      public void appendValue(
         E ignored,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name, value);
      }
   }
}
