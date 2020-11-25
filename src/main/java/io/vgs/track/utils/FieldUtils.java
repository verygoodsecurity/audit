package io.vgs.track.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class FieldUtils {

  private FieldUtils() {
  }

  /**
     * Gets all fields of the given class and its parents (if any).
     *
     * @param cls the {@link Class} to query
     * @return an array of Fields (possibly empty).
     * @throws IllegalArgumentException if the class is {@code null}
     */
    public static List<Field> getAllFieldsList(final Class<?> cls) {
        Objects.requireNonNull(cls, "The class must not be null");
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    /**
     * Gets an accessible {@link Field} by name, breaking scope if requested. Only
     * the specified class will be considered.
     *
     * @param cls         the {@link Class} to reflect, must not be {@code null}
     * @param fieldName   the field name to obtain
     * @param forceAccess whether to break scope restrictions using the
     *                    {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    method. {@code false} will only match {@code public}
     *                    fields.
     * @return the Field object
     * @throws IllegalArgumentException if the class is {@code null}, or the field
     *                                  name is blank or empty
     */
    public static Field getDeclaredField(final Class<?> cls, final String fieldName, final boolean forceAccess) {
        Objects.requireNonNull(cls, "The class must not be null");
        boolean result = true;
        final int strLen = fieldName == null ? 0 : fieldName.length();
        if (strLen != 0) {
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(fieldName.charAt(i))) {
                    result = false;
                    break;
                }
            }
        }
        if (result) {
            throw new IllegalArgumentException("The field name must not be blank/empty");
        }
        try {
            // only consider the specified class by using getDeclaredField()
            final Field field = cls.getDeclaredField(fieldName);
            if (!MemberUtils.isAccessible(field)) {
                if (forceAccess) {
                    field.setAccessible(true);
                } else {
                    return null;
                }
            }
            return field;
        } catch (final NoSuchFieldException e) { // NOPMD
            // ignore
        }
        return null;
    }

}
