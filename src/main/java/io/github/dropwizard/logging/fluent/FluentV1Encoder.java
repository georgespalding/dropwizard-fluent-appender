package io.github.dropwizard.logging.fluent;

import static java.lang.Math.toIntExact;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FluentV1Encoder extends FluentBaseEncoder {

   public FluentV1Encoder(String tag) {
      super(tag);
   }

   protected byte[] packTagAndTimestamp(long epochMillis) throws IOException {
      // https://github.com/fluent/fluentd/wiki/Forward-Protocol-Specification-v0#eventtime-ext-format
      final int epochSecs = toIntExact(epochMillis / 1000);
      final int epochNanoPart = toIntExact((epochMillis % 1000) * 1_000_000);
      final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
      packer
         // ARRAY[3]
         .packArrayHeader(3)
         // TAG
         .packString(tag)
         // EVENT TIME
         .packExtensionTypeHeader((byte) 0, Long.BYTES)
         .addPayload(ByteBuffer.allocate(Long.BYTES).putInt(epochSecs).putInt(epochNanoPart).array())
         // We'll add the actual message later
         .close();
      return packer.toByteArray();
   }

}
