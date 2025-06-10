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

@SuppressWarnings("serial")
class ElimatePoolSetTests {

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
		EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);
		
		assertNotNull(EliminatePoolSet.create(base, 3));
		assertNotNull(EliminatePoolSet.createNoAdditionalPools(base));
		
		assertNull(EliminatePoolSet.create(null, 3));
		assertNull(EliminatePoolSet.createNoAdditionalPools(null));
	}
	
	@Test
	void reset() 
	{
//		Random rand = mock(Random.class);
//		when(rand.nextInt(anyInt())).thenReturn(0);
//		
//		PeekPool<Integer> base = PeekPool.create(false, NON_DUPLICATE_VALS);
//		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
//		
//		pool.peek(rand);
//		pool.selectPeeked();
//		pool.peek(rand);
//		pool.selectPeeked();
//		pool.peek(rand);
//		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.size(), "size returned wrong size for item pool");
//		assertEquals(NON_DUPLICATE_VALS.size() - 3, pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool");
//		
//		pool.reset();
//		assertEquals(NON_DUPLICATE_VALS.size(), pool.size(), "size returned wrong size for item pool after reset");
//		assertEquals(NON_DUPLICATE_VALS.size(), pool.unpeekedSize(), "unpeekedSize returned wrong size for item pool after reset");
//
//		assertPoolEquals(EXPECTED_NON_DUPLICATE, pool);
	}
	
	@Test
	void get() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
		
		int found = pool.get(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(0), found, "Get did not return value based on passed Random");

		int found2 = pool.get(rand);
		assertNotEquals(found, found2, "Get did not remove item from pool");
	}
	
	@Test
	void get_lastItem() 
	{
		// Test the case of the last item in the pool
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(NON_DUPLICATE_VALS.size() - 1).thenReturn(NON_DUPLICATE_VALS.size() - 2);

		EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
		
		int found = pool.get(rand);
		
		assertEquals(NON_DUPLICATE_VALS.get(NON_DUPLICATE_VALS.size() - 1), found, "Get did not return value based on passed Random");

		int found2 = pool.get(rand);
		assertNotEquals(found, found2, "Get did not remove item from pool");
	}
	
	@Test
	void get_badCases() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);

		EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_VALS);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);
		
		assertNull(pool.get(null));
		
		// Exhaust the pool
		for (int i = 0; i < NON_DUPLICATE_VALS.size(); i++)
		{
			assertNotNull(pool.get(rand), "Get returned null when it should have items still");
		}
		
		// then do one more peek
		assertNull(pool.get(rand), "Get did not return null when it was empty");
	}
	
	@Test
	void get_empty() 
	{
		EliminatePool<Integer> base = EliminatePool.createEmpty();
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 1);

		assertNull(pool.get(new Random()), "Get did not return null when pool was empty");
	}
	
	@Test
	void useNextPool() 
	{
		Random rand = mock(Random.class);
		when(rand.nextInt(anyInt())).thenReturn(0);
		
		EliminatePool<Integer> base = EliminatePool.create(NON_DUPLICATE_ARRAY);
		EliminatePoolSet<Integer> pool = EliminatePoolSet.create(base, 3);
		assertTrue(pool.useNextPool());
		assertTrue(pool.useNextPool());
		assertFalse(pool.useNextPool());
		// ensure it didn't walk past the end
		assertNotNull(pool.get(rand));
		
		// See that reset sets it back to the first
		pool.reset();
		
		assertNotNull(pool.get(rand));
		assertTrue(pool.useNextPool());	
		assertNotNull(pool.get(rand));
		assertNotNull(pool.get(rand));
		assertEquals(NON_DUPLICATE_VALS.size() - 1, pool.getWorkingPools().get(0).size(), "size did not return the full pool size");
		assertEquals(NON_DUPLICATE_VALS.size() - 2, pool.getWorkingPools().get(1).size(), "size did not return the full pool size");
		
		// Finish out using next pools
		assertTrue(pool.useNextPool());
		assertFalse(pool.useNextPool());

		EliminatePoolSet<Integer> pool2 = EliminatePoolSet.create(base, 1);
		assertFalse(pool2.useNextPool());
		// ensure it didn't walk past the end
		assertNotNull(pool2.get(new Random()));
	}
}
