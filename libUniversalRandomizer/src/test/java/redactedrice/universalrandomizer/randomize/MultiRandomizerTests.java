package redactedrice.universalrandomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.pool.EliminatePool;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetterNoReturn;
import support.SimpleObject;

// Tests the Randomizer Reuse class and by extension the Randomizer class since the
// reuse class is the most simple of the classes
class MultiRandomizerTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	@Test
	void create() 
	{
    	MultiSetter<SimpleObject, Integer> ms = (o, v, cnt) -> { o.intField = v; return true; };
    	
    	// create(MultiSetter<T2, S2> setter, EnforceParams<T2> enforce)
    	MultiRandomizer<SimpleObject, List<Integer>, Integer> rr = 
    			MultiRandomizer.create(ms);
    	assertEquals(ms, rr.getSetter());
    	assertEquals(1, rr.getCountGetter().get(null));
    	assertNull(rr.getPool());
    	assertNull(rr.getMultiPool());
    	assertNull(rr.getRandom());
	}
	
	@Test
	void create_badInput() 
	{
    	MultiSetter<SimpleObject, Integer> msNull = null;
    	
    	assertNull(MultiRandomizer.create(msNull));
	}
	
	@Test
	void multiRandomizer() 
	{
		final int LIST_SIZE = 5;
		
		// Use mock randomizer to force the excluded value to be selected
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		// Set expectations
		Map<Integer, List<String>> valsMap = new HashMap<>();
		List<SimpleObject> list = new LinkedList<>();
		for (int i = 0; i < LIST_SIZE; i++)
		{
			valsMap.put(i, List.of("" + i, "" + (i + LIST_SIZE)));
			if (i < LIST_SIZE)
			{
				list.add(new SimpleObject("test" + i, i));
			}
		}
		
		EliminatePool<List<String>> pool = EliminatePool.create(valsMap.values());
		MultiSetter<SimpleObject, String> setMapEntryButNotVal5 = (so, val, cnt) -> {
			if (val.equals("5"))
			{
				return false;
			}
			so.setMapEntry(val, cnt);
			return true;
		};
		MultiRandomizer<SimpleObject, List<String>, String> test = MultiRandomizer.create(
				setMapEntryButNotVal5);

		// Perform test and check results
		assertFalse(test.perform(list.stream(), pool, rand));
		pool.reset();

		MultiSetterNoReturn<SimpleObject, String> setMapEntry = SimpleObject::setMapEntry;
		test = MultiRandomizer.create(setMapEntry.asMultiSetter());
		assertTrue(test.perform(list.stream(), pool, rand));
		for (SimpleObject so : list)
		{
			assertEquals(2, so.getMap().size());
			int key = Integer.parseInt(so.getMap().get(0));
			List<String> expectedVals = valsMap.remove(key);
			assertNotNull(expectedVals, "Failed to find key for so in expected vals: " + key);
			assertEquals(expectedVals.get(0), so.getMap().get(0), so.getMap().get(1) + " value 1 not found in set");
			assertEquals(expectedVals.get(1), so.getMap().get(1), so.getMap().get(2) + " value 2 not found in set");
		}
		assertTrue(valsMap.isEmpty());
	}
}
