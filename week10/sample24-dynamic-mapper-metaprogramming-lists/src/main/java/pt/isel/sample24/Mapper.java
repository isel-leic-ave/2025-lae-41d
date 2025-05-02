package pt.isel.sample24;

import java.util.List;
import java.util.ArrayList;

public interface Mapper<T, R> {

    R mapFrom(T src);

    default List<R> mapFromList(List<T> src) {
        List<R> result = new ArrayList<R>();
        for (T t : src) {
            result.add(mapFrom(t));
        }
        return result;
    }
}
