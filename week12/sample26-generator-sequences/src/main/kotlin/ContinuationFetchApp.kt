import kotlinx.coroutines.runBlocking
import java.net.URI
import kotlin.coroutines.*

fun main() {
    // A coroutine to run the suspend functions synchronously
    runBlocking {

        // Call the fetch suspend function
        val data = fetch("https://github.com/")
        processData(data)
    }

    val cont = Continuation (
        context = EmptyCoroutineContext,
        resumeWith = { result: Result<String> ->
            processData(result.getOrThrow())
        }
    )
    // Call the fetchCps with an explicit Continuation.
    fetchCps("https://stackoverflow.com/", cont)

    runBlocking {
        // Transform the fetchCps in a suspend function.
        val data = suspendCoroutine{ continuation: Continuation<String> ->
            fetchCps("https://kotlinlang.org/", continuation)
        }
        processData(data)
    }

}

suspend fun fetch(path: String): String {
    println("Fetching $path")
    //Thread.sleep(1000)
    return URI(path).toURL().readText()
}

fun fetchCps(path: String, onComplete: Continuation<String>) {
    println("Fetching $path")
    val body = URI(path).toURL().readText()
    onComplete.resume(body)
}


fun processData(data: String){
    println(data.substringAfter("<title>").substringBefore("</title>"))
}
