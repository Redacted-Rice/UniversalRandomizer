package redactedrice.universalrandomizer.randomize;

import java.util.Collection;
import java.util.Iterator;

import redactedrice.universalrandomizer.userobjectapis.MultiSetter;

public class GroupSetterWrapper<T extends Collection<O>, O, S extends Collection<V>, V> implements MultiSetter <T, S>
{
	private MultiSetter<O, V> setter;
	
	public GroupSetterWrapper(MultiSetter<O, V> setter)
	{
		this.setter = setter;
	}
	
	@Override
	public boolean setReturn(T objs, S vals, int count)
	{
		boolean success = true;
		Iterator<O> objItr = objs.iterator();
		Iterator<V> valItr = vals.iterator();
		while (objItr.hasNext() && success)
		{
			O obj = objItr.next();
			success = setter.setReturn(obj, valItr.next(), count);
		}
		return success;
	}

	public MultiSetter<O, V> getSetter() {
		return setter;
	}
}
