package io.github.geospa.logback.fluent.access;

import static java.util.Optional.ofNullable;

import ch.qos.logback.access.spi.IAccessEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.FluentEncoder;

@JsonTypeName("v1-access")
public class FluentV1AccessEncoder extends FluentEncoder<IAccessEvent, AccessEventField> {

   @JsonCreator
   public FluentV1AccessEncoder(
      @JsonProperty("fields") ImmutableSet<AccessEventField> fields
   ) {
      super(
         IAccessEvent::getTimeStamp,
         V1,
         ofNullable(fields).orElse(AccessEventField.COMBINED));
   }
}
