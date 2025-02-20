class Lamp {
    // properties (data member)
    var isOn: Boolean = false
    val color: String = "White"

    // member function
    fun turnOn() {
        isOn = true
        println("Turn on")
    }

    // member function
    fun turnOff() {
        isOn = false
        println("Turn off")
    }

    // member function
    fun getState(): Boolean {
        return isOn
    }
}