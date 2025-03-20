public class App {
    public static void main(String[] args) {
        Outer outerObj = new Outer();
        Outer.Inner innerOjb = outerObj.new Inner();
        innerOjb.foo();

        Outer.StaticNested staticNestedObj = new Outer.StaticNested();
        staticNestedObj.foo();

        A a = new A(7);
        A.B ab = a.new B();
        ab.foo();

        new A(11).new B().foo();

        // With makeB method
        new A(7).makeB().foo();
        new A(11).makeB().foo();
    }
}

class Outer {
    class Inner {
        public void foo() { System.out.println("Inner foo");}
    }
    static class StaticNested {
        public void foo() { System.out.println("StaticNested foo");}
    }
}

/**
 * Members:
 * - FIELD nr
 * - constructor A(), i.e. <init>
 * - METHOD makeB()
 */
class A {
    private final int nr;

    public A(int nr) {
        this.nr = nr;
    }
    /**
     * MEMBERS:
     * - METHOD foo
     * - FIELD this$0
     * - constructor A$B(A), i.e. <init>(A):
     *   > initializes field this$0 with the value of the parameter.
     *
     */
    class B {
        public void foo() {
            // <=> out.println(A.this.nr);
            // A.this <=> FIELD this$0
            System.out.println(nr); // Inner class accessing field from outer class
        }
    }

    public B makeB() {
        // <=> new B(this)
        return new B();
    }
}