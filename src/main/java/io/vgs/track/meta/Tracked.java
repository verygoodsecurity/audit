package io.vgs.track.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields of an {@link javax.persistence.Entity} as trackable.
 * <p>
 * If applied to a field, marks that field as trackable.  If applied to a class, marks all fields as trackable except
 * for those explicitly annotated with {@link NotTracked}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Tracked {

  String DEFAULT_REPLACE = "HIDDEN";

  /**
   * Whether the field value should be redacted.
   *
   * @return {@code true} if the field value should be redacted
   */
  boolean replace() default false;

  /**
   * The value that will be substituted in place of the real value if {@link #replace()} is {@code true}.
   *
   * @return the string to use instead of the actual value
   */
  String replaceWith() default DEFAULT_REPLACE;
}
