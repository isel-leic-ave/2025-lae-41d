import java.lang.reflect.*;

public class DumpMethods {
   public static void main(String args[]) {
      try {
         Class c = Class.forName(args[0]);
         dump(c);
      }
      catch (Throwable e) {
         System.err.println(e);
      }
   }
   public static void dump(Class c) {
      for (Method m: c.declaredFunctions())
         System.out.println(m.toString());
   }
}
