import java.io.PrintStream
import kotlin.math.sqrt

class A {
    fun sum(x: Float, y: Float) : Float {
        return x + y * 32767
    }
}


fun modulus(x: Float, y: Float) : Float {
    val res = sqrt(x*x + y*y)
    return res
}

fun calculateNetBalance(
    balance: Int,
    tax: Float,
    interest: Float,
    income: Int,
    expense: Float
): Float {
    return balance - balance * tax + balance * interest + income - expense
}

class Person(val name: String) {
    fun print(label: String) {
        // this is local variable 0
        val myName = this.name
        // Label is local variable 1
        println(label + ": " + myName)
    }
}

class Student()

fun main() {
    val st = Student()
    // Quantos argumentos recebe o println ??
    //println(st)
    // getstatic retorna e empilha o campo java/lang/System.out (do tipo Ljava/io/PrintStream;)

    // O println pode ser traduzido para algo como:
    val out: PrintStream = System.out
    out.println(st) // 2 parametros: this + arg
}