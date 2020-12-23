package io.vgs.track.utils;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public final class MemberUtils {

  private MemberUtils() {
  }

  /**
   * Returns whether a {@link Member} is accessible.
   *
   * @param m Member to check
   * @return {@code true} if {@code m} is accessible
   */
  static boolean isAccessible(final Member m) {
    return m != null && Modifier.isPublic(m.getModifiers()) && !m.isSynthetic();
  }

}
