package pt.isel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggerJavaTest {

    @Test
    void checkLogRectangle() throws Exception {
        String expected = """
           Object of Type Rectangle
             - Width: 4
             - Height: 5
             - Area: 20
           """.indent(0).trim();
        Rectangle r = new Rectangle(4, 5);
        StringBuilder actual = new StringBuilder();
        pt.isel.Logger logger = new pt.isel.Logger();
        logger.log(actual, r);
        //println(actual)
        assertEquals(expected, actual.toString().indent(0).trim());
    }
    @Test
    void checkLogRectJava() throws Exception {
        String expected = """
           Object of Type RectJava
             - Width: 4
             - Height: 5
             - Area: 20
           """.indent(0).trim();
        RectJava r = new RectJava(4, 5);
        StringBuilder actual = new StringBuilder();
        pt.isel.Logger logger = new pt.isel.Logger();
        logger.log(actual, r);
        //println(actual)
        assertEquals(expected, actual.toString().indent(0).trim());
    }
}