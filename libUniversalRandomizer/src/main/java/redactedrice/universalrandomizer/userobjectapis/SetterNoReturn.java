package redactedrice.universalrandomizer.userobjectapis;


public interface SetterNoReturn<T, V> {
    public void set(T toSet, V val);

    private boolean asSetter(T t, V v) {
        set(t, v);
        return true;
    }

    private boolean asMultiSetter(T t, V v, int c) {
        set(t, v);
        return true;
    }

    private void asMultiSetterNoReturn(T t, V v, int c) {
        set(t, v);
    }

    public default Setter<T, V> asSetter() {
        return this::asSetter;
    }

    public default MultiSetter<T, V> asMultiSetter() {
        return this::asMultiSetter;
    }

    public default MultiSetterNoReturn<T, V> asMultiSetterNoReturn() {
        return this::asMultiSetterNoReturn;
    }

    public static <T2, V2> Setter<T2, V2> asSetter(SetterNoReturn<T2, V2> setter) {
        return setter.asSetter();
    }

    public static <T2, V2> MultiSetter<T2, V2> asMultiSetter(SetterNoReturn<T2, V2> setter) {
        return setter.asMultiSetter();
    }

    public static <T2, V2> MultiSetterNoReturn<T2, V2> asMultiSetterNoReturn(
            SetterNoReturn<T2, V2> setter) {
        return setter.asMultiSetterNoReturn();
    }
}
