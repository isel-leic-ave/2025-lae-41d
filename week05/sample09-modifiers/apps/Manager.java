package apps;

public class Manager extends pt.isel.Employee {
    public Manager(String name, int age) {
        super(name, age);
    }
    public void testModifiers(){
        System.out.println("Name in Manager: " + super.name); // name is protected: it is accessible to the subclass
        // System.out.println("Age in Manager: " + super.age); // Compile time error: age is default, needs same package
        // System.out.println("Salary in Manager: " + super.salary); // Compile time error: salary is default, needs same package
    }

}