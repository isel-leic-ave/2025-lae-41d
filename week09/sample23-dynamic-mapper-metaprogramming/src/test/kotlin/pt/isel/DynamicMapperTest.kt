package pt.isel

import ArtistSpotify2ArtistBaseline
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NaiveMapperTest {
    private val source = ArtistSpotify("Pearl Jam", "Band", 1,
        Country("USA", "English")
        /*listOf(
            Song("Jeremy", 1991),
            Song("Alive", 1991),
            Song("Daughter", 1993)
        )*/
    )
    @Test
    fun genBytecodeJavaBaselines() {
        val m = ArtistSpotify2ArtistBaseline()
        val dest = m.mapFrom(source)
        assertEquals(source.name, dest.name)
        assertEquals(source.kind, dest.kind)
        assertEquals(source.id, dest.id)
        assertEquals(source.country.name, dest.from.name)
        assertEquals(source.country.idiom, dest.from.idiom)
    }
    @Test
    fun mapArtistSpotifyToArtist() {
        val dest:Artist =
            loadDynamicMapper(ArtistSpotify::class, Artist::class)
                .mapFrom(source)
        assertEquals(source.name, dest.name)
        assertEquals(source.kind, dest.kind)
        assertEquals(source.id, dest.id)
        assertEquals(source.country.name, dest.from.name)
        assertEquals(source.country.idiom, dest.from.idiom)
        /*val tracks = dest.tracks.iterator()
        source.songs.forEach {
            val actual = tracks.next()
            assertEquals(it.name, actual.name)
            assertEquals(it.year, actual.year)
        }
        assertFalse { tracks.hasNext() }*/
    }
}