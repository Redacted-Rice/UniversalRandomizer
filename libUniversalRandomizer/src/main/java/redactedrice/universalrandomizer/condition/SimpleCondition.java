package redactedrice.universalrandomizer.condition;


import java.util.Comparator;

import redactedrice.universalrandomizer.userobjectapis.Condition;
import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.wrappers.ComparableAsComparator;

public class SimpleCondition<T, M> implements Condition<T> {
    private Getter<T, M> getter;
    private Negate negate;
    private Comparison comparison;
    private M compareToVal;
    private Comparator<M> comparator;

    public static <T2, R2 extends Comparable<R2>> SimpleCondition<T2, R2> create(
            Getter<T2, R2> getter, Comparison comparison, R2 compareToVal) {
        return create(getter, Negate.NO, comparison, compareToVal);
    }

    public static <T2, R2 extends Comparable<R2>> SimpleCondition<T2, R2> create(
            Getter<T2, R2> getter, Negate negate, Comparison comparison, R2 compareToVal) {
        return create(getter, negate, comparison, compareToVal, new ComparableAsComparator<>());
    }

    public static <T2, R2> SimpleCondition<T2, R2> create(Getter<T2, R2> getter,
            Comparison comparison, R2 compareToVal, Comparator<R2> comparator) {
        return create(getter, Negate.NO, comparison, compareToVal, comparator);
    }

    public static <T2, R2> SimpleCondition<T2, R2> create(Getter<T2, R2> getter, Negate negate,
            Comparison comparison, R2 compareToVal, Comparator<R2> comparator) {
        if (getter == null || negate == null || comparison == null) {
            return null;
        }
        return new SimpleCondition<>(getter, negate, comparison, compareToVal, comparator);
    }

    protected SimpleCondition(Getter<T, M> getter, Negate negate, Comparison comparison,
            M compareToVal, Comparator<M> comparator) {
        this.getter = getter;
        this.negate = negate;
        this.comparison = comparison;
        this.compareToVal = compareToVal;
        this.comparator = comparator;
    }

    @Override
    public boolean evaluate(T obj) {
        M ret = getter.get(obj);

        boolean result = invokeCompareTo(ret, comparison, compareToVal);
        if (negate == Negate.YES) {
            result = !result;
        }

        return result;
    }

    private boolean invokeCompareTo(M objVal, Comparison comparison, M compareToVal) {
        int compareResult = comparator.compare(objVal, compareToVal);
        switch (comparison) {
        case EQUAL:
            return 0 == compareResult;
        case LESS_THAN:
            return 0 > compareResult;
        case GREATER_THAN:
            return 0 < compareResult;
        case LESS_THAN_OR_EQUAL:
            return 0 >= compareResult;
        case GREATER_THAN_OR_EQUAL:
            return 0 <= compareResult;
        default:
            throw new IllegalArgumentException("Unknown comparison value: " + comparison);
        }
    }

    public Getter<T, M> getGetter() {
        return getter;
    }

    public Negate getNegate() {
        return negate;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public M getCompareToVal() {
        return compareToVal;
    }

    public Comparator<M> getComparator() {
        return comparator;
    }

    protected boolean setGetter(Getter<T, M> getter) {
        if (getter == null) {
            return false;
        }
        this.getter = getter;
        return true;
    }

    protected boolean setNegate(Negate negate) {
        if (negate == null) {
            return false;
        }
        this.negate = negate;
        return true;
    }

    protected boolean setComparison(Comparison comparison) {
        if (comparison == null) {
            return false;
        }
        this.comparison = comparison;
        return true;
    }

    protected void setCompareToVal(M val) {
        this.compareToVal = val;
    }

    protected boolean setComparator(Comparator<M> comparator) {
        if (comparator == null) {
            return false;
        }
        this.comparator = comparator;
        return true;
    }
}
