package redactedrice.universalrandomizer.userobjectapis;

public interface MultiSetterNoReturn <T, V>
{
	public void set(T toSet, V val, int count);

	private boolean asSetter(T t, V v)
	{
		set(t, v, 1);
		return true;
	}

	private void asSetterNoReturn(T t, V v)
	{
		set(t, v, 1);
	}

	private boolean asMultiSetter(T t, V v, int c)
	{
		set(t, v, c);
		return true;
	}
	
	public default Setter<T, V> asSetter()
	{
		return this::asSetter;
	}
	
	public default SetterNoReturn<T, V> asSetterNoReturn()
	{
		return this::asSetterNoReturn;
	}
	
	public default MultiSetter<T, V> asMultiSetter()
	{
		return this::asMultiSetter;
	}
	
	public static <T2, V2> Setter<T2, V2> asSetter(MultiSetterNoReturn<T2, V2> setter)
	{
		return setter.asSetter();
	}
	
	public static <T2, V2> SetterNoReturn<T2, V2> asSetterNoReturn(MultiSetterNoReturn<T2, V2> setter)
	{
		return setter.asSetterNoReturn();
	}
	
	public static <T2, V2> MultiSetter<T2, V2> asMultiSetter(MultiSetterNoReturn<T2, V2> setter)
	{
		return setter.asMultiSetter();
	}
}
