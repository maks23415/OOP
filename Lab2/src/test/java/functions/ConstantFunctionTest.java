package java.functions;

import functions.ConstantFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ConstantFunctionTest {

    private ConstantFunction constantFunction;

    @BeforeEach
    public void setUp() {
        constantFunction = new ConstantFunction(10);
    }

    @Test
    public void testConstantFunctionForInt() {
        double expected = 10;
        double actual = constantFunction.apply(5);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testConstantFunctionForZero() {
        double expected = 10;
        double actual = constantFunction.apply(0);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testConstantFunctionForNegative() {
        double expected = 10;
        double actual = constantFunction.apply(-5);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testGetArg() {
        double expected = 10;
        double actual = constantFunction.getArg();
        Assertions.assertEquals(expected, actual, 0.0001);
    }
}