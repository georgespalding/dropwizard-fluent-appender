package io.github.geospa.logback.fluent.access;

import static java.util.Optional.ofNullable;

import ch.qos.logback.access.spi.IAccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.geospa.logback.fluent.EventFieldFactory;

import java.util.Arrays;

public enum DynamicAccessEventFieldFactory implements EventFieldFactory<IAccessEvent, AccessEventField.DynamicAccessEventField> {
   requestHeader {
      public AccessEventField.DynamicAccessEventField buildGetter(String property) {
         return new AccessEventField.DynamicAccessEventField(name() + "." + property) {
            @Override
            public void appendValue(IAccessEvent event, ObjectNode parent, ObjectMapper factory) {
               parent.put(name(), event.getRequestHeader(property));
            }
         };
      }
   },
   responseHeader {
      public AccessEventField.DynamicAccessEventField buildGetter(String property) {
         return new AccessEventField.DynamicAccessEventField(name() + "." + property) {
            @Override
            public void appendValue(IAccessEvent event, ObjectNode parent, ObjectMapper factory) {
               parent.put(name(), event.getRequestHeader(property));
            }
         };
      }
   },
   requestParameter {
      public AccessEventField.DynamicAccessEventField buildGetter(String property) {
         return new AccessEventField.DynamicAccessEventField(name() + "." + property) {
            @Override
            public void appendValue(IAccessEvent event, ObjectNode parent, ObjectMapper factory) {
               ofNullable(event.getRequestParameter(property))
                  .map(Arrays::stream)
                  .ifPresent(pars -> {
                     ArrayNode node = parent.putArray(name());
                     pars.forEach(node::add);
                  });
            }
         };
      }
   },
   attribute {
      public AccessEventField.DynamicAccessEventField buildGetter(String property) {
         return new AccessEventField.DynamicAccessEventField(name() + "." + property) {
            @Override
            public void appendValue(IAccessEvent event, ObjectNode parent, ObjectMapper factory) {
               parent.put(name(), event.getAttribute(property));
            }
         };
      }
   },
   cookie {
      public AccessEventField.DynamicAccessEventField buildGetter(String property) {
         return new AccessEventField.DynamicAccessEventField(name() + "." + property) {
            @Override
            public void appendValue(IAccessEvent event, ObjectNode parent, ObjectMapper factory) {
               parent.put(name(), event.getCookie(property));
            }
         };
      }
   },
   ;
}
