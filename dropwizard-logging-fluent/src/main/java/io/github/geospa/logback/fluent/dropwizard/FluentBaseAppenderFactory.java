package io.github.geospa.logback.fluent.dropwizard;

import static java.lang.Math.toIntExact;
import static java.util.Optional.ofNullable;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.util.Duration;
import io.github.geospa.logback.fluent.EventField;
import io.github.geospa.logback.fluent.FluentEncoder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public abstract class FluentBaseAppenderFactory<E extends DeferredProcessingAware, EF extends EventField<E>> extends
   AbstractAppenderFactory<E> {

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
   public static final Duration DEFAULT_ACCEPT_CONNECTION_TIMEOUT = Duration.seconds(5);
   protected final Duration reconnectionDelay;
   protected final int acceptConnectionTimeoutMillis;
   @NotNull
   protected final String host;
   @Min(1)
   @Max(65535)
   protected final int port;
   protected final FluentEncoderFactory<E, EF> encoder;
   protected String tag;

   public FluentBaseAppenderFactory(
      String tag,
      String host,
      Integer port,
      Duration reconnectionDelay,
      Duration acceptConnectionTimeout,
      FluentEncoderFactory<E, EF> encoder
   ) {
      this.tag = tag;
      this.host = host;
      this.port = ofNullable(port).orElse(FLUENTD_DEFAULT_PORT);
      this.reconnectionDelay = ofNullable(reconnectionDelay)
         .orElse(DEFAULT_RECONNECTION_DELAY);
      this.acceptConnectionTimeoutMillis = toIntExact(
         ofNullable(acceptConnectionTimeout)
            .orElse(DEFAULT_ACCEPT_CONNECTION_TIMEOUT)
            .toMilliseconds());
      this.encoder = encoder;
   }

}
