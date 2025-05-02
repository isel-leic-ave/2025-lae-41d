package pt.isel.sample24

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import pt.isel.NaiveMapper
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class DynamicMapperBenchmark {
    private val source = ArtistSpotify("Pearl Jam", "Band", 1,
        Country("USA", "English"),
        listOf(
            Song("Jeremy", 1991),
            Song("Alive", 1991),
            Song("Daughter", 1993)
        )
    )
    /*val mapper = NaiveMapper(ArtistSpotify::class, Artist::class)
    val mapperEnhanced = pt.isel.NaiveMapperEnhanced.mapper(ArtistSpotify::class, Artist::class)

    @Benchmark
    fun mapArtistSpotifyToArtistVersion4() {
        val dest: Artist = mapper.mapFrom(source)
    }

    @Benchmark
    fun mapArtistSpotifyToArtistVersion4Enhanced() {
        val dest: Artist = mapperEnhanced.mapFrom(source)
    }*/
    @Benchmark
    fun mapArtistSpotifyToArtistDynamic() {
        val dest: Artist =
            loadDynamicMapper(ArtistSpotify::class, Artist::class)
                .mapFrom(source)
    }
}