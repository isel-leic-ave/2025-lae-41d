package pt.isel;

class Person {
    protected String name;
    int age;
    private float height;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setHeight(float height) {
        this.height = height;
    }
    public float getHeight() {
        return height;
    }
}