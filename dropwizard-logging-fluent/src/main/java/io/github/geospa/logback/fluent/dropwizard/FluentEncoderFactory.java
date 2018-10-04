package io.github.geospa.logback.fluent.dropwizard;

import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.geospa.logback.fluent.EventField;
import io.github.geospa.logback.fluent.FluentEncoder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface FluentEncoderFactory<E extends DeferredProcessingAware,EF extends EventField<E>> {
                public FluentEncoder<E,EF> build(String appName);
}
