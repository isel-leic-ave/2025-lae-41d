package pt.isel

import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

fun main(){
    val klass = Unit::class.java.classLoader
        .loadClass("pt.isel.Bar")
        .kotlin
    val obj = klass.createInstance()
    val foo = klass.functions.first { it.name == "foo" }
    println(foo.call(obj)) // prints 67895
}