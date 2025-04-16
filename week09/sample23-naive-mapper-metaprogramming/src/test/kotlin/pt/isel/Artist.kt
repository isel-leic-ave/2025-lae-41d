package pt.isel

class Artist(
    val name: String,
    val kind: String,
    val id: Int,
    val from: State, // = State("", ""),
    //val tracks: List<Track> = emptyList()
)