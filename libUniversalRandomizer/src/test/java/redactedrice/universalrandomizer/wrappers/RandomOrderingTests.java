package redactedrice.universalrandomizer.wrappers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.testsupport.SimpleObject;

class RandomOrderingTests {

    @Test
    void create() {
        SimpleObject so = new SimpleObject("test obj", 3);
        RandomOrdering<SimpleObject> ro = RandomOrdering.create(so, 5);
        assertEquals(so, ro.getObject());
        assertEquals(5, ro.getSortingValue());
    }

    @Test
    void forceNonNull() {
        assertNull(RandomOrdering.create(null, 1));

        SimpleObject so = new SimpleObject("test obj", 3);
        RandomOrdering<SimpleObject> ro = RandomOrdering.create(so, 5);
        assertFalse(ro.setObject(null));
        assertEquals(so, ro.getObject()); // Ensure not changed

        SimpleObject so2 = new SimpleObject("test obj", 3);
        assertTrue(ro.setObject(so2));
        assertEquals(so2, ro.getObject()); // Ensure changed

        ro.setSortingValue(3);
        assertEquals(3, ro.getSortingValue());
    }

    @Test
    void sortingValue() {
        RandomOrdering<SimpleObject> ro1 = RandomOrdering.create(new SimpleObject("test obj", 3),
                1);
        RandomOrdering<SimpleObject> ro2 = RandomOrdering.create(new SimpleObject("test obj", -8),
                6);

        ro1.setSortingValue(1);
        assertEquals(1, ro1.getSortingValue());
        ro2.setSortingValue(6);
        assertEquals(6, ro2.getSortingValue());

        assertEquals(ro1, ro1.setSortingValueReturnSelf(-1));
        assertEquals(-1, ro1.getSortingValue());

        assertTrue(RandomOrdering.sortBySortingValue(ro1, ro2) < 0);
        assertTrue(RandomOrdering.sortBySortingValue(ro2, ro1) > 0);
        assertEquals(0, RandomOrdering.sortBySortingValue(ro1, ro1));
    }
}
