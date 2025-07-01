package redactedrice.universalrandomizer.userobjectapis;


public interface MultiSetter<T, V> {
    public boolean setReturn(T toSet, V val, int counter);

    private boolean asSetter(T t, V v) {
        return setReturn(t, v, 1);
    }

    private void asSetterNoReturn(T t, V v) {
        setReturn(t, v, 1);
    }

    private void asMultiSetterNoReturn(T t, V v, int c) {
        setReturn(t, v, c);
    }

    public default Setter<T, V> asSetter() {
        return this::asSetter;
    }

    public default SetterNoReturn<T, V> asSetterNoReturn() {
        return this::asSetterNoReturn;
    }

    public default MultiSetterNoReturn<T, V> asMultiSetterNoReturn() {
        return this::asMultiSetterNoReturn;
    }

    public static <T2, V2> Setter<T2, V2> asSetter(MultiSetter<T2, V2> setter) {
        return setter.asSetter();
    }

    public static <T2, V2> SetterNoReturn<T2, V2> asSetterNoReturn(MultiSetter<T2, V2> setter) {
        return setter.asSetterNoReturn();
    }

    public static <T2, V2> MultiSetterNoReturn<T2, V2> asMultiSetterNoReturn(
            MultiSetter<T2, V2> setter) {
        return setter.asMultiSetterNoReturn();
    }
}
