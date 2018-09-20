package io.github.dropwizard.logging.fluent;

import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.spi.IAccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FluentAccessAppender extends FluentBaseAppender<IAccessEvent, AccessEvent> {

   public FluentAccessAppender(
      String host,
      int port,
      String tag,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis
   ) {
      super(host, port, tag, reconnectionDelayMillis, acceptConnectionTimeoutMillis);
   }

   @Override
   protected long getTimeStamp(AccessEvent data) {
      return data.getTimeStamp();
   }

   protected byte[] packData(AccessEvent data) throws JsonProcessingException {
      return msgpackMapper.writeValueAsBytes(data);
   }

   protected AccessEvent transform(IAccessEvent event) {
      if (event == null) {
         return null;
      }
      if (event instanceof AccessEvent) {
         return (AccessEvent) event;
      } else {
         throw new IllegalArgumentException("Unsupported type " + event.getClass().getName());
      }
   }

}
