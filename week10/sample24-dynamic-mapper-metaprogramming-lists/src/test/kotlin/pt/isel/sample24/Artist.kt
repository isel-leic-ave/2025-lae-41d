package pt.isel.sample24

class Artist(
    val name: String,
    val kind: String,
    val id: Int,
    val from: State, // = State("", ""),
    val tracks: List<Track>// = emptyList()
)