package redactedrice.universalrandomizer.pool;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class ReusePool<T> implements RandomizerSinglePool<T> {
    private ArrayList<T> pool;

    protected ReusePool(Collection<T> valCollection, boolean removeDuplicates) {
        if (removeDuplicates) {
            // Convert to a set first to remove duplicates
            pool = new ArrayList<>(new HashSet<>(valCollection));
        } else {
            pool = new ArrayList<>(valCollection);
        }
    }

    protected ReusePool(ReusePool<T> toCopy) {
        pool = new ArrayList<>(toCopy.pool);
    }

    public static <V> ReusePool<V> createEmpty() {
        return new ReusePool<>(new ArrayList<>(), false);
    }

    public static <V> ReusePool<V> create(Collection<V> valCollection) {
        if (valCollection == null) {
            return null;
        }
        return new ReusePool<>(valCollection, false);
    }

    public static <V> ReusePool<V> createNoDups(Collection<V> valCollection) {
        if (valCollection == null) {
            return null;
        }
        return new ReusePool<>(valCollection, true);
    }

    @SafeVarargs
    public static <V> ReusePool<V> create(V... values) {
        return create(List.of(values));
    }

    @SafeVarargs
    public static <V> ReusePool<V> createNoDups(V... values) {
        return createNoDups(List.of(values));
    }

    public ReusePool<T> copy() {
        return new ReusePool<>(this);
    }

    @Override
    public void reset() {
        // nothing to do!
    }

    protected int getIndex(Random rand) {
        if (pool.isEmpty() || rand == null) {
            return -1;
        }
        return rand.nextInt(pool.size());
    }

    @Override
    public T get(Random rand) {
        // get a random index and the object at that index
        int index = getIndex(rand);

        // Return the object
        return index >= 0 ? pool.get(index) : null;
    }

    public int size() {
        return pool.size();
    }

    public int instancesOf(T obj) {
        return (int) pool.stream().filter(s -> s == obj).count();
    }

    protected ArrayList<T> getPool() {
        return pool;
    }

    @Override
    public boolean useNextPool() {
        return false;
    }
}