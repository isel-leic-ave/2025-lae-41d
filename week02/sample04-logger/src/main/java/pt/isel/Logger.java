package pt.isel;

import java.lang.reflect.Method;
import static java.lang.System.lineSeparator;

public class Logger {
    public static void log(Appendable out, Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        out.append("Object of Type " + clazz.getSimpleName() + lineSeparator());
        for (Method mth : clazz.getDeclaredMethods()) {
            if (isGetter(mth)) {
                String propName = mth.getName().replaceFirst("^get", "");
                Object propValue = mth.invoke(obj);
                out.append("  - " + propName + ": " + propValue + lineSeparator());
            }
        }
    }

    static boolean isGetter(Method method){
        return (method.getName().startsWith("get") // starts with 'get'
                && method.getParameterCount() == 0 // There is no parameters
                && ! method.getReturnType().equals(void.class) // Returned type is not void
        );
    }
}