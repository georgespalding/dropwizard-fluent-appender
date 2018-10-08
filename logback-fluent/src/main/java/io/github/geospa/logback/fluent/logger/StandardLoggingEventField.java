package io.github.geospa.logback.fluent.logger;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;

public enum StandardLoggingEventField implements LoggingEventField {
   timestamp {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getTimeStamp());
      }
   },
   levelStr {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getLevel())
            .ifPresent(l -> parent.put(name(), l.toString()));
      }
   },
   levelInt {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getLevel())
            .ifPresent(l -> parent.put(name(), l.toInt()));
      }
   },
   logger {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getLoggerName())
            .ifPresent(ln -> parent.put(name(), ln));
      }
   },
   threadName {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getThreadName())
            .ifPresent(tn -> parent.put(name(), tn));
      }
   },
   formattedMessage {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getFormattedMessage())
            .ifPresent(fm -> parent.put(name(), fm));
      }
   },
   message {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getMessage())
            .ifPresent(m -> parent.put(name(), m));
      }
   },
   argumentArray {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getArgumentArray())
            .map(Arrays::stream)
            .ifPresent(argStream -> {
               final ArrayNode argArr = parent.putArray(name());
               argStream
                  // IntelliJ flips out if the generic type is not supplied
                  .map(factory::<JsonNode>valueToTree)
                  .forEach(argArr::add);
            });
      }
   },

   marker {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         ofNullable(event.getMarker())
            .ifPresent(m -> parent.put(name(), m.getName()));
      }
   },

   throwable {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         event.getThrowableProxy().getCause();

         ofNullable(event.getThrowableProxy())
            .ifPresent(tp -> appendCause(tp, parent));
      }

      public void appendCause(
         IThrowableProxy proxy,
         ObjectNode parent
      ) {
         final ObjectNode throwableNode = parent.putObject(throwable.name());
         throwableNode.put("message", proxy.getMessage());
         throwableNode.put("type", proxy.getClassName());
         final ArrayNode stackTrace = throwableNode.putArray("stackTrace");
         stream(proxy.getStackTraceElementProxyArray())
            .forEach(ste -> stackTrace.add(ste.getSTEAsString()));

         final IThrowableProxy cause = proxy.getCause();
         if (null != cause && proxy != cause) {
            appendCause(cause, throwableNode.putObject("cause"));
         }
      }
   },

   mdc {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         final ObjectNode node = parent.putObject(name());
         event.getMDCPropertyMap().forEach(node::put);
      }
   },

   caller {
      public void appendValue(
         ILoggingEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         final ArrayNode node = parent.putArray(name());
         stream(event.getCallerData())
            .forEach(ste -> node.add(ste.toString()));
      }
   },

   context {
      public void appendValue(ILoggingEvent event, ObjectNode parent, ObjectMapper factory) {
         ofNullable(event.getLoggerContextVO().getPropertyMap())
            .ifPresent(contextMap -> {
               final ObjectNode node = parent.putObject(name());
               contextMap.forEach(node::put);
            });
      }
   },
   ;
}
