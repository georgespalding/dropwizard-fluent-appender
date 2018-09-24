package io.github.dropwizard.logging.fluent;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;

public class FluentLoggingAppender extends FluentBaseAppender<ILoggingEvent, LoggingEventVO> {

   public FluentLoggingAppender(
      String host,
      int port,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis,
      FluentEncoder encoder
   ) {
      super(host, port, reconnectionDelayMillis, acceptConnectionTimeoutMillis, encoder);
   }

   @Override
   protected long getTimeStamp(LoggingEventVO data) {
      return data.getTimeStamp();
   }

   protected LoggingEventVO transform(ILoggingEvent event) {
      if (event == null) {
         return null;
      }
      if (event instanceof LoggingEvent) {
         return LoggingEventVO.build(event);
      } else if (event instanceof LoggingEventVO) {
         return (LoggingEventVO) event;
      } else {
         throw new IllegalArgumentException("Unsupported type " + event.getClass().getName());
      }
   }

}
