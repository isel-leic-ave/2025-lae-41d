import kotlin.reflect.KClass
import kotlin.reflect.full.*

fun main() {
    val lampClass: KClass<Lamp> = Lamp::class  // Get the Lamp class reference
    println("Class name: ${lampClass.simpleName}") // Access class name
    //println("Complete class name: ${lampClass.qualifiedName}")

    println("Members:")
    val members = lampClass.members // Returns a collection of KCallable
    members.forEach { println("  " + it.name) }

    println("Declared Members:")
    val decMembers = lampClass.declaredMembers // Returns a collection of KCallable
    decMembers.forEach { println("  " + it.name) }

    println("Members Properties:")
    val memberProps = lampClass.memberProperties // Returns a collection of KProperty1<T, *>
    memberProps.forEach { println("  " + it.name) }

    println("Members Functions:")
    val memberFuncs = lampClass.memberFunctions // Returns a collection of KCallable
    memberFuncs.forEach { println("  " + it.name) }


    println("Superclasses:")
    val superclasses = lampClass.allSuperclasses
    superclasses.forEach { println("  " + it.simpleName) }

    val lamp1 = Lamp() // Create an object Lamp
    // Access properties and methods (class and superclass) dynamically
    println("lamp1 object member properties:")
    val lampMemberProps = lamp1::class.memberProperties
    lampMemberProps.forEach {
        println(" name:  " + it.name);
        println(" value: " + it.call(lamp1))
    }
    println("lamp1 object member functions:")
    //val lampMemberFuncs = lamp1::class.declaredMemberFunctions
    val lampMemberFuncs = lamp1::class.memberFunctions
    lampMemberFuncs.forEach {
        println(" name:  " + it.name);
        println(" parameters (size: ${it.parameters.size}):")
        it.parameters.forEach { println("   ${it.name} ${it.kind} ${it.type}") }
        if (it.parameters.size <= 1) // Not the best way to do that
            println(" value: " + it.call(lamp1))
        if (it.parameters.size == 2) // Not the best way to do that
            println(" value: " + it.call(lamp1, null))
    }

    val lamp2 = lampClass.createInstance() // Create an object Lamp without defined constructor
    // Access properties and methods (class and superclass) dynamically
    println("lamp2 object member properties:")
    val lampMemberProps2 = lamp2::class.memberProperties
    lampMemberProps2.forEach {
        println(" name:  " + it.name);
        println(" value: " + it.call(lamp2))
    }
}