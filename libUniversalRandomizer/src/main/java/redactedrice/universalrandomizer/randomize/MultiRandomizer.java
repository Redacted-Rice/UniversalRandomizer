package redactedrice.universalrandomizer.randomize;


import java.util.Collection;

import redactedrice.universalrandomizer.userobjectapis.MultiSetter;

/// Randomizes single items at a time but can randomize a field multiple times
/// i.e. randomizing a list field by calling and indexed setter multiple times, once
/// for each item in the collection and only verifies the object after all setting is
/// done
public class MultiRandomizer
{	
	private static int return1(Object o)
	{
		// We have this function so we don't have to keep creating lambda
		// functions. Ignore sonar lint saying to use a constant since
		// we need the function signature for this to work
		return 1; // NOSONAR
	}
	
	// Create a single setter - where P is a collection of S
	// Create a single setter - S must match P
	public static <T2, P2 extends Collection<S2>, S2> Randomizer<T2, P2> 
	create(MultiSetter<T2, S2> setter)
	{
		if (setter == null)
		{
			return null;
		}
		return new Randomizer<>(new MultiSetterWrapper<T2, P2, S2>(setter), MultiRandomizer::return1);
	}
}