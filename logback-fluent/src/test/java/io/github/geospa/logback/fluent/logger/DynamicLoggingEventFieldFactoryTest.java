package io.github.geospa.logback.fluent.logger;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collections;

public class DynamicLoggingEventFieldFactoryTest {

   private static final String fqcn = "com.test.Instance";
   private static final Logger LOG_TEST_INSTANCE = LoggerFactory.getLogger(fqcn);
   private static final long beforeEventCreation = System.currentTimeMillis();
   private static final LoggingEvent loggingEvent = new LoggingEvent(
      fqcn,
      (ch.qos.logback.classic.Logger) LOG_TEST_INSTANCE,
      Level.TRACE,
      "A message to you, {}",
      new Exception("This is an Exception"),
      new String[] { "Rudy" });
   private static final ObjectMapper msgpackMapper = new ObjectMapper(new MessagePackFactory());

   static {
      loggingEvent.setLoggerContextRemoteView(
         new LoggerContextVO(
            "test",
            Collections.singletonMap(
               "test",
               "a context value"),
            beforeEventCreation));
   }

   private ObjectNode parent;

   @Before
   public void setUp() {
      parent = msgpackMapper.createObjectNode();
   }

   @Test
   public void mdc() {
      final String field = "mdc.test";
      MDC.put("test", "a mdc value");

      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field))
         .isEqualTo(TextNode.valueOf("a mdc value"));

      MDC.remove("test");
   }

   @Test
   public void context() {
      final String field = "context.test";
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(field))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(field))
         .isEqualTo(TextNode.valueOf("a context value"));
   }

}