package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.fluentd.logger.sender.NullSender;

@JsonTypeName("null")
public class FluentNullSenderFactory implements FluentSenderFactory {

   public NullSender build() {
      return new NullSender("null", 0, 0, 0);
   }

}
