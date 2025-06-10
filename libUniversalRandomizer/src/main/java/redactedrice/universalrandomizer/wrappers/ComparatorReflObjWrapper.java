package redactedrice.universalrandomizer.wrappers;

import java.util.Comparator;

public class ComparatorReflObjWrapper<T> implements Comparator<ReflectionObject<T>>
{
	Comparator<T> unwrapped;
	
	public ComparatorReflObjWrapper(Comparator<T> unwrapped)
	{
		this.unwrapped = unwrapped;
	}

	@Override
	public int compare(ReflectionObject<T> o1, ReflectionObject<T> o2) {
		return unwrapped.compare(o1.getObject(), o2.getObject());
	}
}