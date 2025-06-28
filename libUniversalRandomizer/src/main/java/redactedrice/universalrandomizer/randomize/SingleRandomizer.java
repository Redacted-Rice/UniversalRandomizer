package redactedrice.universalrandomizer.randomize;


import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.Setter;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class SingleRandomizer
{
	// Create a multi setter with count from object
	public static <T, P> Randomizer<T, P> 
	create(MultiSetter<T, P> setter, Getter<T, Integer> countGetter)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new Randomizer<>(setter, countGetter);
	}

	// Create a multi setter with fixed count
	public static <T, P> Randomizer<T, P> 
	create(MultiSetter<T, P> setter, int count)
	{
		return create(setter, o -> count);
	}

	// Create a single setter
	public static <T, P> Randomizer<T, P> 
	create(MultiSetter<T, P> setter)
	{
		return create(setter, 1);
	}
	
	// Create a single setter
	public static <T, P> Randomizer<T, P> create(Setter<T, P> setter)
	{
		if (setter == null)
		{
			return null;
		}
		return create(setter.asMultiSetter());
	}
}