package pt.isel;

public class Employee extends Person {
    float salary;

    public Employee(String name, int age){
        super(name, age);
    }

    public void testModifiers(){
        System.out.println("Name in Employee: " + super.name); // name is protected: it is accessible to the subclass
        System.out.println("Age in Employee: " + super.age); // age is default: Person and Employee are in the same package
        //System.out.println("height in Employee: " + super.height); // Compile time error: height is default, needs same package
    }
}