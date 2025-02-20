import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class SampleClass {
    public static void main(String[] args) throws Exception {
        Class<Lamp> lampClass = Lamp.class; // Get the Lamp class reference
        System.out.println("Class name: " + lampClass.getSimpleName()); // Access class name
        // System.out.println("Complete class name: " + lampClass.getName());

        System.out.println("Members:");
        Method[] methods = lampClass.getMethods();
        for (Method method : methods) {
            System.out.println("  " + method.getName());
        }

        System.out.println("Declared Members:");
        Method[] declaredMethods = lampClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            System.out.println("  " + method.getName());
        }

        System.out.println("Members Properties:");
        Field[] fields = lampClass.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("  " + field.getName());
        }

        System.out.println("Members Functions:");
        for (Method method : methods) {
            System.out.println("  " + method.getName());
        }

        System.out.println("Superclasses:");
        Class<?> superclass = lampClass.getSuperclass();
        while (superclass != null) {
            System.out.println("  " + superclass.getSimpleName());
            superclass = superclass.getSuperclass();
        }

        Lamp lamp1 = new Lamp(); // Create an object Lamp
        // Access properties and methods (class and superclass) dynamically
        System.out.println("lamp1 object member properties:");
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.println(" name:  " + field.getName());
            System.out.println(" value: " + field.get(lamp1));
        }
        System.out.println("lamp1 object member functions:");
        for (Method method : methods) {
            System.out.println(" name:  " + method.getName());
            System.out.println(" parameters (size: " + method.getParameterCount() + "):");
            for (Class<?> paramType : method.getParameterTypes()) {
                System.out.println("   " + paramType.getSimpleName());
            }
            if (method.getParameterCount() <= 1) // Not the best way to do that
                System.out.println(" value: " + method.invoke(lamp1));
            if (method.getParameterCount() == 2) // Not the best way to do that
                System.out.println(" value: " + method.invoke(lamp1, (Object) null));
        }

        Constructor<Lamp> constructor = lampClass.getConstructor(); // Create an object Lamp without defined constructor
        Lamp lamp2 = constructor.newInstance();
        // Access properties and methods (class and superclass) dynamically
        System.out.println("lamp2 object member properties:");
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.println(" name:  " + field.getName());
            System.out.println(" value: " + field.get(lamp2));
        }
    }
}

