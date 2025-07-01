package support;


import redactedrice.universalrandomizer.userobjectapis.Sumable;

public class SumableComparableObject
        implements Sumable<SumableComparableObject>, Comparable<SumableComparableObject> {
    public int intVal;
    public int compareSecNullReturn;

    public SumableComparableObject(int intVal) {
        this.intVal = intVal;
        compareSecNullReturn = 1;
    }

    @Override
    public SumableComparableObject sum(SumableComparableObject toAdd) {
        if (toAdd == null) {
            return new SumableComparableObject(this.intVal);
        }
        return new SumableComparableObject(this.intVal + toAdd.intVal);
    }

    @Override
    public int compareTo(SumableComparableObject toCompare) {
        if (toCompare == null) {
            return compareSecNullReturn;
        }
        return this.intVal - toCompare.intVal;
    }
}
