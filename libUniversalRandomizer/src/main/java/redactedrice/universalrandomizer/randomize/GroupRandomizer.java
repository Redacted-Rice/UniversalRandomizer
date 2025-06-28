package redactedrice.universalrandomizer.randomize;


import java.util.Collection;

import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;
import redactedrice.universalrandomizer.userobjectapis.Setter;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times
public class GroupRandomizer
{
	// Create a multi setter with count from object
	public static <T extends Collection<O>, O, P extends Collection<S>, S> Randomizer<T, P> 
	create(MultiSetter<O, S> setter, Getter<T, Integer> countGetter)
	{
		if (setter == null || countGetter == null)
		{
			return null;
		}
		return new Randomizer<>(new GroupSetterWrapper<T, O, P, S>(setter), countGetter);
	}

	// Create a multi setter with fixed count
	public static <T extends Collection<O>, O, P extends Collection<S>, S> Randomizer<T, P> 
	create(MultiSetter<O, S> setter, int count)
	{
		return create(setter, o -> count);
	}
	
	// Create a single setter where we set the whole collection at once
	public static <T extends Collection<O>, O, P extends Collection<S>, S> Randomizer<T, P> 
	create(MultiSetter<O, S> setter)
	{
		return create(setter, 1);
	}
	
	// Create a single setter where we set the whole collection at once
	public static <T extends Collection<O>, O, P extends Collection<S>, S> Randomizer<T, P> 
	create(Setter<O, S> setter)
	{
		if (setter == null)
		{
			return null;
		}
		return create(setter.asMultiSetter());
	}
}