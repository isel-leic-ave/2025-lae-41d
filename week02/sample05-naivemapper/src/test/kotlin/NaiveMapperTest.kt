package pt.isel

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NaiveMapperTest {
    @Test
    fun mapArtistSpotifyToArtistLastFm() {
        val source = ArtistSpotify("Muse", "UK")
        // Properties in class Artist need to be declared as var.
        // For this reason, we use ArtistLastFm instead.
        val dest:ArtistLastFm = source.mapToProps(ArtistLastFm::class)
        assertEquals(source.name, dest.name)
        // 'country' and 'origin' are not yet associated in this implementation.
        assertEquals("", dest.origin)
    }
    @Test
    fun mapArtistSpotifyToArtist() {
        val source = ArtistSpotify("Muse", "UK")
        val dest:Artist = source.mapTo(Artist::class)
        assertEquals(source.name, dest.name)
        // 'country' and 'from' are not yet associated in this implementation.
        assertEquals("", dest.from)
    }
    @Test
    fun mapArtistSpotifyToArtistVersion3() {
        val mapper = NaiveMapper(ArtistSpotify::class, Artist::class)
        val source = ArtistSpotify("Muse", "UK")
        val dest:Artist = mapper.mapFrom(source)
        assertEquals(source.name, dest.name)
        // 'country' and 'from' are not yet associated in this implementation.
        assertEquals("", dest.from)
    }
}