package redactedrice.universalrandomizer.pool;


import java.util.Random;

public interface RandomizerPool<T> {
    public T get(Random rand);

    public void reset();

    public boolean useNextPool();
}