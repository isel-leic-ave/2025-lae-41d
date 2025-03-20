fun main() {
    val ca: A = object : A() {
        override fun foo() { }
    }
    val ci: I = object : I {
        override fun foo() { }
    }
}