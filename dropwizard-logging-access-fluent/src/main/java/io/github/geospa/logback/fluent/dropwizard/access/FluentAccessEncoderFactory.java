package io.github.geospa.logback.fluent.dropwizard.access;

import static java.util.Optional.ofNullable;

import ch.qos.logback.access.spi.IAccessEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.access.AccessEventField;
import io.github.geospa.logback.fluent.access.FluentV0AccessEncoder;
import io.github.geospa.logback.fluent.access.FluentV1AccessEncoder;
import io.github.geospa.logback.fluent.dropwizard.FluentEncoderFactory;

public abstract class FluentAccessEncoderFactory implements FluentEncoderFactory<IAccessEvent, AccessEventField> {

   protected final ImmutableSet<AccessEventField> fields;

   protected FluentAccessEncoderFactory(ImmutableSet<AccessEventField> fields) {
      this.fields = ofNullable(fields).orElse(AccessEventField.COMBINED);
   }

   @JsonTypeName("v0-access")
   public static class V0 extends FluentAccessEncoderFactory {

      @JsonCreator
      public V0(
         @JsonProperty("fields") ImmutableSet<AccessEventField> fields
      ) {
         super(fields);
      }

      @Override
      public FluentV0AccessEncoder build(String appName) {
         return new FluentV0AccessEncoder(fields);
      }
   }

   @JsonTypeName("v1-access")
   public static class V1 extends FluentAccessEncoderFactory {

      @JsonCreator
      public V1(
         @JsonProperty("fields") ImmutableSet<AccessEventField> fields
      ) {
         super(fields);
      }

      @Override
      public FluentV1AccessEncoder build(String appName) {
         return new FluentV1AccessEncoder(fields);
      }
   }
}
