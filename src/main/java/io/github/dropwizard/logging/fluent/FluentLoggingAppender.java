package io.github.dropwizard.logging.fluent;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FluentLoggingAppender extends FluentBaseAppender<ILoggingEvent, LoggingEventVO> {

   public FluentLoggingAppender(
      String host,
      int port,
      String tag,
      long reconnectionDelayMillis,
      int acceptConnectionTimeoutMillis
   ) {
      super(host, port, tag, reconnectionDelayMillis, acceptConnectionTimeoutMillis);
   }

   @Override
   protected long getTimeStamp(LoggingEventVO data) {
      return data.getTimeStamp();
   }

   protected byte[] packData(LoggingEventVO data) throws JsonProcessingException {
      return msgpackMapper.writeValueAsBytes(data);
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
