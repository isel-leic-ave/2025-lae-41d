package pt.isel

import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * 4rd version - recursive mapper and generics
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
                    .any { matchPropWithParam(it, param) }
                }
        }
    /**
     * 2nd - look for matching properties with ctor parameters
     */
    private val args: Map<KParameter, KProperty<*>?> = destCtor
        .parameters
        .associateWith { param -> srcType
            .memberProperties
            .firstOrNull() { matchPropWithParam(it, param) }
        }
        .filter { it.value != null }

    private fun matchPropWithParam(srcProp: KProperty<*>, param: KParameter) : Boolean {
        val fromName = srcProp.findAnnotation<MapProp>()?.paramName ?: srcProp.name
        if(fromName != param.name) {
            return false
        }
        val srcKlass = srcProp.returnType.classifier as KClass<*>
        return if(srcKlass.java.isPrimitive)
            srcProp.returnType == param.type
        else
            true
    }

    private fun matchPropWithParamOld(srcProp: KProperty<*>, param: KParameter) : Boolean {
        if(srcProp.name == param.name) {
            return srcProp.returnType == param.type
        }
        val annot = srcProp.findAnnotation<MapProp>() ?: return false
        return annot.paramName == param.name && srcProp.returnType == param.type
    }

    /**
     * 3rd - Get the values of properties from source and pass them
     * to te constructor through the callBy()
     */
    fun mapFrom(src: Any) : T {
        val args: Map<KParameter, Any?> = args
            .map { pair ->
                // pair.key to pair.value?.call(src) // Old
                val propVal = pair.value?.call(src)
                pair.key to parse(pair.value!!.returnType, pair.key.type, propVal)
            }
            .associate { it }
        return destCtor.callBy(args)
    }

    // Property parser considering:
    // - primitive and String values
    // - Objects non-primitives (recursively)
    // - Lists (recursively)
    private fun parse(srcType: KType, destType: KType, propVal: Any?): Any? {
        if(propVal == null) return null

        val srcKlass = srcType.classifier as KClass<*>
        val destKlass = destType.classifier as KClass<*>
        // Primitive or String case
        if(srcKlass.java.isPrimitive || srcKlass == String::class) {
            return propVal
        }
        // List case: use of generics
        if(srcKlass == List::class && destKlass == List::class) {
            // srcType and destType are Ktype.
            // arguments is a property of Ktype that returns a list of Ktype projections.
            // For instance, for List<Song>, Song is the type refered in the arguments list.
            // srcType.arguments[0].type?.classifier returns the class that represents the first type (e.g., Song).
            val elemSrcKlass = srcType.arguments[0].type?.classifier as KClass<*>
            val elemDestKlass = destType.arguments[0].type?.classifier as KClass<*>
            val mapper: NaiveMapper<*> = NaiveMapper(elemSrcKlass, elemDestKlass)
            return (propVal as List<Any>).map {
                mapper.mapFrom(it)
            }
        }
        // Object case:
        return NaiveMapper(srcKlass, destKlass).mapFrom(propVal)
        // Other cases (e.g., Array, Maps, Enum) need to be implemented
    }

    // Property parser considering:
    // - primitive and String values
    // - Objects non-primitives (recursively) - excepting Array, Collections, ...
    private fun parseWithoutList(srcType: KType, destType: KType, propVal: Any?): Any? {
        if(propVal == null) return null

        val srcKlass = srcType.classifier as KClass<Any>
        val destKlass = destType.classifier as KClass<Any>
        // Primitive or String case
        if(srcKlass.java.isPrimitive || srcKlass == String::class) {
            return propVal
        }

        return NaiveMapper(srcKlass, destKlass).mapFrom(propVal)
    }
}