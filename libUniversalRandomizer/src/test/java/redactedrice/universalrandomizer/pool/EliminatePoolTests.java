package redactedrice.universalrandomizer.pool;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.jupiter.api.Test;

@SuppressWarnings("serial")
class EliminatePoolTests {

    static final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
    static final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
    static final Integer NON_EXISTING_VAL = 7;

    static final Integer[] NON_DUPLICATE_ARRAY = (Integer[]) NON_DUPLICATE_VALS
            .toArray(new Integer[0]);
    static final Integer[] DUPLICATE_ARRAY = (Integer[]) DUPLICATE_VALS.toArray(new Integer[0]);

    static final Map<Integer, Integer> EXPECTED_NON_DUPLICATE = Collections
            .unmodifiableMap(new HashMap<Integer, Integer>() {
                {
                    put(-4, 1);
                    put(1, 1);
                    put(5, 1);
                    put(99, 1);
                }
            });

    static final Map<Integer, Integer> EXPECTED_DUPLICATE = Collections
            .unmodifiableMap(new HashMap<Integer, Integer>() {
                {
                    put(-4, 1);
                    put(1, 3);
                    put(5, 2);
                    put(99, 1);
                }
            });

    public <T> void assertPoolEquals(Map<T, Integer> expected, EliminatePool<T> found) {
        for (Entry<T, Integer> pair : expected.entrySet()) {
            int foundCount = found.instancesOf(pair.getKey());
            assertEquals(pair.getValue(), foundCount, "Found " + foundCount + " instances of "
                    + pair.getKey() + " in pool but expected to find " + pair.getValue());
        }
    }

    @Test
    void create_array() {
        // Array
        EliminatePool<Integer> nonDup = EliminatePool.create(NON_DUPLICATE_ARRAY);
        assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);
        assertEquals(EXPECTED_NON_DUPLICATE.size(), nonDup.getPool().size());
        assertTrue(nonDup.getRemoved().isEmpty());

        EliminatePool<Integer> dup = EliminatePool.create(DUPLICATE_ARRAY);
        assertPoolEquals(EXPECTED_DUPLICATE, dup);
        assertEquals(DUPLICATE_ARRAY.length, dup.getPool().size());
        assertTrue(dup.getRemoved().isEmpty());

