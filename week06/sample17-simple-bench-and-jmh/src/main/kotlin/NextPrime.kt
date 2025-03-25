package pt.isel

fun nextPrime(n: Long): Long{
    if(!isPrime(n)) return nextPrime(n+1)
    return n
}

fun isPrime(number: Long): Boolean {
    if (number <= 1L) {
        return false
    }

    for (i in 2 until number) {
        if (number % i == 0L) {
            return false
        }
    }
    return true
}
