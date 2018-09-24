package io.github.dropwizard.logging.fluent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("v1")
public class FluentV1EncoderFactory extends FluentBaseEncoderFactory {

   @JsonCreator
   protected FluentV1EncoderFactory(@JsonProperty("tag") String tag) {
      super(tag);
   }

   @Override
   public FluentV1Encoder build(String applicationName) {
      return new FluentV1Encoder(tag.orElse("dropwizard." + applicationName));
   }
}
