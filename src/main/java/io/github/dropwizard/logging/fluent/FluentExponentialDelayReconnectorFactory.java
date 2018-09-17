package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.fluentd.logger.sender.ExponentialDelayReconnector;

@JsonTypeName("exponential-delay")
public class FluentExponentialDelayReconnectorFactory implements FluentReconnectorFactory {

   public ExponentialDelayReconnector build() {
      return new ExponentialDelayReconnector();
   }

}
