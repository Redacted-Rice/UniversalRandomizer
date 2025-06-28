package redactedrice.universalrandomizer.pool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
class ReusePoolTests {

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
	
	public <T> void assertPoolEquals(Map<T, Integer> expected, ReusePool<T> found)
	{
		for (Entry<T, Integer> pair : expected.entrySet())
		{
			int foundCount = found.instancesOf(pair.getKey());
			assertEquals(pair.getValue(), foundCount, "Found " + foundCount + " instances of " + 
					pair.getKey() + " in pool but expected to find " + pair.getValue());
		}
	}
	
	@Test
	void create_array() 
	{
		//Array
		ReusePool<Integer> nonDup = ReusePool.create(NON_DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);
		assertEquals(EXPECTED_NON_DUPLICATE.size(), nonDup.getPool().size());
		
		ReusePool<Integer> nonDupFromDup = ReusePool.createNoDups(DUPLICATE_ARRAY);
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);
		assertEquals(EXPECTED_NON_DUPLICATE.size(), nonDupFromDup.getPool().size());
	}
	
	@Test
	void create_collection() 
	{
		//Collection
		ReusePool<Integer> nonDup = ReusePool.create(Arrays.asList(NON_DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDup);
		
		ReusePool<Integer> dup = ReusePool.create(Arrays.asList(DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_DUPLICATE, dup);
		
		ReusePool<Integer> nonDupFromDup = ReusePool.createNoDups(Arrays.asList(DUPLICATE_ARRAY));
		assertPoolEquals(EXPECTED_NON_DUPLICATE, nonDupFromDup);
		
		// Bad input
		assertNull(ReusePool.create((Collection<Integer>)null));
		assertNull(ReusePool.createNoDups((Collection<Integer>)null));
	}

	@Test
	void copy() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		ReusePool<Integer> pool = ReusePool.create(NON_DUPLICATE_VALS);
		
		pool.get(rand);
		pool.get(rand);
		pool.get(rand);
		
		ArrayList<Integer> poolList = pool.getPool();
		
		ReusePool<Integer> copy = pool.copy();
		assertIterableEquals(poolList, copy.getPool());
		
		copy.reset();
		assertIterableEquals(NON_DUPLICATE_VALS, copy.getPool());
	}
	
	@Test
	void size_onCreation() 
	{
		ReusePool<Integer> empty = ReusePool.createEmpty();
		assertEquals(0, empty.size(), "size returned non zero for empty pool");
		
		ReusePool<Integer> single = ReusePool.create(1);
		assertEquals(1, single.size(), "size returned wrong size for single item pool");

		ReusePool<Integer> pool = ReusePool.create(NON_DUPLICATE_VALS);
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool");
	}
	
	@Test
	void reset() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		ReusePool<Integer> pool = ReusePool.create(NON_DUPLICATE_VALS);
		
		pool.get(rand);
		pool.get(rand);
		pool.get(rand);
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool");
		
		pool.reset();
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool after reset");
		assertPoolEquals(EXPECTED_NON_DUPLICATE, pool);
	}
	
	@Test
	void get() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		ReusePool<Integer> pool = ReusePool.create(NON_DUPLICATE_VALS);
		int found = pool.get(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(0), found, "peek did not return value based on passed Random");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size did not return the full pool size");
		
		// Test a more gets list (and that they don't remove)
		pool.get(rand);
		pool.get(rand);
		found = pool.get(rand);
		assertEquals(NON_DUPLICATE_VALS.get(0), found, "peek did not return value based on passed Random");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size changed when it should not have been removed");
	}
	
	@Test
	void get_lastItem() 
	{
		// Test the case of the last item in the pool
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1);
		
		ReusePool<Integer> pool = ReusePool.create(NON_DUPLICATE_VALS);
		int found = pool.get(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found, "peek did not return value based on passed Random");
		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size did not return the full pool size");
	}
	
	@Test
	void peek_badCases() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		ReusePool<Integer> pool = ReusePool.create(NON_DUPLICATE_VALS);

		assertNull(pool.get(null));
		
	}
	
	@Test
	void instancesOf() 
	{
		final int NOT_IN_POOL = -100;
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		ReusePool<Integer> pool = ReusePool.create(DUPLICATE_VALS);
		
		assertEquals(DUPLICATE_VALS.get(0), pool.get(rand), "peek did not return value based on passed Random");

		assertEquals(3, pool.instancesOf(1));
		assertEquals(1, pool.instancesOf(-4));
		assertEquals(2, pool.instancesOf(5));
		assertEquals(1, pool.instancesOf(99));
		assertEquals(0, pool.instancesOf(NOT_IN_POOL));
		
		pool.get(rand);
		pool.get(rand);
		pool.get(rand);
		
		assertEquals(3, pool.instancesOf(1));
		assertEquals(1, pool.instancesOf(-4));
		assertEquals(2, pool.instancesOf(5));
		assertEquals(1, pool.instancesOf(99));
		assertEquals(0, pool.instancesOf(NOT_IN_POOL));
	}
	
	@Test
	void useNextPool() 
	{
		ReusePool<Integer> pool = ReusePool.create(NON_DUPLICATE_ARRAY);
		assertFalse(pool.useNextPool());
	}
}
