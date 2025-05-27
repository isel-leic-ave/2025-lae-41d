import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.File
import java.net.URI
import javax.imageio.ImageIO

class TempImageFinalize(url: String) : Closeable {
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

    override fun close() {
        println("Try deleting file...")
        if(file.exists()) {
            file.delete()
        }
    }

    /**
     * Deprecated !!! => replaced by Cleaners
     */
    protected fun finalize() {
        close()
    }
}