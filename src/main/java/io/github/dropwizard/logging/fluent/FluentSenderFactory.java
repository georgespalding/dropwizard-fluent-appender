package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import org.fluentd.logger.sender.Sender;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface FluentSenderFactory extends Discoverable {

   Sender build();
}
