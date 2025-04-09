package pt.isel

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties

/**
 * 2nd (2.1) Version of NaiveMapper with a NaiveMapper Class definition
 */
class NaiveMapper<T : Any>(val srcType: KClass<*>, val destType:KClass<T>) {
    /**
     * 1st - selects the constructor with all mandatory parameters
     * in the properties of the source.
     */
    private val destCtor = destType
        .constructors
        .first { ctor ->
            ctor
                .parameters
                .filter { !it.isOptional }
                .all { param -> srcType
                    .memberProperties
                    .any { it.name == param.name && it.returnType == param.type}
                }
        }
    /**
     * 2nd - look for matching properties with ctor parameters
     */
    private val args: Map<KParameter, KProperty<*>?> = destCtor
        .parameters
        .associateWith { param -> srcType
            .memberProperties
            .firstOrNull() { it.name == param.name && it.returnType == param.type }
        }
        .filter { it.value != null }
    /**
     * 3rd - Get the values of properties from source and pass them
     * to te constructor through the callBy()
     */
    fun mapFrom(src: Any) : T {
        val args: Map<KParameter, Any?> = args
            .map { pair -> pair.key to pair.value?.call(src) }
            .associate { it }
        return destCtor.callBy(args)
    }
}

/**
 * 2nd Version of NaiveMapper
 * Copy properties to the parameters of the constructor of dest.
 * It selects the constructor with all mandatory parameters
 * in the properties of the source.
 */
fun <T : Any> Any.mapTo(dest: KClass<T>) : T {
    /**
     * 1st - selects the constructor with all mandatory parameters
     * in the properties of the source.
     */
    val destCtor = dest
        .constructors
        .first { ctor ->
            ctor
                .parameters
                .filter { !it.isOptional }
                .all { param -> this::class
                    .memberProperties
                    .any { it.name == param.name && it.returnType == param.type}
                }
        }
    /**
     * 2nd - look for matching properties with constructor parameters
     */
    val args: Map<KParameter, Any?> = destCtor
        .parameters
        .associateWith { param -> this::class
            .memberProperties
            .firstOrNull() { it.name == param.name && it.returnType == param.type }
            ?.call(this)
        }
        .filter { it.value != null }
    args.forEach { (k, v) -> println("${k} -> $v") }
    /**
     * 3rd create the instance of dest via constructor using
     * the callBy<Map<KParameter, Any>>
     */
    return destCtor.callBy(args)
}