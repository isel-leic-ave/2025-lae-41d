package pt.isel

class Artist(
    val name: String,
    val from: State, // = State("", ""),
    val kind: String,
    val tracks: List<Track> = emptyList()
){
    override fun toString(): String {
        return "name: $name, from: $from, kind: $kind, tracks: ${tracks.joinToString(", ", prefix = "[", postfix = "]")}"
    }
}