package io.github.geospa.logback.fluent.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.geospa.logback.fluent.FluentBaseAppender;
import io.github.geospa.logback.fluent.FluentEncoder;

public class FluentLoggingAppender extends FluentBaseAppender<ILoggingEvent, LoggingEventField> {

   public FluentLoggingAppender(
      String tag,
      String host,
      int port,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis,
      FluentEncoder<ILoggingEvent, LoggingEventField> encoder
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
