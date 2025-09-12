package redactedrice.universalrandomizer.pool;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

@SuppressWarnings("serial")
class ElimatePoolSetTests {

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

    @Test
    void create() {
        EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);

        assertNotNull(EliminatePoolSet.create(base, 3));
        assertNotNull(EliminatePoolSet.createNoAdditionalPools(base));

        assertNull(EliminatePoolSet.create(null, 3));
        assertNull(EliminatePoolSet.createNoAdditionalPools(null));
    }

    @Test
    void reset() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> pool = EliminatePool.create(NON_DUPLICATE_VALS);

        // Get some values
        int found = pool.get(rand);
        int found2 = pool.get(rand);
        // ensure they were removed
        assertFalse(pool.getPool().contains(found));
        assertFalse(pool.getPool().contains(found2));

        // reset it
        pool.reset();

        // ensure the values were added back - we can't redo because pool doesn't preserve internal
        // order when resetting
        assertTrue(pool.getPool().contains(found));
        assertTrue(pool.getPool().contains(found2));
    }

    @Test
    void get() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);
        EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);

        int found = pool.get(rand);

        assertEquals(NON_DUPLICATE_VALS.get(0), found,
                "Get did not return value based on passed Random");

        int found2 = pool.get(rand);
        assertNotEquals(found, found2, "Get did not remove item from pool");
    }

    @Test
    void get_lastItem() {
        // Test the case of the last item in the pool
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1)
                .thenReturn(NON_DUPLICATE_VALS.size() - 2);

        EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);
        EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);

        int found = pool.get(rand);

        assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found,
                "Get did not return value based on passed Random");

        int found2 = pool.get(rand);
        assertNotEquals(found, found2, "Get did not remove item from pool");
    }

    @Test
    void get_badCases() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);
        EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);

        assertNull(pool.get(null));

        // Exhaust the pool
        for (int i = 0; i < NON_DUPLICATE_VALS.size(); i++) {
            assertNotNull(pool.get(rand), "Get returned null when it should have items still");
        }

        // then do one more peek
        assertNull(pool.get(rand), "Get did not return null when it was empty");
    }

    @Test
    void get_empty() {
        EliminatePool<Integer> base = EliminatePool.createEmpty();
        EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);

        assertNull(pool.get(new Random()), "Get did not return null when pool was empty");
    }

    @Test
    void useNextPool() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_ARRAY);
        EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 3);
        assertTrue(pool.useNextPool());
        assertTrue(pool.useNextPool());
        assertFalse(pool.useNextPool());
        // ensure it didn't walk past the end
        assertNotNull(pool.get(rand));

        // See that reset sets it back to the first pool
        pool.reset();

        assertNotNull(pool.get(rand));
        assertTrue(pool.useNextPool());
        assertNotNull(pool.get(rand));
        assertNotNull(pool.get(rand));
        assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.getWorkingPools().get(0).size(),
                "size did not return the full pool size");
        assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.getWorkingPools().get(1).size(),
                "size did not return the full pool size");

        // Finish out using next pools
        assertTrue(pool.useNextPool());
        assertFalse(pool.useNextPool());

        EliminatePoolSet<Integer> pool2 = EliminatePoolSet.create(base, 1);
        assertFalse(pool2.useNextPool());
        // ensure it didn't walk past the end
        assertNotNull(pool2.get(new Random()));
    }
}
