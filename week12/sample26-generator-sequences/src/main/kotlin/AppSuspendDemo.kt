fun main() {

    val seq = sequence<Int> {
        println(" -> Generating first")
        yield(1)
        println(" -> Generating second")
        yield(2)
        println(" -> Generating third")
        yield(3)
        println(" -> Done")
    }

    val seq2 = generateSequence(1) {
        if(it < 3) {
            println(" ---> Generating ${it + 1}")
            it + 1
        }
        else{
            println(" ---> Done")
            null
        }
    }

    //println(seq.iterator().hasNext())
    //println(seq.iterator().hasNext())
    seq.forEach { num ->
        println("The next number is $num")
        println("Sleeping 3 second...")
        Thread.sleep(3000)
    }

    seq2.forEach { num ->
        println("The next number is $num")
        println("Sleeping 1 second...")
        Thread.sleep(1000)
    }
}