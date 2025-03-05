package pt.isel

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NaiveMapperTest {
    @Test
    fun mapArtistSpotifyToArtistMutable() {
        val source = ArtistSpotify("Muse", "UK", "Band")
        val dest:ArtistMutable = source.mapToProps(ArtistMutable::class)
        assertEquals(source.name, dest.name)
        assertEquals(source.kind, dest.kind)
        assertEquals(source.country, dest.from)
    }
    @Test
    fun mapArtistSpotifyToArtist() {
        val source = ArtistSpotify("Muse", "UK", "Band")
        val dest:Artist = source.mapTo(Artist::class)
        assertEquals(source.name, dest.name)
        assertEquals(source.kind, dest.kind)
        assertEquals(source.country, dest.from)
    }
    @Test
    fun mapArtistSpotifyToArtistVersion3() {
        val mapper = NaiveMapper(ArtistSpotify::class, Artist::class)
        val source = ArtistSpotify("Muse", "UK", "Band")
        val dest:Artist = mapper.mapFrom(source)
        assertEquals(source.name, dest.name)
        assertEquals(source.kind, dest.kind)
        assertEquals(source.country, dest.from)
    }
}