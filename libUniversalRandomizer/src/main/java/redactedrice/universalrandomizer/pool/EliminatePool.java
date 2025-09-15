package redactedrice.universalrandomizer.pool;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class EliminatePool<T> implements RandomizerSinglePool<T> {
    private ArrayList<T> pool;
    private LinkedList<T> removed;

    protected EliminatePool(boolean removeDuplicates, Collection<T> valCollection) {
        if (removeDuplicates) {
            // Convert to a set first to remove duplicates
            pool = new ArrayList<>(new HashSet<>(valCollection));
        } else {
            pool = new ArrayList<>(valCollection);
        }
        removed = new LinkedList<>();
    }

    protected EliminatePool(EliminatePool<T> toCopy) {
        pool = new ArrayList<>(toCopy.pool);
        removed = new LinkedList<>(toCopy.removed);
    }

    public static <V> EliminatePool<V> createEmpty() {
        return new EliminatePool<>(false, new ArrayList<>());
    }

    public static <V> EliminatePool<V> create(Collection<V> valCollection) {
        if (valCollection == null) {
            return null;
        }
        return create(false, valCollection);
    }

    public static <V> EliminatePool<V> create(boolean removeDuplicates,
            Collection<V> valCollection) {
        if (valCollection == null) {
            return null;
        }
        return new EliminatePool<>(removeDuplicates, valCollection);
    }

    @SafeVarargs
    public static <V> EliminatePool<V> create(V... values) {
        return create(List.of(values));
    }

    @SafeVarargs
    public static <V> EliminatePool<V> create(boolean removeDuplicates, V... values) {
        return create(removeDuplicates, List.of(values));
    }

    public EliminatePool<T> copy() {
        return new EliminatePool<>(this);
    }

    @Override
    public void reset() {
        getPool().addAll(removed);
        removed.clear();
    }

    protected int getIndex(Random rand) {
        if (pool.isEmpty() || rand == null) {
            return -1;
        }
        return rand.nextInt(pool.size());
    }

    @Override
    public T get(Random rand) {
        if (pool.isEmpty() || rand == null) {
            return null;
        }
        // get a random index and the object at that index
        int index = rand.nextInt(pool.size());

        T obj = pool.get(index);

        removed.addFirst(obj);
        if (index != pool.size() - 1) {
            pool.set(index, pool.get(pool.size() - 1));
        }
        pool.remove(pool.size() - 1);

        // Return the object
        return obj;
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

    protected List<T> getRemoved() {
        return removed;
    }

    @Override
    public boolean useNextPool() {
        return false;
    }
}