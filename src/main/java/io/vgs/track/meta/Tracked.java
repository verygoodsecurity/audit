package io.vgs.track.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Tracked {
  String DEFAULT_REPLACE = "HIDDEN";
  boolean replace() default false;
  String replaceWith() default DEFAULT_REPLACE;
}
