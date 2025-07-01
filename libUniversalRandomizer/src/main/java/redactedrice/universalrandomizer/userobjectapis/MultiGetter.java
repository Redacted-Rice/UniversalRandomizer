package redactedrice.universalrandomizer.userobjectapis;


public interface MultiGetter<T, R> {
    public R get(T toGetFrom, int count);

    private R asGetter(T t) {
        return get(t, 1);
    }

    public default Getter<T, R> asGetter() {
        return this::asGetter;
    }

    public static <T2, R2> Getter<T2, R2> asGetter(MultiGetter<T2, R2> getter) {
        return getter.asGetter();
    }
}
