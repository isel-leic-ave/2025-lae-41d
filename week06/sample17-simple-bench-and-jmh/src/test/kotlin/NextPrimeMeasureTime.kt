import pt.isel.nextPrime
import kotlin.math.pow

public inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}

public inline fun measureTimeNano(block: () -> Unit): Long {
    val start = System.nanoTime()
    block()
    return System.nanoTime() - start
}

public inline fun statMeasureTime(currentTimeFunc: () -> Long, block: () -> Unit): Pair<Double, Double> {
    // Calculating mean and standard deviation of the elapsed time
    val listOfElapsedTime = mutableListOf<Long>()
    repeat(12){
        val start = currentTimeFunc()
        block()
        listOfElapsedTime.add(currentTimeFunc() - start)
    }
    listOfElapsedTime.remove(listOfElapsedTime.min()) // remove min
    listOfElapsedTime.remove(listOfElapsedTime.max()) // remove max
    //println(listOfElapsedTime)
    val mean = listOfElapsedTime.average()
    val variance = listOfElapsedTime.map { (it - mean).pow(2) }.average()
    return Pair(mean, Math.sqrt(variance))
    //println("Mean: $mean - Standard Deviation: ${Math.sqrt(variance)}")
}

fun main(){
    val n = 254L // small number

    // Measure time basics: with low time resolution (milliseconds)
    val t1 = System.currentTimeMillis()
    val p = nextPrime(n)
    println("Next prime of $n is $p")
    println("1) Elapsed time (n=$n): ${System.currentTimeMillis() - t1} ms")
    println()

    // With measureTimeNano function
    // Needs to remove I/O (println)
    measureTimeNano {
        val p = nextPrime(n)
        println("Next prime of $n is $p")
    }.also{
        println("2) Elapsed time (n=$n): ${it/1000000.0} ms") // 1 nano --> 10E-6 ms
    }
    println()

    // With 10 repetitions and no I/O
    repeat(10){
        measureTimeNano {
            val p = nextPrime(n)
        }.also{
            println("3) Elapsed time (n=$n): ${it/1000000.0} ms")
        }
    }
    println()

    // With 10 repetitions and 1000000 iterations for a small n
    val nIters = 1000000
    repeat(10){
        measureTimeNano {
            repeat(nIters) {
                val p = nextPrime(n)
            }
        }.also{
            val perOp = it/nIters
            println("4) Elapsed time (n=$n): ${perOp/1000000.0} ms")
        }
    }
    println()

    // Using statMeasureTime in nanoseconds.
    statMeasureTime(currentTimeFunc = {System.nanoTime()}){
        repeat(nIters) {
            val p = nextPrime(n)
        }
    }.also {
        val perOpAvg = it.first/nIters
        val perOpStdDev = it.second/nIters
        println("5) Elapsed time (n=$n): ${perOpAvg/1000000.0} ms")
        println("5) Std. Dev.: ${perOpStdDev/1000000.0} ms")
    }
    println()

    // For larger n, milliseconds has enough precision.
    // Also, nIters is no longer necessary.
    statMeasureTime(currentTimeFunc = {System.currentTimeMillis()}){
        val p = nextPrime(8000000L)
    }.also {
        println("6) Elapsed time (n=8000000L): ${it.first} ms")
        println("6) Std. Dev.: ${it.second} ms")
    }
}
