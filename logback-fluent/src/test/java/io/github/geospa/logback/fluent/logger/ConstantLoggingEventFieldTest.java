package io.github.geospa.logback.fluent.logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantLoggingEventFieldTest {

   private static final String fqcn = "com.test.Instance";
   private static final Logger LOG_TEST_INSTANCE = LoggerFactory.getLogger(fqcn);
   private static final LoggingEvent loggingEvent = new LoggingEvent(
      fqcn,
      (ch.qos.logback.classic.Logger) LOG_TEST_INSTANCE,
      Level.TRACE,
      "A message to you, {}",
      new Exception("This is an Exception"),
      new String[] { "Rudy" });
   private static final ObjectMapper msgpackMapper = new ObjectMapper(new MessagePackFactory());
   private ObjectNode parent;

   @Before
   public void setUp() {
      parent = msgpackMapper.createObjectNode();
   }

   @Test
   public void myExample() {
      final String name = "myConstant";
      final String value = "example";
      final String field = name + "=" + value;
      final LoggingEventField myExample = LoggingEventField.fromName(field);
      myExample.appendValue(loggingEvent, parent, msgpackMapper);

      assertThat(myExample.name())
         .isEqualTo(name);
      assertThat(parent.get(name))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(name).textValue())
         .isEqualTo(value);
   }

   @Test
   public void dottedNameIsOk() {
      final String name = "my.constant";
      final String value = "example";
      final String field = name + "=" + value;
      LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
      assertThat(parent.get(name))
         .isExactlyInstanceOf(TextNode.class);
      assertThat(parent.get(name).textValue())
         .isEqualTo(value);
   }

   @Test
   public void prefixOfDynamicFieldNotAllowed() {
      final String name = "mdc.does.not.work";
      final String value = "example";
      final String field = name + "=" + value;
      try {
         LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      }catch (IllegalArgumentException expected){
         assertThat(expected).hasMessage("mdc.does.not.work has a prefix of a dynamic logging event field");
      }
   }

   @Test
   public void overwriteStandardFieldNotAllowed() {
      final String name = "message";
      final String value = "example";
      final String field = name + "=" + value;
      try {
         LoggingEventField.fromName(field).appendValue(loggingEvent, parent, msgpackMapper);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      }catch (IllegalArgumentException expected){
         assertThat(expected).hasMessage("message is a standard logging event field");
      }
   }

}