// LazyMap
class LazyMap<T, R>(
    private val source: Sequence<T>,
    private val transform: (T) -> R
) : Sequence<R> {

    override fun iterator(): Iterator<R> = object : Iterator<R> {
        private val sourceIterator = source.iterator()

        override fun hasNext(): Boolean {
            return sourceIterator.hasNext()
        }

        override fun next(): R {
            return transform(sourceIterator.next())
        }
    }
}

// Extension function for Sequence
fun <T, R> Sequence<T>.lazyMapExp(transform: (T) -> R): Sequence<R> {
    val sourceIterator = this@lazyMapExp
    return LazyMap(sourceIterator, transform)
}