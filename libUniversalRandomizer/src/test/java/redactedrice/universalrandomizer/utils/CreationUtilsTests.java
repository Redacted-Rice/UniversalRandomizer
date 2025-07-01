package redactedrice.universalrandomizer.utils;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import support.SumableComparableObject;

class CreationUtilsTests {

    void assertScoCollectionEquals(Collection<Integer> expected,
            Collection<SumableComparableObject> found) {
        assertEquals(expected.size(), found.size());
        Iterator<Integer> expItr = expected.iterator();
        Iterator<SumableComparableObject> foundItr = found.iterator();
        while (expItr.hasNext()) {
            assertEquals(expItr.next(), foundItr.next().intVal);
        }
    }

    @Test
    void createRange() {
        Collection<Integer> expected = List.of(0, 3, 6, 9, 12);

        Collection<SumableComparableObject> rangeSco = CreationUtils.createRange(
                new SumableComparableObject(0), new SumableComparableObject(12),
                new SumableComparableObject(3));
        assertScoCollectionEquals(expected, rangeSco);

        rangeSco = CreationUtils.createRange(new SumableComparableObject(0),
                new SumableComparableObject(13), new SumableComparableObject(3),
                (Comparator<SumableComparableObject>) (lhs, rhs) -> lhs.compareTo(rhs));
        assertScoCollectionEquals(expected, rangeSco);

        Collection<Integer> range = CreationUtils.createRange(0, 12, 3, (i1, i2) -> i1 + i2);
        assertIterableEquals(expected, range);

        range = CreationUtils.createRange(0, 13, 3, (lhs, rhs) -> lhs.compareTo(rhs),
                (i1, i2) -> i1 + i2);
        assertIterableEquals(expected, range);
    }
}
