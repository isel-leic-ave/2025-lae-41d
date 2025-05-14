fun <T, R> Sequence<T>.lazyMap(transform: (T) -> R): Sequence<R> {
    return sequence {
        for (element in this@lazyMap) {
            yield(transform(element))
        }
    }
}