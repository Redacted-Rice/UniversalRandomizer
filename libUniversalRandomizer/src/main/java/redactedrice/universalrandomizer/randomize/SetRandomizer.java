package redactedrice.universalrandomizer.randomize;


import java.util.Collection;
import java.util.Iterator;

import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetterNoReturn;
import redactedrice.universalrandomizer.userobjectapis.Setter;
import redactedrice.universalrandomizer.userobjectapis.SetterNoReturn;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class SetRandomizer<T extends Collection<O>, O, P extends Collection<S>, S> extends OneToOneRandomizer<T, O, P, S>
{		
	protected SetRandomizer(MultiSetter<O, S> setter, Getter<T, Integer> countGetter)
	{
		super(setter, countGetter);
	}

	// Create a multi setter with count from object
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(MultiSetter<O2, S2> setter, Getter<T2, Integer> countGetter)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new SetRandomizer<>(setter, countGetter);
	}

	// Create a multi setter with fixed count
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(MultiSetter<O2, S2> setter, int count)
	{
		return create(setter, o -> count);
	}
	
	// Create a single setter where we set the whole collection at once
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(MultiSetter<O2, S2> setter)
	{
		return create(setter, 1);
	}
	
//	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
//	create(MultiSetterNoReturn<O2, S2> setter)
//	{
//		if (setter == null)
//		{
//			return null;
//		}
//		return create(setter.asMultiSetter());
//	}
	
	// Create a single setter where we set the whole collection at once
	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
	create(Setter<O2, S2> setter)
	{
		if (setter == null)
		{
			return null;
		}
		return create(setter.asMultiSetter());
	}

	// Create a single setter where we set the whole collection at once
//	public static <T2 extends Collection<O2>, O2, P2 extends Collection<S2>, S2> SetRandomizer<T2, O2, P2, S2> 
//	create(SetterNoReturn<O2, S2> setter)
//	{
//		if (setter == null)
//		{
//			return null;
//		}
//		return create(setter.asMultiSetter());
//	}

	@Override	
	protected boolean assignAndCheckEnforce(T objs, P poolValue, int count)
	{
		boolean success = true;
		Iterator<O> objItr = objs.iterator();
		Iterator<S> valItr = poolValue.iterator();
		while (objItr.hasNext() && success)
		{
			O obj = objItr.next();
			success = getSetter().setReturn(obj, valItr.next(), count);
		}
		return success;
	}
}