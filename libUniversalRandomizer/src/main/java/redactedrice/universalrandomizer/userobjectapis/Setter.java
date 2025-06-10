package redactedrice.universalrandomizer.userobjectapis;

public interface Setter <T, V>
{
	public boolean setReturn(T toSet, V val);
	
	private void asSetterNoReturn(T t, V v)
	{
		setReturn(t, v);
	}

	private boolean asMultiSetter(T t, V v, int c)
	{
		return setReturn(t, v);
	}

	private void asMultiSetterNoReturn(T t, V v, int c)
	{
		setReturn(t, v);
	}
	
	public default SetterNoReturn<T, V> asSetterNoReturn()
	{
		return this::asSetterNoReturn;
	}

	public default MultiSetter<T, V> asMultiSetter()
	{
		return this::asMultiSetter;
	}

	public default MultiSetterNoReturn<T, V> asMultiSetterNoReturn()
	{
		return this::asMultiSetterNoReturn;
	}
	
	public static <T2, V2> SetterNoReturn<T2, V2> asSetterNoReturn(Setter<T2, V2> setter)
	{
		return setter.asSetterNoReturn();
	}

	public static <T2, V2> MultiSetter<T2, V2> asMultiSetter(Setter<T2, V2> setter)
	{
		return setter.asMultiSetter();
	}

	public static <T2, V2> MultiSetterNoReturn<T2, V2> asMultiSetterNoReturn(Setter<T2, V2> setter)
	{
		return setter.asMultiSetterNoReturn();
	}
}
