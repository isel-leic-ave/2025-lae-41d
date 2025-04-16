package pt.isel

import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

private const val packageName = "pt.isel"

fun main() {

    val klass = Unit::class.java.classLoader
        .loadClass("$packageName.Bar")
        .kotlin
    val objBar = klass.createInstance() // <=> val objBar = new Bar()
    val foo = klass.functions.first { it.name == "foo" }
    println(foo.call(objBar)) // <=> objBar.foo()

    // Using interface Sum for casting the object
    val counter: Sum = Unit::class.java.classLoader
        .loadClass("$packageName.Counter")
        .kotlin
        .constructors.first()
        .call(7) as Sum // <=> val count = new Count(7)

    println(counter.add(11)) // 18
}