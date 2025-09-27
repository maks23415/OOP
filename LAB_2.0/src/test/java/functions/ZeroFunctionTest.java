package java.functions;

import functions.ZeroFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ZeroFunctionTest {

    private ZeroFunction zeroFunction;

    @BeforeEach
    public void setUp() {
        zeroFunction = new ZeroFunction();
    }

    @Test
    public void testZeroFunctionReturnsZero() {
        double expected = 0;
        double actual = zeroFunction.apply(5);
        Assertions.assertEquals(expected, actual, 0.0001);

        actual = zeroFunction.apply(0);
        Assertions.assertEquals(expected, actual, 0.0001);

        actual = zeroFunction.apply(-5);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testGetArg() {
        double expected = 0;
        double actual = zeroFunction.getArg();
        Assertions.assertEquals(expected, actual, 0.0001);
    }
}