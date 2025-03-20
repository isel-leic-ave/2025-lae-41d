public class App {
    public static void main(String[] args) {
        Person p = new Person("Maria");
        Student s = new Student("João");
        printName(p);
        printName(s);
        s.printBothName();
    }
    public static void printName(Person obj) {
        /**
         * Always read the field name defined by
         * the base type, i.e. Person
         */
        System.out.println(obj.name);
        /**
         * Calls the implementation of the virtual method print
         * depending on the OBJECT REFERRED by obj.
         */
        obj.print();
    }

}

class Person {
    public final String name;

    public Person(String name) {
        this.name = name;
    }

    public void print() {System.out.println("I am a Person");}
}

/**
 * An instance of Student has two fields name.
 */
class Student extends Person {
    /**
     * Another field name different from the field name of the base class.
     */
    public final String name;

    public Student(String name) {
        super(null);
        this.name = name;
    }

    public void print() {System.out.println("I am a Student");}

    public void printBothName(){
        System.out.println("Base name: " + super.name);
        System.out.println("My name  : " + name); // <=> this.name
    }
}