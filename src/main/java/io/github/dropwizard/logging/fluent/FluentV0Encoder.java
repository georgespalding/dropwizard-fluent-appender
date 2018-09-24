package io.github.dropwizard.logging.fluent;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import java.io.IOException;

public class FluentV0Encoder extends FluentBaseEncoder {

   public FluentV0Encoder(String tag) {
      super(tag);
   }

   protected byte[] packTagAndTimestamp(long epochMillis) throws IOException {
      // Could not find a way to use msgpacker to write EventTime for V0 as described here:
      // https://github.com/fluent/fluentd/wiki/Forward-Protocol-Specification-v0#eventtime-ext-format
      // So for now we'll just encode epoch Seconds.
      final MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
      final long epochSecs = epochMillis / 1000;
      packer
         // ARRAY[3]
         .packArrayHeader(3)
         // TAG
         .packString(tag)
         // EVENT TIME
         .packLong(epochSecs)
         // We'll add the actual message later
         .close();
      return packer.toByteArray();
   }

}
