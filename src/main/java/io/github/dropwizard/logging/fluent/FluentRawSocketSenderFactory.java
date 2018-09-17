package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.util.Duration;
import io.dropwizard.util.Size;
import org.fluentd.logger.sender.RawSocketSender;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@JsonTypeName("raw-socket")
public class FluentRawSocketSenderFactory implements FluentSenderFactory {

   public static final int FLUENTD_DEFAULT_PORT = 24224;

   @NotNull
   private final String host;
   @Min(1)
   @Max(65535)
   private final int port;
   private final int timeout;
   private final int bufferCapacity;

   private final FluentReconnectorFactory reconnector;

   @JsonCreator
   public FluentRawSocketSenderFactory(
      @JsonProperty("host")
         String host,
      @JsonProperty("port")
         Integer port,
      @JsonProperty("timeout")
         Duration timeout,
      @JsonProperty("bufferCapacity")
         Size bufferCapacity,
      @JsonProperty("reconnector")
         FluentReconnectorFactory reconnector
   ) {
      this.host = host;
      this.port = Optional.ofNullable(port).orElse(FLUENTD_DEFAULT_PORT);
      this.timeout = Math.toIntExact(Optional.ofNullable(timeout).orElse(Duration.seconds(3)).toMilliseconds());
      this.bufferCapacity = Math.toIntExact(Optional.ofNullable(bufferCapacity).orElse(Size.kilobytes(1)).toBytes());
      this.reconnector = Optional.ofNullable(reconnector).orElseGet(FluentExponentialDelayReconnectorFactory::new);
   }

   public RawSocketSender build() {
      return new RawSocketSender(host, port, timeout, bufferCapacity, reconnector.build());
   }

}
