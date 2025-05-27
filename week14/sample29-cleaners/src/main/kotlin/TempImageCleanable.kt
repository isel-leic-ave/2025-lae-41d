import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.File
import java.lang.ref.Cleaner
import java.net.URI
import javax.imageio.ImageIO

class TempImageCleanable(url: String) : Closeable {
    val img: BufferedImage
    val downloaded: Boolean
    private val file = File(url.substringAfterLast('/'))
    init {
        if(file.exists()) {
            img = ImageIO.read(file) // read from local file
            downloaded = false
        } else {
            val conn = URI(url).toURL().openConnection()
            val kind = conn.getHeaderField("Content-Type").substringAfterLast('/')
            conn.getInputStream().use { stream ->
                img = ImageIO.read(stream)
                // Some images can return null
                if (img == null) { throw IllegalArgumentException("Failed to decode image from URL: $url") }
                // read from the connection stream
                ImageIO.write(img, kind, file)
                downloaded = true
            }
        }
    }
    val pathname: String
        get() {
            return if(file.exists()) {file.absolutePath} else {""}
        }

    /* Cleanable implementation: */

    // A Cleaner must be a static field (i.e., a field of the class)
    private companion object {
        val cleaner: Cleaner = Cleaner.create()
    }

    // action is an object of a type that implements Runnable
    private val action = object : Runnable {
        private val file = File(url.substringAfterLast('/'))
        override fun run() {
            println("Try deleting file ${file.absolutePath}...")
            if(file.exists()) {
                file.delete()
            }
        }
    }

    // Registering the action
    private val cleanable = cleaner.register(this, action)

    // close method calls the clean method of the cleanable
    override fun close() {
        cleanable.clean()
    }
}