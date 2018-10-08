package io.github.geospa.logback.fluent.logger;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.BasicMarkerFactory;

import java.util.Collections;

public class StandardLoggingEventFieldTest {

   private static final String fqcn = "com.test.Instance";
   private static final StackTraceElement[] fakeCallerData = new StackTraceElement[] {
      new StackTraceElement(
         fqcn,
         "aTestMethod",
         "test.java",
         13)
   };
   private static final Logger LOG_TEST_INSTANCE = LoggerFactory.getLogger(fqcn);
   private static final long beforeEventCreation = System.currentTimeMillis();
   private static final LoggingEvent loggingEvent = new LoggingEvent(
      fqcn,
      (ch.qos.logback.classic.Logger) LOG_TEST_INSTANCE,
      Level.TRACE,
      "A message to you, {}",
      new Exception("This is an Exception"),
      new String[] { "Rudy" });
   private static final long afterEventCreation = System.currentTimeMillis();
   private static final ObjectMapper msgpackMapper = new ObjectMapper(new MessagePackFactory());

   static {
      loggingEvent.setCallerData(fakeCallerData);
      loggingEvent.setMarker(new BasicMarkerFactory().getMarker("testMarker"));
      loggingEvent.setLoggerContextRemoteView(
         new LoggerContextVO(
            "test",
            Collections.singletonMap(
               "context.test",
               "a context value"),
            beforeEventCreation));
   }

   private ObjectNode parent;

   @Before
   public void setUp() {
      parent = msgpackMapper.createObjectNode();
   }

   @Test
   public void timestamp() {
      final String field = "timestamp";
      LoggingEventField.fromName(field)
         .appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(LongNode.class);
      assertThat(parent.get(field).longValue())
         .isBetween(beforeEventCreation, afterEventCreation);
   }

   @Test
   public void levelStr() {
      final String field = "levelStr";
      LoggingEventField.fromName(field)
         .appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field).textValue())
         .isEqualTo(Level.TRACE.toString());
   }

   @Test
   public void levelInt() {
      final String field = "levelInt";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(IntNode.class);
      assertThat(parent.get(field).intValue())
         .isEqualTo(Level.TRACE.toInt());
   }

   @Test
   public void logger() {
      final String field = "logger";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field).textValue())
         .isEqualTo(fqcn);
   }

   @Test
   public void threadName() {
      final String field = "threadName";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field).textValue())
         .isEqualTo("main");
   }

   @Test
   public void formattedMessage() {
      final String field = "formattedMessage";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field).textValue())
         .isEqualTo("A message to you, Rudy");
   }

   @Test
   public void message() {
      final String field = "message";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field).textValue())
         .isEqualTo("A message to you, {}");
   }

   @Test
   public void argumentArray() {
      final String field = "argumentArray";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(ArrayNode.class);
      ArrayNode argumentArray = (ArrayNode) parent.get(field);
      assertThat(argumentArray.elements())
         .contains(TextNode.valueOf("Rudy"));
   }

   @Test
   public void marker() {
      final String field = "marker";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field).textValue())
         .isEqualTo("testMarker");
   }

   @Test
   public void throwable() throws JsonProcessingException {
      final String field = "throwable";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(ObjectNode.class);
      DummyThrowable dummy = new ObjectMapper().treeToValue(parent.get(field), DummyThrowable.class);
      assertThat(dummy.message)
         .isEqualTo("This is an Exception");
      assertThat(dummy.type)
         .isEqualTo(Exception.class.getName());
      assertThat(dummy.stackTrace.length)
         .isGreaterThan(8);
      assertThat(dummy.stackTrace[0])
         .isEqualTo(
            "at io.github.geospa.logback.fluent.logger.StandardLoggingEventFieldTest.<clinit>(StandardLoggingEventFieldTest.java:39)");
      assertThat(dummy.cause)
         .isNull();
   }

   @Test
   public void mdc() {
      MDC.put("mdc.test", "a mdc value");

      final String field = "mdc";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(ObjectNode.class);
      ObjectNode mdc = (ObjectNode) parent.get(field);
      assertThat(mdc.fieldNames())
         .containsExactlyInAnyOrder("mdc.test");
      assertThat(mdc.get("mdc.test"))
         .isEqualTo(TextNode.valueOf("a mdc value"));

      MDC.remove("mdc.test");
   }

   @Test
   public void caller() {
      final String field = "caller";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(ArrayNode.class);
      ArrayNode caller = (ArrayNode) parent.get(field);
      assertThat(caller.elements())
         .containsExactlyInAnyOrder(TextNode.valueOf("com.test.Instance.aTestMethod(test.java:13)"));
   }

   @Test
   public void context() {
      final String field = "context";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(ObjectNode.class);
      ObjectNode context = (ObjectNode) parent.get(field);
      assertThat(context.fieldNames())
         .containsExactlyInAnyOrder("context.test");
      assertThat(context.get("context.test"))
         .isEqualTo(TextNode.valueOf("a context value"));
   }

   private static final class DummyThrowable {

      final String message;
      final String type;
      final String[] stackTrace;
      final DummyThrowable cause;

      @JsonCreator
      private DummyThrowable(
         @JsonProperty("message")
         String message,
         @JsonProperty("type")
         String type,
         @JsonProperty("stackTrace")
         String[] stackTrace,
         @JsonProperty("cause")
         DummyThrowable cause
      ) {
         this.message = message;
         this.type = type;
         this.stackTrace = stackTrace;
         this.cause = cause;
      }
   }

}