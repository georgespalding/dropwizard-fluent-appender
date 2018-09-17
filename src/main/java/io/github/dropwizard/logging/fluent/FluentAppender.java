package io.github.dropwizard.logging.fluent;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.google.common.collect.ImmutableMap;
import org.fluentd.logger.sender.Sender;

public class FluentAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

   private final String tag;
   private final Sender sender;
   private final ImmutableMap<String, String> customFields;
   private final boolean includeCallerData;
   private final boolean includeContext;
   private final boolean includeMdc;

   public FluentAppender(
      String tag,
      Sender sender,
      ImmutableMap<String, String> customFields,
      boolean includeCallerData,
      boolean includeContext,
      boolean includeMdc
   ) {
      this.tag = tag;
      this.sender = sender;
      this.customFields = customFields;
      this.includeCallerData = includeCallerData;
      this.includeContext = includeContext;
      this.includeMdc = includeMdc;
   }

   @Override
   protected void append(ILoggingEvent rawData) {
      final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

      builder.put("msg", rawData.getFormattedMessage());
      builder.put("level", rawData.getLevel().levelStr);
      builder.put("category", rawData.getLoggerName());
      builder.put("thread", rawData.getThreadName());
      builder.putAll(customFields);

      // CallerData
      if (includeCallerData) {
         ofNullable(rawData.getCallerData())
            .filter(stes -> stes.length > 0)
            .map(stes -> stream(stes).map(StackTraceElement::toString).toArray(String[]::new))
            .ifPresent(arr -> builder.put("stacktrace", arr));
      }

      // Context
      if (includeContext) {
         rawData.getLoggerContextVO().getPropertyMap()
            .forEach((k, v) -> {
               if (v != null) {
                  builder.put(k, v);
               }
            });
      }

      // MDC
      if (includeMdc) {
         rawData.getMDCPropertyMap()
            .forEach((k, v) -> {
               if (v != null) {
                  builder.put(k, v);
               }
            });
      }

      final long timestamp = rawData.getTimeStamp();
      //FIXME investigate if timestamp is/should be seconds or millis
      sender.emit(tag, timestamp, builder.build());
   }

   @Override
   public void start() {
      super.start();
   }

   @Override
   public void stop() {
      super.stop();
      sender.flush();
      sender.close();
   }

}