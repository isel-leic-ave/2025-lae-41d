package pt.isel

import java.io.File
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.*
import java.lang.classfile.Interfaces
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.*
import java.lang.constant.MethodTypeDesc
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

private const val packageName = "pt.isel"
private val packageFolder = packageName.replace(".", "/")

private val root =
    Unit::class.java
        .getResource("/")
        ?.toURI()
        ?.path
        ?: "${System.getProperty("user.dir")}/"

/**
 * A new ClassLoader is required when the existing one loads classes from a JAR
 * and its resource path is null. In such cases, we create a ClassLoader that uses
 * the current working directory, as specified by the 'user.dir' system property.
 */
private val rootLoader = URLClassLoader(arrayOf(File(root).toURI().toURL()))

/**
 * Cache of dynamically generated mappers keyed by the domain class.
 * Prevents repeated code generation and loading.
 */
private val mappers = mutableMapOf< Pair< KClass<*>, KClass<*> >, Mapper<*, *> >()

/**
 * Loads a dynamic mapper instance for the given domain class using its Java `Class`
 * representation. Delegates to the Kotlin version of `loadDynamicMapper`.
 */
fun <T : Any, R : Any> loadDynamicMapper(
    srcType: Class<T>,
    destType: Class<R>,
) = loadDynamicMapper(srcType.kotlin, destType.kotlin)

/**
 * Loads or creates a dynamic mapper instance for the given domain class.
 * If not already cached, it generates the class using a builder, loads it, and instantiates it.
 */
fun <T : Any, R : Any> loadDynamicMapper(
    srcType: KClass<T>,
    destType: KClass<R>,
) = mappers.getOrPut(srcType to destType) {
    buildMapperClassfile(srcType, destType)
        .createInstance() as Mapper<*, *>
} as Mapper<T, R>

/**
 * Generates the class file for a mapper based on the structure of the given domain classes.
 * Uses code generation techniques (e.g., Class-File API) to build the repository implementation at runtime.
 *
 * @param src the Kotlin class of the source domain type.
 * @param dest the Kotlin class of the destination domain type.
 * @return the runtime-generated class implementing the repository logic.
 */
private fun <T : Any, R : Any> buildMapperClassfile(
    src: KClass<T>,
    dest: KClass<R>,
): KClass<out Any> {
    val className = "${src.simpleName}2${dest.simpleName}"
    buildMapperByteArray(className, src, dest)
    return rootLoader
        .loadClass("$packageName.$className")
        .kotlin
}

fun <T : Any, R : Any> buildMapperByteArray(
    className: String,
    src: KClass<T>,
    dest: KClass<R>,
) {
    val destInit: KFunction<R> = findConstructor(src, dest)
    val props: Map<KProperty<*>, KParameter> = findMatchingProperties(src, destInit)

    val classDesc = ClassDesc.of(packageName, className)
    val mapperInterface = Interfaces.ofSymbols(Mapper::class.descriptor())
    val bytes = ClassFile.of()
        .build(classDesc) { clb: ClassBuilder ->
            clb.withInterfaces(mapperInterface.interfaces())
            clb.withMethod(INIT_NAME, MethodTypeDesc.of(CD_void), ACC_PUBLIC) { mb ->
                mb.withCode { cb ->
                    cb
                        .aload(0)
                        .invokespecial(CD_Object, INIT_NAME, MTD_void)
                        .return_()
                }
            }
            clb.withMethod("mapFrom", MethodTypeDesc.of(CD_Object, CD_Object), ACC_PUBLIC or ACC_BRIDGE or ACC_SYNTHETIC) { mb ->
                mb.withCode { cob ->
                    cob.aload(0)
                    cob.aload(1)
                    cob.checkcast(src.descriptor())
                    // 1st argument: The class that owns the property (e.g., ArtistSpotify)
                    cob.invokevirtual(classDesc, "mapFrom", MethodTypeDesc.of(dest.descriptor(), src.descriptor()))
                    cob.areturn()
                }
            }
            clb.withMethod("mapFrom", MethodTypeDesc.of(dest.descriptor(), src.descriptor()), ACC_PUBLIC) { mb ->
                mb.withCode { cob ->
                    cob.new_(dest.descriptor())
                    cob.dup()
                    props.forEach { srcProp, destParam ->
                        // Primitive or String case:
                        if (srcProp.returnType.toKClass().isPrimitiveOrString()) {
                            cob.aload(1)
                            cob.invokevirtual(
                                src.descriptor(),
                                srcProp.javaGetter?.name,
                                MethodTypeDesc.of(srcProp.returnType.descriptor())
                            )
                        }
                        else {
                            // Object Case
                            cob.ldc(cob.constantPool().classEntry(srcProp.returnType.descriptor()))
                            cob.ldc(cob.constantPool().classEntry(destParam.type.descriptor()))
                            cob.invokestatic(
                                ClassDesc.of("pt.isel.DynamicMapperKt"),
                                "loadDynamicMapper",
                                MethodTypeDesc.of(
                                    Mapper::class.descriptor(),
                                    Class::class.descriptor(),
                                    Class::class.descriptor()
                                ),
                            )
                            cob.aload(1)
                            cob.invokevirtual(
                                src.descriptor(),
                                srcProp.javaGetter?.name,
                                MethodTypeDesc.of(srcProp.returnType.descriptor())
                            )
                            cob.invokeinterface(
                                Mapper::class.descriptor(),
                                //classDesc,
                                "mapFrom",
                                MethodTypeDesc.of(CD_Object, CD_Object),
                            )
                            cob.checkcast(destParam.type.descriptor())
                        }
                    }

                    cob.invokespecial(
                        dest.descriptor(),
                        INIT_NAME,
                        MethodTypeDesc.of(CD_void, destInit.parameters.map { it.type.descriptor() }),
                    )
                    cob.areturn()
                }
            }
            // An idea for better code organization: create an extension function for a ClassBuilder
            //clb.withMapFrom(classDesc, src, dest, props, destInit)
        }
    File("${root}$packageFolder/$className.class")
        .also { it.parentFile.mkdirs() }
        .writeBytes(bytes)
}

