package redactedrice.universalrandomizer.pool;


import java.util.Map;
import java.util.Random;

import redactedrice.universalrandomizer.userobjectapis.MultiGetter;

public class MultiPool<O, K, T> implements RandomizerMultiPool<O, T> {
    private Map<K, RandomizerPool<T>> poolMap;
    private MultiGetter<O, K> keyGetter;
    private RandomizerPool<T> activePool;

    public MultiPool(Map<K, RandomizerPool<T>> poolMap, MultiGetter<O, K> keyGetter) {
        this.poolMap = poolMap;
        this.keyGetter = keyGetter;
        this.activePool = null;
    }

    public static <O2, K2, T2> MultiPool<O2, K2, T2> create(Map<K2, RandomizerPool<T2>> poolMap,
            MultiGetter<O2, K2> keyGetter) {
        if (poolMap == null || keyGetter == null) {
            return null;
        }
        return new MultiPool<>(poolMap, keyGetter);
    }

    @Override
    public boolean setPool(O obj, int count) {
        K key = keyGetter.get(obj, count);
        boolean success = key != null;
        if (success) {
            activePool = poolMap.get(key);
            success = activePool != null;
        }
        return success;
    }

    @Override
    public void reset() {
        for (RandomizerPool<T> pool : poolMap.values()) {
            pool.reset();
        }
    }

    @Override
    public T get(Random rand) {
        if (activePool != null) {
            return activePool.get(rand);
        }
        return null;
    }

    @Override
    public boolean useNextPool() {
        if (activePool != null) {
            return activePool.useNextPool();
        }
        return false;
    }
}
