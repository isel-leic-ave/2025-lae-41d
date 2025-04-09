object ElapsedTimeCalculator {
    private val startTime: Long = System.currentTimeMillis()
    fun elapsedTime(): Long {
        val currentTime = System.currentTimeMillis()
        return currentTime - startTime
    }
}

/*fun main(){
    println(ElapsedTimeCalculator.elapsedTime())
    Thread.sleep(1000)
    println(ElapsedTimeCalculator.elapsedTime())
}*/

