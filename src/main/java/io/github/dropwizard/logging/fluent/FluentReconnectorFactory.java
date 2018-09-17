package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import org.fluentd.logger.sender.Reconnector;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface FluentReconnectorFactory extends Discoverable {

   Reconnector build();
}
