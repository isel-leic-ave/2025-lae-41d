class Test{
    public static void main(String[] args) {
        pt.isel.Employee employee1 = new pt.isel.Employee("Charles", 30);
        employee1.testModifiers();
        apps.Manager manager1 = new apps.Manager("Bob", 40);
        manager1.testModifiers();
        //System.out.println(employee1.salary); // Compile Time Error: cannot be accessed from outside package
    }
}