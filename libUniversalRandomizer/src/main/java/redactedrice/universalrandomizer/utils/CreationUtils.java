package redactedrice.universalrandomizer.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import redactedrice.universalrandomizer.userobjectapis.Sum;
import redactedrice.universalrandomizer.userobjectapis.Sumable;
import redactedrice.universalrandomizer.wrappers.ComparableAsComparator;
import redactedrice.universalrandomizer.wrappers.SumableAsSum;

public class CreationUtils 
{	
	private CreationUtils() 
	{
	    throw new IllegalStateException("Utility class");
	}
	
	public static <N extends Comparable<N> & Sumable<N>> Collection<N> createRange(N min, N max, N stepSize)
	{
		return createRange(min, max, stepSize, new SumableAsSum<>());
	}
	
	public static <N extends Comparable<N>> Collection<N> createRange(N min, N max, N stepSize, Sum<N> sumFn)
	{
		return createRange(min, max, stepSize, new ComparableAsComparator<>(), sumFn);
	}
	
	public static <N extends Sumable<N>> Collection<N> createRange(N min, N max, N stepSize, Comparator<N> comparator)
	{
		return createRange(min, max, stepSize, comparator, new SumableAsSum<>());
	}
	
	public static <N> Collection<N> createRange(N min, N max, N stepSize, Comparator<N> comparator, Sum<N> sumtor)
	{
		List<N> vals = new LinkedList<>();
		N nextVal = min;
		while (comparator.compare(nextVal, max) <= 0)
		{
			vals.add(nextVal);
			nextVal = sumtor.sum(nextVal, stepSize);
		}
		return vals;
	}
	
	public static <T> Collection<T> weightedCollection(Collection<Entry<Integer, T>> weightedValuePairs)
	{
		LinkedList<T> items = new LinkedList<>();
		for (Entry<Integer, T> entry : weightedValuePairs)
		{
			for (int i = 0; i < entry.getKey(); i++)
			{
				items.add(entry.getValue());
			}
		}
		return items;
	}

	@SafeVarargs
	public static <T> Collection<T> weightedCollection(Entry<Integer, T>... weightedValuePairs)
	{
		return weightedCollection(List.of(weightedValuePairs));
	}
	
	public static <T> Collection<T> weightedCollection(Stream<Entry<Integer, T>> weightedValuePairs)
	{
		return weightedCollection(weightedValuePairs.toList());
	}
}
