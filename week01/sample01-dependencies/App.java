public class App {
    public static void main(String[] args) {
        System.out.println("Press ENTER to proceed.");
        System.console().readLine();
        if(args.length == 0)
            new X().print();
        else
            bar();
    }
    public static void bar() {
        Y someY = new Y();
        someY.print();
    }
}
