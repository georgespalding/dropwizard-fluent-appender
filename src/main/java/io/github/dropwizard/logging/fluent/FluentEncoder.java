package io.github.dropwizard.logging.fluent;

import java.io.IOException;

public interface FluentEncoder {

   byte[] encodeFluentEvent(long timestampMillis, Object event) throws IOException;
}
