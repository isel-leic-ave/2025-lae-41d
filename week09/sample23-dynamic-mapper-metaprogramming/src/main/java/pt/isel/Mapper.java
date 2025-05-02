package pt.isel;

import java.util.List;

public interface Mapper<T, R> {

    R mapFrom(T src);

    /*default List<R> mapFromList(List<T> src) {
        return src.stream().map(it -> mapFrom(it)).toList();
    }*/
}
