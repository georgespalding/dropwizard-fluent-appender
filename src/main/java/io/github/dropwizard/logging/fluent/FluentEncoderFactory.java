package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface FluentEncoderFactory extends Discoverable {

   FluentEncoder build(String applicationName);
}
