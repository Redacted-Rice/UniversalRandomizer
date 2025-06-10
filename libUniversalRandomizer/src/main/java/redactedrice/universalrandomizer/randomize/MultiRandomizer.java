package redactedrice.universalrandomizer.randomize;


import java.util.Collection;
import java.util.Iterator;

import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetterNoReturn;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times, once
/// for each item in the collection and only verifies the object after all setting is
/// done
public class MultiRandomizer<T, P extends Collection<S>, S> extends OneToOneRandomizer<T, T, P, S>
{	
	private static int return1(Object o)
	{
		// We have this function so we don't have to keep creating lambda
		// functions. Ignore sonar lint saying to use a constant since
		// we need the function signature for this to work
		return 1; // NOSONAR
	}
	
	protected MultiRandomizer(MultiSetter<T, S> setter)
	{
		super(setter, MultiRandomizer::return1);
	}
	
	// Create a single setter - where P is a collection of S
	// Create a single setter - S must match P
	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
	create(MultiSetter<T2, S2> setter)
	{
		if (setter == null)
		{
			return null;
		}
		return new MultiRandomizer<>(setter);
	}
	
//	public static <T2, P2 extends Collection<S2>, S2> MultiRandomizer<T2, P2, S2> 
//	create(MultiSetterNoReturn<T2, S2> setter)
//	{
//		if (setter == null)
//		{
//			return null;
//		}
//		return create(setter.asMultiSetter());
//	}

	@Override	
	protected boolean assignAndCheckEnforce(T obj, P poolValue, int count)
	{
		boolean success = true;
		Iterator<S> valItr = poolValue.iterator();
		int counter = 0;
		while (valItr.hasNext() && success)
		{
			success = getSetter().setReturn(obj, valItr.next(), counter++);
		}
		return success;
	}
}