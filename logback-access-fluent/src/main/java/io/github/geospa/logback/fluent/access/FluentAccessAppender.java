package io.github.geospa.logback.fluent.access;

import ch.qos.logback.access.spi.IAccessEvent;
import io.github.geospa.logback.fluent.FluentBaseAppender;
import io.github.geospa.logback.fluent.FluentEncoder;

public class FluentAccessAppender extends FluentBaseAppender<IAccessEvent, AccessEventField> {

   public FluentAccessAppender(
      String tag,
      String host,
      int port,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis,
      FluentEncoder<IAccessEvent, AccessEventField> encoder
   ) {
      super(
         tag,
         host,
         port,
         reconnectionDelayMillis,
         acceptConnectionTimeoutMillis,
         encoder);
   }

}
