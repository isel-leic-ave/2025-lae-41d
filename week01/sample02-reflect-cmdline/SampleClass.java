import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.*;

public class SampleClass {
    public static void main(String[] args) throws Exception {
        Class<Lamp> lampClass = Lamp.class; // Get the Lamp class reference
        System.out.println("Class name: " + lampClass.getSimpleName()); // Access class name

        System.out.println("Methods:");
        Method[] methods = lampClass.getMethods();
        for (Method method : methods) {
            System.out.println("  " + method.toString());
        }

        System.out.println("Public Fields:");
        Field[] fields = lampClass.getFields();
        for (Field field : fields) {
            System.out.println("  " + field.getName());
        }

        // In Java, all declared members can be accessed through getDeclaredMethods and getDeclaredFields
        System.out.println("Declared Methods:");
        Method[] declaredMethods = lampClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println("  " + declaredMethod.getName());
        }

        System.out.println("Declared Fields:");
        Field[] declaredFields = lampClass.getDeclaredFields();
        for (Field field : declaredFields) {
            System.out.println("  " + field.getName());
        }

        System.out.println("Superclasses:");
        Class<?> superclass = lampClass.getSuperclass();
        //
        while (superclass != null) {
            System.out.println("  " + superclass.getSimpleName());
            superclass = superclass.getSuperclass();
        }

        Lamp lamp1 = new Lamp(); // Create an object Lamp
        Class<?> lamp1Class = lamp1.getClass();

        // Access all declared fields and their values
        System.out.println("lamp1 object declared fields:");
        for (Field field : lamp1Class.getDeclaredFields()) {
            System.out.println(" name:  " + field.getName());
            field.setAccessible(true);
            System.out.println(" value: " + field.get(lamp1));
        }

        System.out.println("lamp1 object declared methods:");
        for (Method method : lamp1Class.getDeclaredMethods()) {
            System.out.println(" name:  " + method);
        }

        // Get a declared method by name (using lamp1Class) and invoke the method for lamp1
        Method turnOnMethod = lamp1Class.getDeclaredMethod("turnOn");
        System.out.println("lamp1 - turnOn return: " + turnOnMethod.invoke(lamp1));

        // Get a declared method by name (using lampClass) and invoke the method for lamp1
        Method getStateMethod = lamp1Class.getDeclaredMethod("getState");
        System.out.println("lamp1 - getState return: " + getStateMethod.invoke(lamp1));

        // Get setState method that needs an argument with an Array of Class objects.
        Class[] cArg = new Class[1]; // Array of 1 element
        cArg[0] = boolean.class; // Classe object of a boolean (primitive)
        Method setStateMethod = lamp1Class.getDeclaredMethod("setState", cArg);
        // Invoke the method for lamp1 with the argument value equal to false
        System.out.println("lamp1 - setState return: " + setStateMethod.invoke(lamp1, false));

        // Create an object Lamp
        Lamp lamp2 = lampClass.getDeclaredConstructor().newInstance();
        // Access declared fields of lamp2
        System.out.println("lamp2 object member properties:");
        for (Field field : lamp2.getClass().getDeclaredFields()) {
            System.out.println(" name:  " + field.getName());
            field.setAccessible(true);
            System.out.println(" value: " + field.get(lamp2));
        }
    }
}