package redactedrice.universalrandomizer.randomize;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.Setter;
import redactedrice.universalrandomizer.utils.StreamUtils;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class DependentRandomizer<T extends Collection<O>, O, P, G> extends Randomizer<T, O, O, P, P>
{		
	private Comparator<? super P> valSorter;
	private Getter<O, G> grouper;
	private Comparator<? super G> groupSorter;
	
	protected DependentRandomizer(MultiSetter<O, P> setter, Getter<T, Integer> countGetter, Comparator<? super P> valSorter, 
			Getter<O, G> grouper, Comparator<? super G> groupSorter)
	{
		super(setter, countGetter);
		this.valSorter = valSorter;
		this.grouper = grouper;
		this.groupSorter = groupSorter;
	}

	// Create a multi setter with count from the list
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(MultiSetter<O2, P2> setter, Getter<T2, Integer> countGetter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new DependentRandomizer<>(setter, countGetter, valSorter, grouper, groupSorter);
	}

	// Create a multi setter with fixed count
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(MultiSetter<O2, P2> setter, int count, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		return create(setter, o -> count, valSorter, grouper, groupSorter);
	}
	
	// Create a single setter
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(MultiSetter<O2, P2> setter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		return create(setter, 1, valSorter, grouper, groupSorter);
	}
	
	// Create a single setter where we set the whole collection at once
	public static <T2 extends Collection<O2>, O2, P2, G2> DependentRandomizer<T2, O2, P2, G2> 
	create(Setter<O2, P2> setter, Comparator<? super P2> valSorter, 
			Getter<O2, G2> grouper, Comparator<? super G2> groupSorter)
	{
		if (setter == null)
		{
			return null;
		}
		return create(Setter.asMultiSetter(setter), 1, valSorter, grouper, groupSorter);
	}
	
	@Override
	protected boolean attemptAssignValue(T obj, int count)
	{
		boolean success = false;
		
		// Get the values for each object
		List<P> vals = getValues(obj, count);
		if (!vals.isEmpty())
		{
			// Sort the values as appropriate
			vals.sort(valSorter);
			
			// Group the object being randomized (in sorted order)
			SortedMap<G, List<O>> groupedObjs = StreamUtils.sortedGroup(obj.stream(), grouper, groupSorter);
			
			// break val into groups by index/count
			Iterator<P> valItr = vals.iterator();
			success = true;
			for (List<O> objs : groupedObjs.values())
			{
				// try set each group
				if (!assignAndCheckEnforce(objs, valItr, count)) 
				{
					success = false;
					break;
				}
			}
		}
		return success;
	}
	
	protected List<P> getValues(T objs, int count)
	{
		List<P> vals = new ArrayList<>(objs.size());
		for (O item : objs)
		{
			// Set the mutlipool (if we are using one)
			safeMultiPoolSetPool(item, count);
			
			P val = getPool().get(getRandom());
			// Loop on pool depth (if pool supports it)
			while (val == null && getPool().useNextPool())
			{
				val = getPool().get(getRandom());
			} 	
			
			if (val == null)
			{
				vals.clear();
				break;
			}
			vals.add(val);
		}
		return vals;
	}

	protected boolean assignAndCheckEnforce(List<O> objs, Iterator<P> valItr, int count)
	{
		boolean success = true;
		Iterator<O> objItr = objs.iterator();
		while (objItr.hasNext() && success)
		{
			O obj = objItr.next();
			success = getSetter().setReturn(obj, valItr.next(), count);
		}
		return success;
	}
}