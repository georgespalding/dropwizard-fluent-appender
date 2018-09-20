package io.github.dropwizard.logging.fluent;

import static java.lang.Math.toIntExact;
import static java.util.Optional.ofNullable;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.util.Duration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public abstract class FluentBaseAppenderFactory<E extends DeferredProcessingAware> extends AbstractAppenderFactory<E> {

   /**
    * The default fluentd port 24224.
    */
   public static final int FLUENTD_DEFAULT_PORT = 24224;
   /**
    * The default reconnection delay (30000 milliseconds or 30 seconds).
    */
   public static final Duration DEFAULT_RECONNECTION_DELAY = Duration.seconds(30);
   /**
    * Default timeout when waiting for the remote server to accept our
    * connection.
    */
   public static final Duration DEFAULT_ACCEPT_CONNECTION_DELAY = Duration.seconds(5);
   protected final Duration reconnectionDelay;
   protected final int acceptConnectionTimeoutMillis;
   protected final Optional<String> tag;

   @NotNull
   protected final String host;
   @Min(1)
   @Max(65535)
   protected final int port;

   public FluentBaseAppenderFactory(
      String host,
      Integer port,
      String tag,
      Duration reconnectionDelay,
      Duration acceptConnectionTimeout
   ) {
      this.host = host;
      this.port = ofNullable(port).orElse(FLUENTD_DEFAULT_PORT);
      this.tag = ofNullable(tag);
      this.reconnectionDelay = ofNullable(reconnectionDelay)
         .orElse(DEFAULT_RECONNECTION_DELAY);
      this.acceptConnectionTimeoutMillis = toIntExact(
         ofNullable(acceptConnectionTimeout)
            .orElse(DEFAULT_ACCEPT_CONNECTION_DELAY)
            .toMilliseconds());
   }
}
