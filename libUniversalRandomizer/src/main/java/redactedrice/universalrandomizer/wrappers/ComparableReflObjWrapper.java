package redactedrice.universalrandomizer.wrappers;


import java.util.Comparator;

public class ComparableReflObjWrapper<T extends Comparable<T>>
        implements Comparator<ReflectionObject<T>> {
    @Override
    public int compare(ReflectionObject<T> o1, ReflectionObject<T> o2) {
        // Both will not be null - Reflection Object doesn't allow null objects
        return o1.getObject().compareTo(o2.getObject());
    }

}