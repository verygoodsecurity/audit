package com.verygood.security.track;

import com.google.common.base.Objects;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;

class Utils {

  private Utils() {

  }

  static boolean areEqualOrCompareEqual(Object oldValue, Object newValue) {
    return Objects.equal(oldValue, newValue)
        || compareEquals(oldValue, newValue);
  }

  private static boolean compareEquals(Object first, Object second) {
    return first instanceof Comparable && second instanceof Comparable
        && ObjectUtils.compare((Comparable) first, (Comparable) second) == 0;
  }

  //return true when: first = null, second = empty collection or vice versa
  static boolean areEqualEmptyCollection(Object first, Object second) {
    return (first == null && second instanceof Collection && ((Collection) second).isEmpty())
        || (second == null && first instanceof Collection && ((Collection) first).isEmpty());
  }
}
