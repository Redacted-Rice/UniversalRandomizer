package redactedrice.universalrandomizer.randomize;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import redactedrice.universalrandomizer.pool.EliminatePool;
import redactedrice.universalrandomizer.pool.EliminatePoolSet;
import redactedrice.universalrandomizer.pool.MultiPool;
import redactedrice.universalrandomizer.pool.RandomizerPool;
import redactedrice.universalrandomizer.pool.ReusePool;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiGetter;
import redactedrice.universalrandomizer.userobjectapis.Setter;
import redactedrice.universalrandomizer.userobjectapis.SetterNoReturn;
import support.SimpleObject;
import support.SimpleObjectUtils;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class RandomizerTests {

    final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
    final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
    final Integer NON_EXISTING_VAL = 7;

    final static Setter<SimpleObject, Integer> setterInt = (o, v) -> {
        if (v == null) {
            return false;
        }
        o.intField = v;
        return true;
    };
    final static Getter<SimpleObject, Integer> getterInt = o -> o.intField;

    public static List<SimpleObject> createSimpleObjects(int number) {
        List<SimpleObject> list = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            list.add(new SimpleObject("name" + i, i * 100));
        }
        return list;
    }

    @Test
    void perform_basic() {
        final int POOL_VAL = 5;
        final int LIST_SIZE = 10;

        // Set expectations
        List<Integer> expected = new LinkedList<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            expected.add(POOL_VAL);
        }

        // Setup mocks
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        @SuppressWarnings("unchecked")
        ReusePool<Integer> pool = mock(ReusePool.class);
        when(pool.get(any())).thenReturn(POOL_VAL);
        when(pool.copy()).thenReturn(pool);

        // Create test data and object
        List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
        Randomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt);

        // Perform test and check results
        assertTrue(test.perform(list.stream(), pool, rand));
        List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
        assertIterableEquals(expected, results);
    }

    @Test
    void perform_callSignitures() {
        final int LIST_SIZE = 5;

        // Set expectations
        Set<Integer> expected = new HashSet<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            expected.add(i);
        }
        // Create test data and object
        EliminatePool<Integer> basePool = EliminatePool.create(expected);
        List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
        Randomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt);

        // Basic pool with no randomizer passed
        assertTrue(test.perform(list.stream(), basePool));
        Set<Integer> expectedBasicPool = new HashSet<>(expected);
        for (SimpleObject so : list) {
            assertTrue(expectedBasicPool.remove(so.getIntField()),
                    so.getIntField() + " not found (or already removed) in expected set");
        }

        // multi pool with no randomizer passed
        list = createSimpleObjects(LIST_SIZE);
        basePool.reset();

        Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
        poolMap.put(1, basePool);
        MultiPool<SimpleObject, Integer, Integer> multiPool = MultiPool.create(poolMap,
                (so, c) -> 1);
        assertTrue(test.perform(list.stream(), multiPool));
        for (SimpleObject so : list) {
            assertTrue(expected.remove(so.getIntField()),
                    so.getIntField() + " not found (or already removed) in expected set");
        }
    }

    @Test
    void perform_someFailed() {
        final int LIST_SIZE = 10;
        final List<Integer> POOL_VALS = Arrays.asList(0, 1, 2, null, 4, null, 6, 7, 8, null);
        final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 2, 300, 4, 500, 6, 7, 8, 900);

        // Set mocks
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        @SuppressWarnings("unchecked")
        ReusePool<Integer> pool = mock(ReusePool.class);
        when(pool.get(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
        when(pool.copy()).thenReturn(pool);

        // Create test data and object
        List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
        Randomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt);

        // Perform test and check results
        assertFalse(test.perform(list.stream(), pool, rand));
        List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
        assertIterableEquals(EXPECTED_VALS, results);
    }

    @Test
    void perform_enforce_null() {
        final int LIST_SIZE = 10;
        final List<Integer> POOL_VALS = Arrays.asList(0, 1, null, 3, 4, null, 6, 7, 8, 9);
        // 5 will be excluded by the enforce
        final List<Integer> EXPECTED_VALS = Arrays.asList(0, 1, 200, 3, 4, 500, 6, 7, 8, 9);

        // Setup mocks
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        @SuppressWarnings("unchecked")
        ReusePool<Integer> pool = mock(ReusePool.class);
        when(pool.get(any())).thenAnswer(AdditionalAnswers.returnsElementsOf(POOL_VALS));
        when(pool.copy()).thenReturn(pool);

        List<SimpleObject> list = createSimpleObjects(LIST_SIZE);
        Randomizer<SimpleObject, Integer> test = SingleRandomizer.create(setterInt);

        // Perform test and check results
        assertFalse(test.perform(list.stream(), pool, rand));
        List<Integer> results = SimpleObjectUtils.toIntFieldList(list);
        assertIterableEquals(EXPECTED_VALS, results);
    }

    @Test
    void elimatePoolRandomizer() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        final List<Integer> TEST_VALUES = List.of(1, 5, -4);
        final int REPEATS = 2;

        List<SimpleObject> objs = new LinkedList<>();
        for (int i = 0; i < TEST_VALUES.size() * REPEATS; i++) {
            objs.add(new SimpleObject("name" + i, i * 100));
        }

        EliminatePoolSet<Integer> pool = EliminatePoolSet.create(EliminatePool.create(TEST_VALUES),
                3);

        Randomizer<SimpleObject, Integer> test = SingleRandomizer
                .create(SetterNoReturn.asMultiSetter(SimpleObject::setField));

        assertTrue(test.perform(objs.stream(), pool, rand));
        for (int i = 0; i < TEST_VALUES.size(); i++) {
            final int final_i = i;
            assertEquals(REPEATS,
                    objs.stream().filter(s -> s.intField == TEST_VALUES.get(final_i)).count());
        }
    }

    @Test
    void elimatePoolRandomizer_exhaust() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        final List<Integer> TEST_VALUES = List.of(1, 5, -4);

        List<SimpleObject> objs = new LinkedList<>();
        for (int i = 0; i < 7; i++) {
            objs.add(new SimpleObject("name" + i, i * 100));
        }

        EliminatePoolSet<Integer> pool = EliminatePoolSet.create(EliminatePool.create(TEST_VALUES),
                2);

        Randomizer<SimpleObject, Integer> test = SingleRandomizer
                .create(SetterNoReturn.asMultiSetter(SimpleObject::setField));

        assertFalse(test.perform(objs.stream(), pool, rand));
    }

    @Test
    void mutlipoolRandomizer() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        Set<Integer> expected1 = new HashSet<Integer>();
        expected1.add(1);
        expected1.add(5);
        expected1.add(-4);
        expected1.add(9);
        Set<Integer> expected2 = new HashSet<Integer>();
        expected2.add(3);
        expected2.add(-1);
        expected2.add(5);
        expected2.add(0);

        Map<String, RandomizerPool<Integer>> poolMap = new HashMap<>();
        poolMap.put("name1", EliminatePool.create(List.of(1, 5, -4, 9)));
        poolMap.put("name2", EliminatePool.create(List.of(3, -1, 5, 0)));

        List<SimpleObject> objs = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            objs.add(new SimpleObject("name" + (1 + (i % 2)), i * 100));
        }

        MultiGetter<SimpleObject, String> soString = (so2, cnt) -> so2.getStringField();
        MultiPool<SimpleObject, String, Integer> pool = MultiPool.create(poolMap, soString);

        Randomizer<SimpleObject, Integer> test = SingleRandomizer
                .create(SetterNoReturn.asMultiSetter(SimpleObject::setField));

        assertTrue(test.perform(objs.stream(), pool, rand));
        assertEquals(objs.size(), expected1.size() + expected2.size(), "Bad test setup!");
        for (SimpleObject so : objs) {
            if (so.getStringField().equals("name1")) {
                assertTrue(expected1.remove(so.getIntField()),
                        so.getIntField() + " not found in set 1");
            } else if (so.getStringField().equals("name2")) {
                assertTrue(expected2.remove(so.getIntField()),
                        so.getIntField() + " not found in set 2");
            } else {
                assertTrue(false, "Bad test setup!");
            }
        }
    }

    @Test
    void mutlipoolRandomizer_exhaust() {
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        Map<String, RandomizerPool<Integer>> poolMap = new HashMap<>();
        poolMap.put("name1", EliminatePool.create(List.of(1, 5, -4, 9)));
        poolMap.put("name2", EliminatePool.create(List.of(3, -1, 5, 0)));

        List<SimpleObject> objs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            objs.add(new SimpleObject("name" + (1 + (i % 2)), i * 100));
        }

        MultiGetter<SimpleObject, String> soString = (so2, cnt) -> so2.getStringField();
        MultiPool<SimpleObject, String, Integer> pool = MultiPool.create(poolMap, soString);

        Randomizer<SimpleObject, Integer> test = SingleRandomizer
                .create(SetterNoReturn.asMultiSetter(SimpleObject::setField));

        assertFalse(test.perform(objs.stream(), pool, rand));
    }
}
