abstract class A {
    int a;
    abstract void foo();
    void bar() {
        System.out.println("bar");
    }
}

interface I {
    int CONST = 100; // <=> public static final int CONST
    void foo(); // <=> abstract void foo()
    default void bar(){
        System.out.println("bar");
    };
}

class CA extends A {
    public void foo() {}
}

class CI implements I {
    public void foo() {}
}