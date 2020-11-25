package io.vgs.track.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an {@link javax.persistence.Entity} field as omitted from tracking.  This overrides a
 * {@link Tracked} annotation applied at the class level.  It is an error for a field to be annotated
 * with both {@link Tracked} and {@link NotTracked} simultaneously.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotTracked {

}
