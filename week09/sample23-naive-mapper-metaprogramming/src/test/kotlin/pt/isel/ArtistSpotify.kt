package pt.isel

class ArtistSpotify(
    val name: String,
    val kind: String,
    val id: Int,
    @MapProp("from") val country: Country,
    //@MapProp("tracks") val songs: List<Song>
)