package io.vgs.track.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an {@link javax.persistence.Entity} as trackable.  Any fields within the entity that are
 * annotated with {@link Tracked} are tracked.  If the entity itself is marked as {@link Tracked},
 * then all fields not annotated with {@link NotTracked} are tracked.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Trackable {

}
