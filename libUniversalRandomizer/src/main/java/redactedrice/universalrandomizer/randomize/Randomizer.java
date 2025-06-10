package redactedrice.universalrandomizer.randomize;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import redactedrice.universalrandomizer.pool.RandomizerMultiPool;
import redactedrice.universalrandomizer.pool.RandomizerPool;
import redactedrice.universalrandomizer.pool.RandomizerSinglePool;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;

public abstract class Randomizer<T, U, O, P, S> 
{	
	private RandomizerPool<P> pool;
	private RandomizerMultiPool<U, P> multiPool;
	private Random rand;
	private MultiSetter<O, S> setter;
	private Getter<T, Integer> countGetter;

	protected Randomizer(MultiSetter<O, S> setter, Getter<T, Integer> countGetter)
	{
		pool = null;
		multiPool = null;
		rand = null;
		
		this.setter = setter;
		this.countGetter = countGetter;
	}

	public boolean perform(Stream<T> objStream, RandomizerSinglePool<P> pool) 
	{
		return perform(objStream, pool, null);
	}

	public boolean perform(Stream<T> objStream, RandomizerMultiPool<U, P> pool) 
	{
		return perform(objStream, pool, null);
	}
	
	public boolean perform(Stream<T> objStream, RandomizerSinglePool<P> pool, Random rand) 
	{
		this.pool = pool;
		this.multiPool = null;
		return performCommon(objStream, rand);
	}
	
	public boolean perform(Stream<T> objStream, RandomizerMultiPool<U, P> pool, Random rand) 
	{
		this.pool = pool;
		this.multiPool = pool;
		return performCommon(objStream, rand);
	}
	
	private boolean performCommon(Stream<T> objStream, Random rand)
	{
		if (this.rand == null)
		{
			this.rand = rand == null ? new Random() : rand;
		}
		
		// in order to "reuse" the stream, we need to convert it out of a stream
		// and create new ones. We need to save off the list if we need to create
        // a source pool or if there is a RESET on fail action
		List<T> streamAsList = objStream.toList();
		return attemptRandomization(streamAsList);
	}
	
	// Handles RESET
	protected boolean attemptRandomization(List<T> streamAsList)
	{
		// Attempt to assign randomized values for each item in the stream
		List<T> failed = randomize(streamAsList.stream());		
		return failed.isEmpty();
	}

	protected List<T> randomize(Stream<T> objStream)
	{
		return objStream.filter(this::assignValueNegated).toList();
	}
	
	protected boolean assignValueNegated(T obj)
	{
		return !assignValue(obj);
	}
	
	// Think on delayed checking of set values?
	// TODO: Add a delayed, full object enforce?
	protected boolean assignValue(T obj)
	{
		boolean success = true;
		int times = countGetter.get(obj);
		
		// Assign all values in the set
		for (int count = 0; count < times; count++)
		{
			if (success)
			{
				success = attemptAssignValue(obj, count);
			}
			else
			{
				break;
			}
		}
		
		return success;
	}

	protected abstract boolean attemptAssignValue(T obj, int count);

	protected Random getRandom() 
	{
		return rand;
	}

	protected RandomizerPool<P> getPool() 
	{
		return pool;
	}

	protected RandomizerMultiPool<U, P> getMultiPool() 
	{
		return multiPool;
	}
	
	protected void safeMultiPoolSetPool(U obj, int count) 
	{
		if (multiPool != null)
		{
			multiPool.setPool(obj, count);
		}
	}
	
	protected Getter<T, Integer> getCountGetter() 
	{
		return countGetter;
	}

	protected MultiSetter<O, S> getSetter() 
	{
		return setter;
	}
}