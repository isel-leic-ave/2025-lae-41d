package pt.isel;

public class CounterBaseline implements Sum {
    private final int nr;

    public CounterBaseline(int nr) {
        this.nr = nr;
    }
    public int add(int other) {
        return this.nr + other;
    }
}