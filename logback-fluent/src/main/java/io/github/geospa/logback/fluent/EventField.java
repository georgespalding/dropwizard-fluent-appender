package io.github.geospa.logback.fluent;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.msgpack.jackson.dataformat.Tuple;

public interface EventField<E extends DeferredProcessingAware> {

   String name();

   void appendValue(E event, ObjectNode parent, ObjectMapper factory);

   static Tuple<String, String> nameParts(String name, char sep) {
      final int indexOfDot = name.indexOf(sep);
      return new Tuple<>(name.substring(0, indexOfDot), name.substring(indexOfDot + 1));
   }

}
