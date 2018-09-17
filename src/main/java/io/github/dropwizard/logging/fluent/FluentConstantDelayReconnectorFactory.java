package io.github.dropwizard.logging.fluent;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.util.Duration;
import org.fluentd.logger.sender.ConstantDelayReconnector;

@JsonTypeName("constant-delay")
public class FluentConstantDelayReconnectorFactory implements FluentReconnectorFactory {

   private final Duration wait;

   @JsonCreator
   public FluentConstantDelayReconnectorFactory(Duration wait) {
      this.wait = ofNullable(wait).orElse(Duration.milliseconds(50));
   }

   public ConstantDelayReconnector build() {
      return new ConstantDelayReconnector(Math.toIntExact(wait.toMilliseconds()));
   }

}
