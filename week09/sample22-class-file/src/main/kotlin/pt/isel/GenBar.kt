package pt.isel

import java.io.File
import java.io.FileOutputStream
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassBuilder
import java.lang.classfile.MethodBuilder
import java.lang.classfile.CodeBuilder
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ClassDesc
import java.lang.constant.MethodTypeDesc
import java.lang.constant.ConstantDescs.INIT_NAME
import java.lang.constant.ConstantDescs.MTD_void
import java.lang.constant.ConstantDescs.CD_Object
import java.lang.constant.ConstantDescs.CD_int

private val resourcePath =
    Unit::class.java
        .getResource("/")
        ?.toURI()
        ?.path

fun main(){
    // println(resourcePath)
    val bytes =
        ClassFile.of().build(ClassDesc.of("pt.isel.Bar")) { clb: ClassBuilder -> // ClassBuilder
            clb
                .withFlags(ACC_PUBLIC) // Class flags
                .withMethod(INIT_NAME, MTD_void, ACC_PUBLIC) { mb: MethodBuilder -> // Constructor method
                    mb.withCode { cob: CodeBuilder ->
                        cob
                            .aload(0)
                            .invokespecial(CD_Object, INIT_NAME, MTD_void)
                            .return_()
                    }
                }.withMethod("foo", MethodTypeDesc.of(CD_int), ACC_PUBLIC) { mb: MethodBuilder ->
                    mb.withCode { cob: CodeBuilder ->
                        cob
                            .ldc(clb.constantPool().intEntry(67895))
                            .ireturn()
                    }
                }
        }
    FileOutputStream(File(resourcePath, "pt/isel/Bar.class")).write(bytes)
}