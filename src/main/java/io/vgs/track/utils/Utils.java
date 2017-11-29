package io.vgs.track.utils;

import com.google.common.base.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;

public class Utils {

  private Utils() {

  }

  public static boolean equalsOrCompareEquals(Object oldValue, Object newValue) {
    return Objects.equal(oldValue, newValue)
        || compareEquals(oldValue, newValue)
        || areEqualCollection(oldValue, newValue);
  }

  private static boolean compareEquals(Object first, Object second) {
    return first instanceof Comparable && second instanceof Comparable
        && ObjectUtils.compare((Comparable) first, (Comparable) second) == 0;
  }

  public static boolean areEqualCollection(Object first, Object second) {
    return (first instanceof Collection) && (second instanceof Collection)
        && CollectionUtils.isEqualCollection((Collection) first, (Collection) second);
  }
}
