package redactedrice.universalrandomizer.randomize;


import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetterNoReturn;
import redactedrice.universalrandomizer.userobjectapis.Setter;
import redactedrice.universalrandomizer.userobjectapis.SetterNoReturn;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class SingleRandomizer<T, P> extends OneToOneRandomizer<T, T, P, P>
{		
	protected SingleRandomizer(MultiSetter<T, P> setter, Getter<T, Integer> countGetter)
	{
		super(setter, countGetter);
	}

	// Create a multi setter with count from object
	public static <T2, P2> SingleRandomizer<T2, P2> create(MultiSetter<T2, P2> setter, Getter<T2, Integer> countGetter)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new SingleRandomizer<>(setter, countGetter);
	}

	// Create a multi setter with fixed count
	public static <T2, P2> SingleRandomizer<T2, P2> create(MultiSetter<T2, P2> setter, int count)
	{
		return create(setter, o -> count);
	}

	// Create a single setter
	public static <T2, P2> SingleRandomizer<T2, P2> create(MultiSetter<T2, P2> setter)
	{
		return create(setter, 1);
	}
	
	// Create a single setter
//	public static <T2, P2> SingleRandomizer<T2, P2> create(MultiSetterNoReturn<T2, P2> setter)
//	{
//		if (setter == null)
//		{
//			return null;
//		}
//		return create(setter.asMultiSetter());
//	}
	
	// Create a single setter
	public static <T2, P2> SingleRandomizer<T2, P2> create(Setter<T2, P2> setter)
	{
		if (setter == null)
		{
			return null;
		}
		return create(setter.asMultiSetter());
	}
	
	// Create a single setter
//	public static <T2, P2> SingleRandomizer<T2, P2> create(SetterNoReturn<T2, P2> setter)
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
		return getSetter().setReturn(obj, poolValue, count);
	}
}