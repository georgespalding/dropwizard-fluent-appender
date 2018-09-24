package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class FluentBaseEncoder implements FluentEncoder {

   protected final ObjectMapper msgpackMapper = new ObjectMapper(new MessagePackFactory());
   protected final String tag;

   public FluentBaseEncoder(String tag) {this.tag = tag;}

   public byte[] encodeFluentEvent(long timestampMillis, Object event) throws IOException {
      final byte[] tagAndTimestamp = packTagAndTimestamp(timestampMillis);
      final byte[] data = packData(event);

      // Join the first two and the third together
      return ByteBuffer.allocate(tagAndTimestamp.length + data.length)
         .put(tagAndTimestamp)
         .put(data)
         .array();
   }

   protected abstract byte[] packTagAndTimestamp(long epochMillis) throws IOException;

   protected byte[] packData(Object data) throws JsonProcessingException {
      return msgpackMapper.writeValueAsBytes(data);
   }

}
