package redactedrice.universalrandomizer.pool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import redactedrice.universalrandomizer.pool.EliminatePool;
import redactedrice.universalrandomizer.pool.EliminatePoolSet;
import redactedrice.universalrandomizer.pool.MultiPool;
import redactedrice.universalrandomizer.pool.RandomizerPool;
import redactedrice.universalrandomizer.pool.ReusePool;
import redactedrice.universalrandomizer.userobjectapis.MultiGetter;
import support.SimpleObject;

@SuppressWarnings("serial")
class MultiPoolTests {

	final List<Integer> NON_DUPLICATE_VALS = List.of(1, -4, 5, 99);
	final List<Integer> DUPLICATE_VALS = List.of(1, -4, 5, 1, 99, 1, 5);
	final Integer NON_EXISTING_VAL = 7;

	final Integer[] NON_DUPLICATE_ARRAY = (Integer[]) NON_DUPLICATE_VALS.toArray(new Integer[0]);
	final Integer[] DUPLICATE_ARRAY = (Integer[]) DUPLICATE_VALS.toArray(new Integer[0]);
	
	final Map<Integer, Integer> EXPECTED_NON_DUPLICATE = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {
	    {
	        put(-4, 1);
	        put(1, 1);
	        put(5, 1);
	        put(99, 1);
	    }
	});

	final Map<Integer, Integer> EXPECTED_DUPLICATE = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {
	    {
	        put(-4, 1);
	        put(1, 3);
	        put(5, 2);
	        put(99, 1);
	    }
	});
	
	@Test
	void create() 
	{
		ReusePool<Integer> p1 = ReusePool.create(NON_DUPLICATE_VALS);
		ReusePool<Integer> p2 = ReusePool.create(DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so, cnt) -> so.getIntField();
		assertNotNull(MultiPool.create(poolMap, soInt));
		assertNull(MultiPool.create(null, soInt));
		assertNull(MultiPool.create(poolMap, null));
	}
	

	@Test
	void setPool() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		ReusePool<Integer> p1 = ReusePool.create(NON_DUPLICATE_VALS);
		ReusePool<Integer> p2 = ReusePool.create(DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (o, cnt) -> {
			return cnt == 0 ? null : cnt;
		};
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		
		assertNull(mp.get(null));
		mp.reset();
		assertFalse(mp.useNextPool());
		
		// bad key
		assertFalse(mp.setPool(so, 0));
		// key not in map
		assertFalse(mp.setPool(so, 5));
		
		assertTrue(mp.setPool(so, 1));
	}
	
	@Test
	void reset() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		EliminatePool<Integer> p1 = EliminatePool.create(NON_DUPLICATE_VALS);
		EliminatePool<Integer> p2 = EliminatePool.create(DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		mp.reset();
		
		mp.setPool(so, 0);
		
		mp.get(rand);
		mp.get(rand);
		
		assertEquals(NON_DUPLICATE_VALS.size() - 2, p1.size(), "size returned wrong size for item pool");
		assertEquals(DUPLICATE_VALS.size(), p2.size(), "size returned wrong size for item pool");
		
		so.setIntField(2);
		mp.setPool(so, 0);
		
		mp.get(rand);

		assertEquals(NON_DUPLICATE_VALS.size() - 2, p1.size(), "size returned wrong size for item pool");
		assertEquals(DUPLICATE_VALS.size() - 1, p2.size(), "size returned wrong size for item pool");
		
		mp.reset();
		assertEquals(NON_DUPLICATE_VALS.size(), p1.size(), "size returned wrong size for item pool after reset");
		assertEquals(DUPLICATE_VALS.size(), p2.size(), "size returned wrong size for item pool after reset");
	}
	
	@Test
	void get() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		EliminatePool<Integer> p1 = EliminatePool.create(NON_DUPLICATE_VALS);
		EliminatePool<Integer> p2 = EliminatePool.create(DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);
		
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		int found = mp.get(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(0), found, "get did not return value based on passed Random");

		int found2 = mp.get(rand);
		assertNotEquals(found, found2, "get did not remove item from pool");
	}
	
	@Test
	void get_lastItem() 
	{
		// Test the case of the last item in the pool
		SimpleObject so = new SimpleObject("test", 1);
		EliminatePool<Integer> p1 = EliminatePool.create(NON_DUPLICATE_VALS);
		EliminatePool<Integer> p2 = EliminatePool.create(DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);
		
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1).thenReturn(NON_DUPLICATE_VALS.size() - 2);
		
		int found = mp.get(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found, "get did not return value based on passed Random");

		int found2 = mp.get(rand);
		assertNotEquals(found, found2, "get did not remove item from pool");
	}
	
	@Test
	void get_badCases() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		EliminatePool<Integer> p1 = EliminatePool.create(NON_DUPLICATE_VALS);
		EliminatePool<Integer> p2 = EliminatePool.create(DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);

		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		assertNull(mp.get(null));
		assertNull(mp.get(rand));
		
		mp.setPool(so, 0);

		assertNull(mp.get(null));
		
		// Exhaust the pool
		for (int i = 0; i < NON_DUPLICATE_VALS.size(); i++)
		{
			assertNotNull(mp.get(rand), "get returned null when it should have items still");
		}
		
		// then do one more peek
		assertNull(mp.get(rand), "get did not return null when it was empty");
	}
	
	@Test
	void get_empty() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		EliminatePool<Integer> p1 = EliminatePool.createEmpty();
		EliminatePool<Integer> p2 = EliminatePool.create(DUPLICATE_VALS);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);

		assertNull(mp.get(new Random()), "get did not return null when pool was empty");
	}
	
	@Test
	void useNextPool() 
	{
		SimpleObject so = new SimpleObject("test", 1);
		EliminatePool<Integer> base1 = EliminatePool.create(NON_DUPLICATE_ARRAY);
		EliminatePool<Integer> base2 = EliminatePool.create(DUPLICATE_VALS);
		EliminatePoolSet<Integer> p1 = EliminatePoolSet.create(base1, 3);
		EliminatePoolSet<Integer> p2 = EliminatePoolSet.create(base2, 1);
		Map<Integer, RandomizerPool<Integer>> poolMap = new HashMap<>();
		poolMap.put(1, p1);
		poolMap.put(2, p2);
		
		MultiGetter<SimpleObject, Integer> soInt = (so2, cnt) -> so2.getIntField();
		MultiPool<SimpleObject, Integer, Integer> mp = MultiPool.create(poolMap, soInt);
		mp.setPool(so, 0);

		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		assertTrue(mp.useNextPool());
		assertTrue(mp.useNextPool());
		assertFalse(mp.useNextPool());

		mp.reset();
		mp.get(rand);
		assertTrue(mp.useNextPool());
		assertEquals(NON_DUPLICATE_VALS.size() - 1, p1.getWorkingPools().get(0).size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size(), p1.getWorkingPools().get(1).size(), "size did not return the full pool size");
		
		// Finish out using next pools
		assertTrue(mp.useNextPool());
		assertFalse(mp.useNextPool());

		so.setIntField(2);
		mp.setPool(so, 0);
		assertFalse(mp.useNextPool());
	}
}
