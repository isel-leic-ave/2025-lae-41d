//class Person (val name: String)
//class Person (var name: String)

class Person(name: String, val surname: String) {
    var name: String = name
        set(value) {
            require(value.length <= 70)
            field = value
        }
        get(){
            return field.lowercase().replaceFirstChar{it.uppercase()}
        }
    val fullName: String
        get() {return "${name} ${surname}"}  // <=> val fullName get() = "${name} ${surname}"
}