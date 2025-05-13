package pt.isel.weather

import java.util.Spliterators.AbstractSpliterator
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.stream.StreamSupport


// With class implementing Sequence<T>
fun <T> Sequence<T>.interleaveExt(other: Sequence<T>) : Sequence<T> {
    return InterleavingSequence(this, other)
}

class InterleavingSequence<T>(self: Sequence<T>, other: Sequence<T>) : Sequence<T> {
    val selfIter = self.iterator()
    val otherIter = other.iterator()
    var count = 0
    override fun iterator(): Iterator<T> {

        return object : Iterator<T> {
            override fun hasNext(): Boolean {
                return selfIter.hasNext() || otherIter.hasNext()
            }
            override fun next(): T {
                if (count % 2 == 0)
                    if (selfIter.hasNext()){
                        count++
                        return selfIter.next()
                    }
                    else return otherIter.next()
                else
                    if (otherIter.hasNext()){
                        count++
                        return otherIter.next()
                    }
                    else return selfIter.next()
            }
        }
    }
}

// With Stream (Java)
fun <T> Stream<T>.interleave(other: Stream<T>) : Stream<T> {
    val res = object : AbstractSpliterator<T>(Long.MAX_VALUE, ORDERED) {
        val selfIter = this@interleave.spliterator()
        val otherIter = other.spliterator()
        var count = 0

        override fun tryAdvance(consumer: Consumer<in T>): Boolean {
            if (count % 2 == 0) {
                if (selfIter.tryAdvance(consumer)) {
                    count++
                    return true
                } else {
                    return otherIter.tryAdvance(consumer)
                }
            }
            else {
                if(otherIter.tryAdvance(consumer)){
                    count++
                    return true
                }
                else {
                    return selfIter.tryAdvance(consumer)
                }
            }
        }
    }
    return StreamSupport.stream(res, false)
}

// With sequence generation
fun <T> Sequence<T>.interleave(other: Sequence<T>) : Sequence<T> {
    return sequence {
        val selfIter = this@interleave.iterator()
        val otherIter = other.iterator()
        while(selfIter.hasNext()) {
            yield(selfIter.next())
            if(otherIter.hasNext())
                yield(otherIter.next())
        }
        while(otherIter.hasNext()) {
            yield(otherIter.next())
        }
    }
}

fun <T> Sequence<T>.interleaveGen(other: Sequence<T>) : Sequence<T> {
    val selfIter = this@interleaveGen.iterator()
    val otherIter = other.iterator()
    var count = 0
    return generateSequence {
        if (count % 2 == 0) {
            if (selfIter.hasNext()) {
                count++
                selfIter.next()
            } else if (otherIter.hasNext()) otherIter.next() else null
        }
        else {
            if (otherIter.hasNext()) {
                count++
                otherIter.next()
            } else if (selfIter.hasNext()) selfIter.next() else null
        }
    }
}