package io.github.geospa.logback.fluent.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.geospa.logback.fluent.EventFieldFactory;
import io.github.geospa.logback.fluent.logger.LoggingEventField.DynamicLoggingEventField;

public enum DynamicLoggingEventFieldFactory implements EventFieldFactory<ILoggingEvent, DynamicLoggingEventField> {
   mdc {
      public DynamicLoggingEventField buildGetter(String property) {
         return new DynamicLoggingEventField(name() + "." + property) {
            @Override
            public void appendValue(
               ILoggingEvent event,
               ObjectNode parent,
               ObjectMapper factory
            ) {
               parent.put(name(), event.getMDCPropertyMap().get(property));
            }
         };
      }
   },
   context {
      public DynamicLoggingEventField buildGetter(String property) {
         return new DynamicLoggingEventField(name() + "." + property) {
            @Override
            public void appendValue(
               ILoggingEvent event,
               ObjectNode parent,
               ObjectMapper factory
            ) {
               parent.put(name(), event.getLoggerContextVO().getPropertyMap().get(property));
            }
         };
      }
   },
   ;
}
