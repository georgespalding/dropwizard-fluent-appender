package io.github.geospa.logback.fluent;

import static java.lang.Math.toIntExact;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.ToLongFunction;

public abstract class FluentEncoder<E extends DeferredProcessingAware, EF extends EventField<E>> {

   protected static final EpochMillisEncoder V0 = (MessageBufferPacker packer, long epochMillis)
      -> packer.packLong(epochMillis / 1000);
   protected static final EpochMillisEncoder V1 = (MessageBufferPacker packer, long epochMillis) -> {
      // https://github.com/fluent/fluentd/wiki/Forward-Protocol-Specification-v0#eventtime-ext-format
      final int epochSecs = toIntExact(epochMillis / 1000);
      final int epochNanoPart = toIntExact((epochMillis % 1000) * 1_000_000);
      // EVENT TIME
      packer
         .packExtensionTypeHeader((byte) 0, Long.BYTES)
         .addPayload(ByteBuffer.allocate(Long.BYTES).putInt(epochSecs).putInt(epochNanoPart).array());
   };

   protected final ObjectMapper msgpackMapper = new ObjectMapper(new MessagePackFactory());
   protected final ToLongFunction<E> timestampGetter;
   protected final EpochMillisEncoder timestampEncoder;
   protected final ImmutableSet<EF> fields;

   public FluentEncoder(
      ToLongFunction<E> timestampGetter,
      EpochMillisEncoder timestampEncoder,
      ImmutableSet<EF> fields
   ) {
      this.timestampGetter = timestampGetter;
      this.timestampEncoder = timestampEncoder;
      this.fields = fields;
   }

   public byte[] encodeFluentEvent(String tag, E event) throws IOException {
      final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
      // ARRAY[3]
      packer.packArrayHeader(3);
      // TAG
      encodeTag(packer, tag);
      // EVENT TIME
      encodeTimestamp(packer, event);
      packer.close();

      final byte[] tagAndTimestamp = packer.toByteArray();

      // Add message parts
      final byte[] data = packData(event);

      // Join the first two and the third together
      return ByteBuffer.allocate(tagAndTimestamp.length + data.length)
         .put(tagAndTimestamp)
         .put(data)
         .array();
   }

   protected void encodeTag(MessageBufferPacker packer, String tag) throws IOException {
      packer.packString(tag);
   }

   protected void encodeTimestamp(MessageBufferPacker packer, E event) throws IOException {
      timestampEncoder.accept(packer, timestampGetter.applyAsLong(event));
   }

   protected byte[] packData(E event) throws JsonProcessingException {
      final ObjectNode root = msgpackMapper.createObjectNode();
      fields.forEach(field -> field.appendValue(event, root, msgpackMapper));

      return msgpackMapper.writeValueAsBytes(root);
   }

   @FunctionalInterface
   public interface EpochMillisEncoder {

      void accept(MessageBufferPacker t, long value) throws IOException;
   }

}