        EliminatePool<Integer> nonDupFromDup = EliminatePool.createNoDups(DUPLICATE_ARRAY);
        assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);
        assertEquals(EXPECTED_NON_DUPLICATE.size(), nonDupFromDup.getPool().size());
        assertTrue(nonDupFromDup.getRemoved().isEmpty());
    }

    @Test
    void create_collection() {
        // Collection
        EliminatePool<Integer> nonDup = EliminatePool.create(Arrays.asList(NON_DUPLICATE_ARRAY));
        assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);

        EliminatePool<Integer> dup = EliminatePool.create(Arrays.asList(DUPLICATE_ARRAY));
        assertPoolEquals(EXPECTED_DUPLICATE, dup);

        EliminatePool<Integer> nonDupFromDup = EliminatePool
                .createNoDups(Arrays.asList(DUPLICATE_ARRAY));
        assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);

        // Bad input
        assertNull(EliminatePool.create((Collection<Integer>) null));
        assertNull(EliminatePool.createNoDups((Collection<Integer>) null));
    }

    @Test
    void copy() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_VALS);

        pool.get(rand);
        pool.get(rand);
        pool.get(rand);

        ArrayList<Integer> poolList = new ArrayList<>(pool.getPool());
        ArrayList<Integer> removedList = new ArrayList<>(pool.getRemoved());

        EliminatePool<Integer> copy = pool.copy();
        assertIterableEquals(poolList, copy.getPool());
        assertIterableEquals(removedList, copy.getRemoved());

        copy.reset();

        assertEquals(NON_DUPLICATE_VALS.size(), copy.getPool().size());
        assertTrue(copy.getRemoved().isEmpty());
    }

    @Test
    void size() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> empty = EliminatePool.createEmpty();
        assertEquals(0, empty.size(), "size returned non zero for empty pool");

        EliminatePool<Integer> single = EliminatePool.create(1);
        assertEquals(1, single.size(), "size returned wrong size for single item pool");
        assertEquals(0, single.getRemoved().size(),
                "unpeekedSize returned wrong size for single item pool");

        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_VALS);
        assertEquals(NON_DUPLICATE_VALS.size(), pool.size(),
                "size returned wrong size for item pool");
        assertEquals(0, pool.getRemoved().size(), "unpeekedSize returned wrong size for item pool");

        pool.get(rand);
        assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.size(),
                "size returned wrong size for item pool");
        assertEquals(1, pool.getRemoved().size(), "unpeekedSize returned wrong size for item pool");
    }

    @Test
    void reset() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_VALS);

        pool.get(rand);
        pool.get(rand);
        pool.get(rand);
        assertEquals(NON_DUPLICATE_VALS.size() - 3, pool.size(),
                "size returned wrong size for item pool");
        assertEquals(3, pool.getRemoved().size(), "unpeekedSize returned wrong size for item pool");

        pool.reset();
        assertEquals(NON_DUPLICATE_VALS.size(), pool.size(),
                "size returned wrong size for item pool");
        assertEquals(0, pool.getRemoved().size(), "unpeekedSize returned wrong size for item pool");

        assertPoolEquals(EXPECTED_NON_DUPLICATE, pool);
    }

    @Test
    void get() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_VALS);
        int found = pool.get(rand);

        assertEquals(NON_DUPLICATE_VALS.get(0), found,
                "peek did not return value based on passed Random");
        assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.size(),
                "size did not return the full pool size");
        assertEquals(1, pool.getRemoved().size(), "unpeekedSize did not reflect peeked value");

        int found2 = pool.get(rand);
        assertNotEquals(found, found2, "peek did not remove item from pool");
        assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.size(),
                "size did not return the full pool size");
        assertEquals(2, pool.getRemoved().size(), "unpeekedSize did not reflect peeked value");
    }

    @Test
    void get_lastItem() {
        // Test the case of the last item in the pool
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1)
                .thenReturn(NON_DUPLICATE_VALS.size() - 2);

        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_VALS);
        int found = pool.get(rand);

        assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found,
                "peek did not return value based on passed Random");
        assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.size(),
                "size did not return the full pool size");
        assertEquals(1, pool.getRemoved().size(), "unpeekedSize did not reflect peeked value");

        int found2 = pool.get(rand);
        assertNotEquals(found, found2, "peek did not remove item from pool");
        assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.size(),
                "size did not return the full pool size");
        assertEquals(2, pool.getRemoved().size(), "unpeekedSize did not reflect peeked value");
    }

    @Test
    void get_badCases() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_VALS);

        assertNull(pool.get(null));

        // Exhaust the pool
        for (int i = 0; i < NON_DUPLICATE_VALS.size(); i++) {
            assertNotNull(pool.get(rand), "Peek returned null when it should have items still");
        }
        assertEquals(0, pool.size(), "size did not return the full pool size");

        // then do one more peek
        assertNull(pool.get(rand), "Peek did not return null when it was empty");

    }

    @Test
    void instancesOf() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> pool = EliminatePool.create(DUPLICATE_VALS);

        // 1, -4, 5, 1, 99, 1, 5
        final int NOT_IN_POOL = -100;
        assertEquals(3, pool.instancesOf(1));
        assertEquals(1, pool.instancesOf(-4));
        assertEquals(2, pool.instancesOf(5));
        assertEquals(1, pool.instancesOf(99));
        assertEquals(0, pool.instancesOf(NOT_IN_POOL));

        pool.get(rand);

        assertEquals(2, pool.instancesOf(1));
        assertEquals(1, pool.instancesOf(-4));
        assertEquals(2, pool.instancesOf(5));
        assertEquals(1, pool.instancesOf(99));
        assertEquals(0, pool.instancesOf(NOT_IN_POOL));
    }

    @Test
    void useNextPool() {
        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_ARRAY);
        assertFalse(pool.useNextPool());
    }
}
