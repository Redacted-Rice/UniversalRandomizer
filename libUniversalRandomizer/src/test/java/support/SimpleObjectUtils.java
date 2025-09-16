package support;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SimpleObjectUtils {
    public static List<SimpleObject> soList(int count) {
        List<SimpleObject> list = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            list.add(new SimpleObject("so" + i, i));
        }
        return list;
    }

    public static List<Integer> toIntFieldList(List<SimpleObject> soList) {
        List<Integer> list = new ArrayList<>();
        for (SimpleObject so : soList) {
            list.add(so.getIntField());
        }
        return list;
    }
}
