fun main() {
    A(7).makeB().foo();
}

class A(val nr: Int) {
    val dummy: B = object : B() {
        override fun foo() {
            println("I am a different B")
        }
    }
    fun makeB() : B {
        return object : B() {
            override fun foo() {
                println("Foo from B on Kotlin A class with nr $nr")
            }
        }
    }
}