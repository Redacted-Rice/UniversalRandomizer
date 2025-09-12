package redactedrice.universalrandomizer.randomize;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.pool.EliminatePool;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetterNoReturn;
import redactedrice.universalrandomizer.userobjectapis.Setter;
import support.SimpleObject;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class GroupRandomizerTests {

    static final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
    static final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
    static final Integer NON_EXISTING_VAL = 7;

    @Test
    void create() {
        MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> {
            o.intField = v;
            return true;
        };
        Setter<SimpleObject, Integer> setter = (o, v) -> {
            o.intField = v;
            return true;
        };
        Getter<Collection<SimpleObject>, Integer> count2Getter = o -> 2;

        // create(MultiSetter<O2, S2> setter, Getter<T2, Integer> countGetter, EnforceParams<T2>
        // enforce)
        Randomizer<Collection<SimpleObject>, Collection<Integer>> rr = GroupRandomizer.create(ms,
                count2Getter);
        assertEquals(ms, ((GroupSetterWrapper<?, ?, ?, ?>) rr.getSetter()).getSetter());
        assertEquals(count2Getter, rr.getCountGetter());
        assertEquals(2, rr.getCountGetter().get(null));
        assertNull(rr.getPool());
        assertNull(rr.getMultiPool());
        assertNull(rr.getRandom());

        // create(MultiSetter<O2, S2> setter, int count, EnforceParams<T2> enforce)
        rr = GroupRandomizer.create(ms, 1);
        assertEquals(ms, ((GroupSetterWrapper<?, ?, ?, ?>) rr.getSetter()).getSetter());
        assertEquals(1, rr.getCountGetter().get(null));
        assertNull(rr.getPool());
        assertNull(rr.getMultiPool());
        assertNull(rr.getRandom());

        // create(Setter<O2, S2> setter, EnforceParams<T2> enforce)
        SimpleObject test = new SimpleObject("test", 0);
        rr = GroupRandomizer.create(setter);
        assertTrue(setter.setReturn(test, 1)); 	// Setter gets wrapped
        assertEquals(1, test.intField); 		// Setter gets wrapped
        assertEquals(1, rr.getCountGetter().get(null));
        assertNull(rr.getPool());
        assertNull(rr.getMultiPool());
        assertNull(rr.getRandom());
        assertNotNull(GroupRandomizer.create(ms));
    }

    @Test
    void create_badInput() {
        MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> {
            o.intField = v;
            return true;
        };
        MultiSetter<SimpleObject, Integer> msNull = null;
        Setter<SimpleObject, Integer> setterNull = null;
        Getter<Collection<SimpleObject>, Integer> count2Getter = o -> 2;
        Getter<Collection<SimpleObject>, Integer> countGetterNull = null;

        assertNull(GroupRandomizer.create(msNull, count2Getter));
        assertNull(GroupRandomizer.create(ms, countGetterNull));
        assertNull(GroupRandomizer.create(msNull, 1));
        assertNull(GroupRandomizer.create(msNull));
        assertNull(GroupRandomizer.create(setterNull));
    }

    @Test
    void GroupRandomizer() {
        final int LIST_SIZE = 5;
        final int INNER_LIST_SIZE = 3;

        // Use mock randomizer to force the excluded value to be selected
        Random rand = mock(Random.class);
        when(rand.nextInt(anyInt())).thenReturn(0);

        List<List<SimpleObject>> soListList = new LinkedList<>();
        Map<String, List<String>> vals = new HashMap<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            List<SimpleObject> innerList = new LinkedList<>();
            List<String> innerVals = new LinkedList<>();
            for (int inner = 0; inner < INNER_LIST_SIZE; inner++) {
                if (i < LIST_SIZE) {
                    innerList.add(new SimpleObject("test" + (i * 100 + inner), i * 100 + inner));
                }
                innerVals.add("" + (inner + i * INNER_LIST_SIZE));
            }
            if (i < LIST_SIZE) {
                soListList.add(innerList);
            }
            vals.put(innerVals.get(0), innerVals);
        }

        EliminatePool<List<String>> pool = EliminatePool.create(vals.values());

        MultiSetter<SimpleObject, String> setMapEntryButNotVal11 = (so, val, cnt) -> {
            if (val.equals("11")) {
                return false;
            }
            so.setMapEntry(val, cnt);
            return true;
        };

        Randomizer<List<SimpleObject>, List<String>> test = GroupRandomizer
                .create(setMapEntryButNotVal11);

        // Perform test and check results
        assertFalse(test.perform(soListList.stream(), pool, rand));
        pool.reset();

        MultiSetterNoReturn<SimpleObject, String> setMapEntry = SimpleObject::setMapEntry;
        test = GroupRandomizer.create(setMapEntry.asMultiSetter());
        assertTrue(test.perform(soListList.stream(), pool, rand));
        for (List<SimpleObject> soList : soListList) {
            List<String> expectedVals = vals.remove(soList.get(0).getMap().get(0));
            assertNotNull(expectedVals,
                    "Failed to find key for so in expected vals: " + soList.get(0).getMap().get(0));
            for (int i = 0; i < soList.size(); i++) {
                assertEquals(expectedVals.get(i), soList.get(i).getMap().get(0),
                        soList.get(i).getMap().get(0) + " value " + i + " not found in set");
            }
        }
        assertTrue(vals.isEmpty());
    }
}
