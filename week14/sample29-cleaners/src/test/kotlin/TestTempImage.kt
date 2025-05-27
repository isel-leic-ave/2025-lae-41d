import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import kotlin.test.Test

class TestTempImage {
    val filename = "java-turning-30.png"
    val url = "https://dev.java/assets/images/${filename}"
    @Test
    fun `testing finalize method`(){
        fun loadImageAndForgetClose() {
            TempImageFinalize(url)
                .also {
                    assertTrue(it.downloaded)
                }
        }
        loadImageAndForgetClose()
        assertTrue(File(filename).exists())
        /*
        * Once an object is eligible for GC, finalization may occur on a different thread,
        * so you might need to pause briefly to observe the changes.
        */
        System.gc()
        Thread.sleep(100)
        assertFalse(File(filename).exists())

    }

    @Test
    fun `testing Cleanable`(){
        fun loadImageAndForgetClose() {
            TempImageCleanable(url)
                .also {
                    assertTrue(it.downloaded)
                }
        }
        loadImageAndForgetClose()
        assertTrue(File(filename).exists())
        /*
        * Once an object is eligible for GC, finalization may occur on a different thread,
        * so you might need to pause briefly to observe the changes.
        */
        System.gc()
        Thread.sleep(100)
        assertFalse(File(filename).exists())

    }

}