fun <T : Any, R : Any> findMatchingProperties(
    src: KClass<T>,
    destInit: KFunction<R>,
): Map<KProperty<*>, KParameter> =
    destInit
        .parameters
        .associateBy { param ->
            src
                .memberProperties
                .firstOrNull { prop -> matchPropertyToParam(prop, param) }
        }.filterKeys { it != null } as Map<KProperty<*>, KParameter>

fun matchPropertyToParam(
    prop: KProperty<*>,
    param: KParameter,
): Boolean {
    // Consider the name in the Match annotation if it exists,
    // or the property's name otherwise
    val propName = prop.findAnnotation<MapProp>()?.paramName ?: prop.name
    if (propName != param.name) {
        // If source property and destination parameter have different names
        return false
    }
    val classifier = prop.returnType.classifier
    check(classifier is KClass<*>)
    if (classifier.java.isPrimitive || classifier == String::class) {
        return prop.returnType == param.type
    }
    return true
}

fun <T : Any, R : Any> findConstructor(
    src: KClass<T>,
    dest: KClass<R>,
): KFunction<R> =
    dest
        .constructors
        .first {
            it
                .parameters
                .filter { param -> !param.isOptional }
                .all { param ->
                    src
                        .memberProperties
                        .any { prop -> matchPropertyToParam(prop, param) }
                }
        }


/**
 * Returns a ClassDesc of the type descriptor of the given KClass.
 */
fun KClass<*>.descriptor(): ClassDesc =
    if (this.java.isPrimitive) {
        when (this) {
            Char::class -> CD_char
            Short::class -> CD_short
            Int::class -> CD_int
            Long::class -> CD_long
            Float::class -> CD_float
            Double::class -> CD_double
            Boolean::class -> CD_boolean
            else -> {
                throw IllegalStateException("No primitive type for ${this.qualifiedName}!")
            }
        }
    } else {
        ClassDesc.of(this.java.name)
    }

/**
 * Returns a ClassDesc of the type descriptor of the given KType.
 */
fun KType.descriptor(): ClassDesc {
    val klass = this.classifier as KClass<*>
    return klass.descriptor()
}

/**
 * Returns the KClass of the type corresponding to the
 * generic argument of the List or to the classifier itself, otherwise.
 */
fun KType.toKClass() =
    if (classifier == List::class) {
        arguments[0]
            .type
            ?.classifier as KClass<*>
    } else {
        classifier as KClass<*>
    }

fun KClass<*>.isPrimitiveOrString(): Boolean = this.java.isPrimitive || this == String::class