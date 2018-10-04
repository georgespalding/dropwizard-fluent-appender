package io.github.geospa.logback.fluent;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EventFieldFactory<E extends DeferredProcessingAware, EF extends EventField<E>> {

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
