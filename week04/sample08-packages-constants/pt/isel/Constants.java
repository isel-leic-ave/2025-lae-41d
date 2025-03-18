package pt.isel;

class Constants {
    final int BITS_OF_10KB = 8 * 10 * 1024;
    final long created1 = System.currentTimeMillis(); // Option 1
    final long created2;
    final long created3;
    static final long createdStatic1 = System.currentTimeMillis();
    static final long createdStatic2;

    // Constructor
    Constants(){
        created2 = System.currentTimeMillis(); // Option 2
    }

    // Static initializer block
    static {
        createdStatic2 = System.currentTimeMillis();
    }

    // Instance initializer block
    {
        created3 = System.currentTimeMillis(); // Option 3
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(3000);
        Constants c = new Constants();
        System.out.println(c.created1);
        Thread.sleep(3000);
        System.out.println(c.created1);

        System.out.println(Constants.createdStatic1);
        Thread.sleep(3000);
        System.out.println(Constants.createdStatic1);
    }
}