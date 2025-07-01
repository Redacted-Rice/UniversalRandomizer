package redactedrice.universalrandomizer.pool;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EliminatePoolSet<T> implements RandomizerSinglePool<T> {
    public static final int UNLIMITED_DEPTH = -1;
    private int maxDepth;

    // Internal tracking
    private List<EliminatePool<T>> workingPools;
    private int currentPool;

    protected EliminatePoolSet(EliminatePool<T> sourcePool, int maxDepth) {
        this.maxDepth = maxDepth;
        if (maxDepth > 0) {
            workingPools = new ArrayList<>(maxDepth);
        } else {
            workingPools = new ArrayList<>();
        }
        workingPools.add(sourcePool.copy());
        workingPools.get(0).reset();
        currentPool = 0;
    }

    public static <T2> EliminatePoolSet<T2> create(EliminatePool<T2> sourcePool, int maxDepth) {
        if (sourcePool == null) {
            return null;
        }
        return new EliminatePoolSet<>(sourcePool, maxDepth);
    }

    public static <T2> EliminatePoolSet<T2> createNoAdditionalPools(EliminatePool<T2> sourcePool) {
        return create(sourcePool, 1);
    }

    @Override
    public void reset() {
        for (EliminatePool<T> pool : workingPools) {
            pool.reset();
        }
        currentPool = 0;
    }

    @Override
    public T get(Random rand) {
        if (currentPool < workingPools.size()) {
            // Get an item from the next pool
            return workingPools.get(currentPool).get(rand);
        }
        return null;
    }

    @Override
    public boolean useNextPool() {
        if (currentPool >= maxDepth - 1 && maxDepth > 0) {
            return false;
        }
        currentPool++;

        // If we ran out of pools, add a new one
        if (currentPool >= workingPools.size()) {
            workingPools.add(workingPools.get(currentPool - 1).copy());
            workingPools.get(currentPool).reset();
        }
        return true;
    }

    protected List<EliminatePool<T>> getWorkingPools() {
        return workingPools;
    }
}
