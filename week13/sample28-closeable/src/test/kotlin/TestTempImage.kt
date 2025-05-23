import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import kotlin.test.Test

class TestTempImage {
    val filename = "java-turning-30.png"
    val url = "https://dev.java/assets/images/${filename}"
    @Test
    fun `simple test TempImage`() {
        TempImage(url).use {
            println("The file temporarily exists in: ${it.pathname}")
            println("Finishing the resource usage... The resource will be removed...")
        }
    }

    @Test
    fun `testing the requirements of TempImage`() {
        TempImage(url)
            .use {
                // Requirement 1: at the first time, the file should be downloaded
                assertTrue(it.downloaded)
                TempImage(url)
                    .use { second ->
                        // Requirement 2: at the second time, the file already exists
                        assertFalse(second.downloaded)
                        assertTrue(File(filename).exists())
                    }
            }
        // Requirement 3: at the end, the file should not exist
        assertFalse(File(filename).exists())
    }
}
