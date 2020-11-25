package io.vgs.track.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public final class Utils {

    private Utils() {
    }

    public static boolean equalsOrCompareEquals(final Object oldValue, final Object newValue) {
        return Objects.equals(oldValue, newValue) || compareEquals(oldValue, newValue);
    }

    private static boolean compareEquals(final Object first, final Object second) {
        return first instanceof Comparable && second instanceof Comparable
                && Objects.compare(first, second, new Foo()) == 0;
    }

    // return true when: first = null, second = empty collection or vice versa
    public static boolean collectionsEqual(final Collection<?> first, final Collection<?> second) {
        return (first == null && second.isEmpty()) || (second == null && first.isEmpty());
    }

    private static class Foo implements Comparator<Object> {

        @SuppressWarnings("unchecked")
        @Override
        public int compare(final Object o1, final Object o2) {
            final Comparable<Object> c1 = (Comparable<Object>) o1;
            final Comparable<Object> c2 = (Comparable<Object>) o2;
            return c1.compareTo(c2);
        }
    }
}
