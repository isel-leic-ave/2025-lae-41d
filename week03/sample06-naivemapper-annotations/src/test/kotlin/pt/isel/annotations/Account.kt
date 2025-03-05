package pt.isel.annotations

@Color("Green")
class Account(
    @property:[Color("Blue") Tag] @Color("Yellow") var balance: Long,
    // @property:Color("Blue") @Color("Yellow") @property:Tag var balance: Long, // Equivalent to the above line
    @property:Color("Red") val owner: String
) {
    fun deposit(@Tag value: Long) {
        balance += value
    }
}