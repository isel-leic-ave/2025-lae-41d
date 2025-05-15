enum class PauseState {
    ValueReady,
    ValueNotReady,
    Finished
}

// Similar to Continuation, only with resume part (which is a function with the next operation)
typealias SimpleContinuation = () -> Unit

// Simple implementation (for academic purpose) based on:
// https://github.com/JetBrains/kotlin/blob/whyoleg/dokka2-sync-stdlib/libraries/stdlib/src/kotlin/collections/SequenceBuilder.kt
class SimpleSequenceIterator<T : Any>(firstPart: SimpleSequenceIterator<T>.() -> Unit): Iterator<T> {
    private var state: PauseState = PauseState.ValueNotReady
    private var value: T? = null
    private var nextPart: SimpleContinuation? = { firstPart() }

    fun yield(value: T, nextPart: SimpleContinuation) {
        this.value = value
        this.nextPart = nextPart
        state = PauseState.ValueReady
    }

    override fun hasNext(): Boolean {
        while(true) {
            when(this.state) {
                PauseState.ValueReady -> return true
                PauseState.Finished -> return false
                PauseState.ValueNotReady -> { /* Do nothing */}
            }
            this.state = PauseState.Finished
            nextPart!!() // Resume
        }
    }

    override fun next() = when(state) {
        PauseState.Finished -> throw NoSuchElementException()
        PauseState.ValueReady -> {
            state = PauseState.ValueNotReady
            value!!
        }
        PauseState.ValueNotReady -> {
            if(!hasNext()) throw NoSuchElementException()
            value!!
        }
    }
}

// Similar to a sequence generator
fun <T: Any> buildSimpleSequence(block: SimpleSequenceIterator<T>.() -> Unit): Iterator<T>{
    return SimpleSequenceIterator<T>(block)
}

fun main() {
    val seq = buildSimpleSequence<Int> {
        var value = 1
        println("Yielding 1 and pausing...")
        yield(value++) {
            println("Resumed after 1, yielding 2 and pausing...")
            yield(value++) {
                println("Resumed after 2, yielding 3 and pausing...")
                yield(value++) {
                    println("Resumed after 3, and finishing")
                }
            }
        }
    }

    println("Before consume the sequence")
    seq.forEach {
        println(it)
        println("-----")
    }
}