package pt.isel

import java.io.File
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_FINAL
import java.lang.classfile.ClassFile.ACC_PRIVATE
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.Interfaces
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs.CD_Object
import java.lang.constant.ConstantDescs.CD_int
import java.lang.constant.ConstantDescs.CD_void
import java.lang.constant.ConstantDescs.INIT_NAME
import java.lang.constant.ConstantDescs.MTD_void
import java.lang.constant.MethodTypeDesc

private val resourcePath =
    Unit::class.java
        .getResource("/")
        ?.toURI()
        ?.path

private const val packageName = "pt.isel"
private val packageFolder = packageName.replace(".", "/")

/**
 * Dynamically generates a class similar to:
 * <pre>
 * package pt.isel;
 *
 * class Counter implements Sum {
 *     private final int nr;
 *
 *     public Counter(int nr) {
 *         this.nr = nr;
 *     }
 *
 *     public int add(int other) {
 *         return this.nr + other;
 *     }
 * }
 * </pre>
 */
fun main() {
    val counterClassDesc = ClassDesc.of("$packageName.Counter")
    val sumClassDesc = ClassDesc.of("pt.isel.Sum")
    val bytes =
        ClassFile.of().build(counterClassDesc) { clb: ClassBuilder ->
            clb
                .withFlags(ACC_FINAL or ACC_PUBLIC)
                .withInterfaces(Interfaces.ofSymbols(sumClassDesc).interfaces())
                .withField("nr", CD_int, ACC_PRIVATE)
                .withMethod(INIT_NAME, MethodTypeDesc.of(CD_void, CD_int), ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .aload(0)
                            .iload(1)
                            .putfield(counterClassDesc, "nr", CD_int)
                            .return_()
                    }
                }.withMethod("add", MethodTypeDesc.of(CD_int, CD_int), ACC_PUBLIC) { mb ->
                    mb.withCode { cob ->
                        cob
                            .aload(0)
                            .getfield(counterClassDesc, "nr", CD_int)
                            .iload(1)
                            .iadd()
                            .ireturn()
                    }
                }
        }
    File(resourcePath, "$packageFolder/Counter.class").also { it.parentFile.mkdirs() }.writeBytes(bytes)
}