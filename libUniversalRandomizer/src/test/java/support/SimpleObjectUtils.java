package support;


import java.util.ArrayList;
import java.util.List;

public class SimpleObjectUtils {
    public static List<Integer> toIntFieldList(List<SimpleObject> soList) {
        List<Integer> list = new ArrayList<>();
        for (SimpleObject so : soList) {
            list.add(so.getIntField());
        }
        return list;
    }
}
