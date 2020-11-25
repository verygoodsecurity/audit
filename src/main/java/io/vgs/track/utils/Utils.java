package io.vgs.track.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public final class Utils {

  private static class Foo implements Comparator<Object> {

    @Override
    public int compare(final Object o1, final Object o2) {
      Comparable<Object> c1 = (Comparable<Object>) o1;
      Comparable<Object> c2 = (Comparable<Object>) o2;
      return c1.compareTo(c2);
    }
  }

  private Utils() {
  }

  public static boolean equalsOrCompareEquals(Object oldValue, Object newValue) {
    return Objects.equals(oldValue, newValue) || compareEquals(oldValue, newValue);
  }

  private static boolean compareEquals(Object first, Object second) {
    return first instanceof Comparable && second instanceof Comparable
        && Objects.compare(first, second, new Foo()) == 0;
  }

  //return true when: first = null, second = empty collection or vice versa
  public static boolean collectionsEqual(Collection first, Collection second) {
    return (first == null && second.isEmpty()) || (second == null && first.isEmpty());
  }
}
