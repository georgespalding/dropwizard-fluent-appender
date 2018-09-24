package io.github.dropwizard.logging.fluent;

import java.util.Optional;

public abstract class FluentBaseEncoderFactory implements FluentEncoderFactory {

   protected final Optional<String> tag;

   protected FluentBaseEncoderFactory(String tag) {this.tag = Optional.ofNullable(tag);}

}
