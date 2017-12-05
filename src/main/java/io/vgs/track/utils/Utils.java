package io.vgs.track.utils;

import com.google.common.base.Objects;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;

public class Utils {

  private Utils() {

  }

  public static boolean equalsOrCompareEquals(Object oldValue, Object newValue) {
    return Objects.equal(oldValue, newValue) || compareEquals(oldValue, newValue);
  }

  private static boolean compareEquals(Object first, Object second) {
    return first instanceof Comparable && second instanceof Comparable
        && ObjectUtils.compare((Comparable) first, (Comparable) second) == 0;
  }

  //return true when: first = null, second = empty collection or vice versa
  public static boolean collectionsEqual(Collection first, Collection second) {
    return (first == null && second.isEmpty()) || (second == null && first.isEmpty());
  }
}
