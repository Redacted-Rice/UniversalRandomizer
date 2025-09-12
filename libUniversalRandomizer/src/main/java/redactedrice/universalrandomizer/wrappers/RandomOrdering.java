package redactedrice.universalrandomizer.wrappers;


public class RandomOrdering<T> {
    private T obj;
    private long sortingValue;

    protected RandomOrdering(T obj, long sortingVal) {
        this.obj = obj;
        sortingValue = sortingVal;
    }

    public static <T2> RandomOrdering<T2> create(T2 obj, long sortingVal) {
        if (obj == null) {
            return null;
        }
        return new RandomOrdering<>(obj, sortingVal);
    }

    public T getObject() {
        return obj;
    }

    protected boolean setObject(T obj) {
        if (obj != null) {
            this.obj = obj;
            return true;
        }
        return false;
    }

    public long getSortingValue() {
        return sortingValue;
    }

    protected void setSortingValue(long val) {
        sortingValue = val;
    }

    public RandomOrdering<T> setSortingValueReturnSelf(int val) {
        setSortingValue(val);
        return this;
    }

    public static <T> int sortBySortingValue(RandomOrdering<T> lhs, RandomOrdering<T> rhs) {
        return Long.compare(lhs.getSortingValue(), rhs.getSortingValue());
    }
}
