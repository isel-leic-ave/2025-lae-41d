package pt.isel;

public class App {
    String name;
    static int x = 123;

    // Main method
    public static void main(String[] args) {
        perTypeMethod(); // <=> App.perTypeMethod();
        App.x = 234;

        new App("app1").perInstanceMethod();

        new App("app2").otherInstanceMethod();
    }

    // Constructor
    App(String name) {this.name = name; }

    public void perInstanceMethod() {
        System.out.println("I am a per-instance method of " + name);
    }

    public static void perTypeMethod() {
        System.out.println("I am a per-type method of class " + App.class.getSimpleName());
    }

    public void otherInstanceMethod() {
        perInstanceMethod(); // <=> this.perInstanceMethod()
    }
}