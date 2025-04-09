// Function declaration
fun sum1(a: Int, b: Int): Int {
    return a + b
}

// Single-expression function
fun sum2(a: Int, b: Int) = a + b

// Nested functions sample
fun funcOuter() {
    fun funcInner1() {
        fun funcInner2() {
        }
    }
}

//fun main(args: Array<String>) {
fun main() {
        // Anonymous function
        val sum3 = fun(a: Int, b: Int): Int { return a + b }
        // Lambda functions
        // (A, B) -> C
        val sum4: (Int, Int) -> Int = { a: Int, b: Int -> a + b }
        // val sum4 = { a: Int, b: Int -> a + b } // the same (without the annotation)
        val concat = { a: String, b: String -> a + b }

        // Function literals with receiver (similar to extension functions)
        // A.(B) -> C
        val sum5 = fun Int.(b: Int): Int = this + b
        val sum6: Int.(Int) -> Int = { b -> this + b } // <=> Int.(Int) -> Int = { this + it }

        println("sum1: " + sum1(3, 4))
        println("sum2: " + sum2(3, 4))
        println("sum3: " + sum3(3, 4))
        println("sum4: " + sum4(3, 4))
        println("sum5: " + 3.sum5(4))
        println("sum6: " + 3.sum6(4))
        println("concat: " + concat("IS", "EL"))
}