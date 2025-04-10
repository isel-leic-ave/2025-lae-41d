package pt.isel

class Point(val x: Double, val y: Double) {
    fun getDistance(p: Point): Double {
        return (Math.sqrt(Math.pow(p.x - x, 2.0) + Math.pow(p.y - y, 2.0)))
    }
}