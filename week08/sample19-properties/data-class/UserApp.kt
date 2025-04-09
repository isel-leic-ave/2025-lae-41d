class UserClass(val name: String, val age: Int)
data class UserData(val name: String, val age: Int){
    fun x(){
        println(name)
    }
}

fun main(){
    val userClass1 = UserClass("Bob", 30)
    val userClass2 = UserClass("Bob", 30)
    println("toString() of userClass1: ${userClass1.toString()}")
    println("hashCode() of userClass1: ${userClass1.hashCode()}")
    println("hashCode() of userClass2: ${userClass2.hashCode()}")
    println("userClass1 == userClass2? ${userClass1.equals(userClass2)}")

    val userData1 = UserData("Maria", 20)
    val userData2 = UserData("Maria", 20)
    println("[userData1] overrided toString(): ${userData1.toString()}")
    println("[userData1] overrided hashCode(): ${userData1.hashCode()}")
    println("[userData2] overrided hashCode(): ${userData2.hashCode()}")
    println("userData1 == userData2? ${userData1.equals(userData2)}")

    println("[userData1] component1(): ${userData1.component1()}")
    println("[userData1] component2(): ${userData1.component2()}")
    val (name, age) = userData1 // access component1 and component2
    println("[userData1] destructuring: $name, $age")

    // Copy all and compare with equals
    val userData3 = userData1.copy()
    println("[userData3] override hashCode(): ${userData3.hashCode()}")
    println("userData1 == userData3? ${userData1.equals(userData3)}")

    // Copy and replace the value of 'age'
    val userData4 = userData1.copy(age = 25)
    println("userData4: $userData4")
    println("[userData4] override hashCode(): ${userData4.hashCode()}")
    println("userData4 == userData1? ${userData4.equals(userData1)}")
}