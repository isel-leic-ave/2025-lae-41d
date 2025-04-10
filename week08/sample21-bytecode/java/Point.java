package pt.isel;

public class Point {
    private double x;
    private double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    double getDistance(Point p) {
        return (Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)));
    }
}