package io.github.geospa.logback.fluent.access;

import static java.util.Optional.ofNullable;

import ch.qos.logback.access.spi.IAccessEvent;
import com.google.common.collect.ImmutableSet;
import io.github.geospa.logback.fluent.FluentEncoder;

public class FluentV0AccessEncoder extends FluentEncoder<IAccessEvent, AccessEventField> {

   public FluentV0AccessEncoder(
      ImmutableSet<AccessEventField> fields
   ) {
      super(
         IAccessEvent::getTimeStamp,
         V0,
         fields);
   }
}
