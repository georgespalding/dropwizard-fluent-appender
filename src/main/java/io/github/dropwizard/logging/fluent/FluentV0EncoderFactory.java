package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("v0")
public class FluentV0EncoderFactory extends FluentBaseEncoderFactory {

   @JsonCreator
   protected FluentV0EncoderFactory(@JsonProperty("tag") String tag) {
      super(tag);
   }

   @Override
   public FluentV0Encoder build(String applicationName) {
      return new FluentV0Encoder(tag.orElse("dropwizard." + applicationName));
   }
}
