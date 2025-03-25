package pt.isel

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.infra.Blackhole

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class NextPrimeJmh {
    private val n = 8L

    @Benchmark
    //@OutputTimeUnit(TimeUnit.NANOSECONDS)
    fun benchNextPrimeWithBH(blackhole: Blackhole) {
        blackhole.consume(nextPrime(n))
    }

    @Benchmark
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    fun benchNextPrime() {
        nextPrime(7000000L)
    }
}
