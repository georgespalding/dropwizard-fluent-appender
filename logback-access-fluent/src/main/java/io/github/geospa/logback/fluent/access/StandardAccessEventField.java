package io.github.geospa.logback.fluent.access;

import static java.util.Optional.ofNullable;

import ch.qos.logback.access.spi.IAccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.Enumeration;

public enum StandardAccessEventField implements AccessEventField {
   timestamp {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getTimeStamp());
      }
   },
   contentLength {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getContentLength());
      }
   },
   elapsedTime {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getElapsedTime());
      }
   },
   elapsedSeconds {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getElapsedSeconds());
      }
   },
   status {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getStatusCode());
      }
   },
   method {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getMethod());
      }
   },
   protocol {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getProtocol());
      }
   },
   localPort {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getLocalPort());
      }
   },
   remoteAddr {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getRemoteAddr());
      }
   },
   remoteHost {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getRemoteHost());
      }
   },
   remoteUser {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getRemoteUser());
      }
   },
   queryString {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getQueryString());
      }
   },
   requestUri {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getRequestURI());
      }
   },
   requestUrl {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getRequestURL());
      }
   },
   requestHeaderMap {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         final ObjectNode node = parent.putObject(name());
         event.getRequestHeaderMap().forEach(node::put);
      }
   },
   requestHeaderNames {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         final ArrayNode node = parent.putArray(name());
         final Enumeration<String> requestHeaderNames = event.getRequestHeaderNames();
         while (requestHeaderNames.hasMoreElements()) {
            node.add(requestHeaderNames.nextElement());
         }
      }
   },
   requestParameterMap {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         final ObjectNode reqParams = parent.putObject(name());
         event.getRequestParameterMap()
            .forEach(
               (reqParamName, reqParamValueArr) ->
                  ofNullable(reqParamValueArr)
                     .map(Arrays::stream)
                     .ifPresent(reqParamValues -> {
                        final ArrayNode valuesNode = reqParams.putArray(reqParamName);
                        reqParamValues.forEach(valuesNode::add);
                     }));
      }
   },
   requestContent {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getRequestContent());
      }
   },
   responseContent {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getResponseContent());
      }
   },
   responseHeaderMap {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         final ObjectNode node = parent.putObject(name());
         event.getResponseHeaderMap().forEach(node::put);
      }
   },
   responseHeaderNames {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         final ArrayNode node = parent.putArray(name());
         event.getResponseHeaderNameList().forEach(node::add);
      }
   },
   sessionID {
      public void appendValue(
         IAccessEvent event,
         ObjectNode parent,
         ObjectMapper factory
      ) {
         parent.put(name(), event.getSessionID());
      }
   },
   ;
}
