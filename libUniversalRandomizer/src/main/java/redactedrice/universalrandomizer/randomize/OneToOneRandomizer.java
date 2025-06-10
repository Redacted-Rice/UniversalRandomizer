package redactedrice.universalrandomizer.randomize;

import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.userobjectapis.MultiSetter;

public abstract class OneToOneRandomizer<T, O, P, S> extends Randomizer<T, T, O, P, S>
{	
	protected OneToOneRandomizer(MultiSetter<O, S> setter, Getter<T, Integer> countGetter) 
	{
		super(setter, countGetter);
	}

	@Override
	protected boolean attemptAssignValue(T obj, int count)
	{
		// Set the pool by the key
		if (getMultiPool() != null)
		{
			getMultiPool().setPool(obj, count);
		}		
		
		P selectedVal = getPool().get(getRandom());
		// Loop on pool depth (if pool supports it)
		while (selectedVal == null && getPool().useNextPool())
		{
			selectedVal = getPool().get(getRandom());
		}
		return selectedVal != null && assignAndCheckEnforce(obj, selectedVal, count);
	}
	
	protected abstract boolean assignAndCheckEnforce(T obj, P poolValue, int count);
}