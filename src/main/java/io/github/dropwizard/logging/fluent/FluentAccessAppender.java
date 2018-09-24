package io.github.dropwizard.logging.fluent;

import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.access.spi.IAccessEvent;

public class FluentAccessAppender extends FluentBaseAppender<IAccessEvent, AccessEvent> {

   public FluentAccessAppender(
      String host,
      int port,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis,
      FluentEncoder encoder
   ) {
      super(host, port, reconnectionDelayMillis, acceptConnectionTimeoutMillis, encoder);
   }

   @Override
   protected long getTimeStamp(AccessEvent data) {
      return data.getTimeStamp();
   }

   @Override
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
