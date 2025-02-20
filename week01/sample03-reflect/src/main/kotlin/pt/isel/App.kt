package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberProperties

/**
 * Represents the property Country in class Person
 */
val countryOfPerson: KProperty<*> = Person::class
    .memberProperties
    .first { it.name == "country" }

class Account(val country: String)

fun main() {
    // val klass: KClass<Person> = Person::class
    val maria = Person("Maria", "Portugal")
    val pedro = Person("Pedro", "Brasil")
    checkDeclaredMembers(maria)

    // ??? get the value of property country through countryProperty
    println("Country of Maria: ${countryOfPerson.call(maria)}")
    println("Country of Pedro: ${countryOfPerson.call(pedro)}")

    val stdManu = Student("Manuela", "Italy")
    checkCountry(stdManu) // Ok, uses the object as reference
    checkCountry(Account("Spain")) // Ok, uses the object as reference
    /**
     * IllegalArgumentException: object is not an instance of declaring class
     */
    //println("Country of Manuela: ${countryOfPerson.call(stdManu)}")

    /**
     * NoSuchElementException: Collection contains no element matching the predicate.
     */
    //checkCountry("ISEL")

}

fun checkCountry(obj: Any) {
    val propCountry = obj::class
        .memberProperties
        .first { it.name == "country" }
    println(propCountry.call(obj))
}

fun checkMembers(obj: Any) {
    val klass: KClass<*> = obj::class
    klass.members.forEach { println(it.name) }
}

fun checkDeclaredMembers(obj: Any) {
    val klass: KClass<*> = obj::class
    klass.declaredMembers.forEach { println(it.name) }
}