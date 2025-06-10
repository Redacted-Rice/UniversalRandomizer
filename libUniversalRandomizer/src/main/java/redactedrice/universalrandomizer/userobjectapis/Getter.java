package redactedrice.universalrandomizer.userobjectapis;

public interface Getter <T, R> 
{
	public R get(T toGetFrom);
	
	private <C> R asMultiGetter(T t, C c)
	{
		return get(t);
	}

	public default MultiGetter<T, R> asMultiGetter()
	{
		return this::asMultiGetter;
	}
	
	public static <T2, R2> MultiGetter<T2, R2> asMultiGetter(Getter<T2, R2> getter)
	{
		return getter.asMultiGetter();
	}
}
