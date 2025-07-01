package redactedrice.universalrandomizer.randomize;


import java.util.Collection;
import java.util.Iterator;

import redactedrice.universalrandomizer.userobjectapis.MultiSetter;

public class MultiSetterWrapper<T, S extends Collection<V>, V> implements MultiSetter<T, S> {
    private MultiSetter<T, V> setter;

    public MultiSetterWrapper(MultiSetter<T, V> setter) {
        this.setter = setter;
    }

    @Override
    public boolean setReturn(T obj, S vals, int count) {
        boolean success = true;
        Iterator<V> valItr = vals.iterator();
        int counter = 0;
        while (valItr.hasNext() && success) {
            success = setter.setReturn(obj, valItr.next(), counter++);
        }
        return success;
    }

    public MultiSetter<T, V> getSetter() {
        return setter;
    }
}
