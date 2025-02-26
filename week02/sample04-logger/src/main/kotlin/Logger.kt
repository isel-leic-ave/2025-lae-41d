package pt.isel

import kotlin.reflect.KCallable
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

fun Appendable.log(obj: Any) {
    this.appendLine("Object of Type ${obj::class.simpleName}")
    obj::class.memberProperties.forEach { prop ->
        prop.isAccessible = true // e.g., if the property is private
        this.appendLine("  - ${prop.name}: ${prop.call(obj)}")
    }
}

fun Appendable.logGetters(obj: Any) {
    this.appendLine("Object of Type ${obj::class.simpleName}")
    obj::class
        .members
        .filter { isGetter(it) }
        .forEach { func ->
            func.isAccessible = true
            appendLine("  - ${func.name.substring(3)}: ${func.call(obj)}")
        }
}

fun isGetter(m: KCallable<*>): Boolean {
    return m.name.startsWith("get")
            && m.parameters.size == 1
            && m.instanceParameter == m.parameters[0]
            && m.returnType.classifier != Unit::class
            // && m.returnType != typeOf<Unit>() // the same as the line above
}