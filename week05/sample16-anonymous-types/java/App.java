public class App {
    public static void main(String[] args) {

        new A(9).makeB().foo();
     }
}

class A {
    final int nr;
    A (int nr) { this.nr = nr; }

    final B dummy = new B() {
        public void foo() {
            System.out.println("I am a different B");
        }

    };

    // Create an object B
    public B makeB() {
        return new B() {
            public void foo() {
                System.out.println("Foo from B on Java A class with nr: " + nr);
            }
        };
    }
}