package redactedrice.universalrandomizer.randomize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Iterator;
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
class SingleAsSetTest {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;
	
	
	@Test
	void asSetRandomizer() 
	{
		final int LIST_SIZE = 5;
		final int INNER_LIST_SIZE = 3;
		
		// Use mock randomizer to force the excluded value to be selected
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		List<List<SimpleObject>> soListList = new LinkedList<>();
		Map<String, List<String>> vals = new HashMap<>();
		for (int i = 0; i < LIST_SIZE; i++)
		{
			List<SimpleObject> innerList = new LinkedList<>();
			List<String> innerVals = new LinkedList<>();
			for (int inner = 0; inner < INNER_LIST_SIZE; inner++)
			{
				if (i < LIST_SIZE)
				{
					innerList.add(new SimpleObject("test" + (i * 100 + inner), i * 100 + inner));
				}
				innerVals.add("" + (inner + i * INNER_LIST_SIZE));
			}
			if (i < LIST_SIZE)
			{
				soListList.add(innerList);
			}
			vals.put(innerVals.get(0), innerVals);
		}
		
		EliminatePool<List<String>> pool = EliminatePool.create(vals.values());

		MultiSetter<SimpleObject, String> setMapEntryButNotVal11 = (so, val, cnt) -> {
			if (val.equals("11"))
			{
				return false;
			}
			so.setMapEntry(val, cnt);
			return true;
		};
		MultiSetter<List<SimpleObject>, List<String>> wrapper = (objs, poolValue, cnt) -> {
			boolean success = true;
			Iterator<SimpleObject> objItr = objs.iterator();
			Iterator<String> valItr = poolValue.iterator();
			while (objItr.hasNext() && success)
			{
				SimpleObject obj = objItr.next();
				success = setMapEntryButNotVal11.setReturn(obj, valItr.next(), cnt);
			}
			return success;
		};
		
		SingleRandomizer<List<SimpleObject>, List<String>> test = 
				SingleRandomizer.create(wrapper);

		// Perform test and check results
		assertFalse(test.perform(soListList.stream(), pool, rand));
		pool.reset();

		MultiSetterNoReturn<SimpleObject, String> setMapEntry = SimpleObject::setMapEntry;
		MultiSetter<List<SimpleObject>, List<String>> wrapper2 = (objs, poolValue, cnt) -> {
			boolean success = true;
			Iterator<SimpleObject> objItr = objs.iterator();
			Iterator<String> valItr = poolValue.iterator();
			while (objItr.hasNext() && success)
			{
				SimpleObject obj = objItr.next();
				success = setMapEntry.asMultiSetter().setReturn(obj, valItr.next(), cnt);
			}
			return success;
		};
		test = SingleRandomizer.create(wrapper2);
		assertTrue(test.perform(soListList.stream(), pool, rand));
		for (List<SimpleObject> soList : soListList)
		{
			List<String> expectedVals = vals.remove(soList.get(0).getMap().get(0));
			assertNotNull(expectedVals, "Failed to find key for so in expected vals: " + soList.get(0).getMap().get(0));
			for (int i = 0; i < soList.size(); i++)
			{
				assertEquals(expectedVals.get(i), soList.get(i).getMap().get(0), 
						soList.get(i).getMap().get(0) + " value " + i + " not found in set");
			}
		}
		assertTrue(vals.isEmpty());
	}
}
