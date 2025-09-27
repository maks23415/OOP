package java.functions;

import functions.UnitFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class UnitFunctionTest {

    private UnitFunction unitFunction;

    @BeforeEach
    public void setUp() {
        unitFunction = new UnitFunction();
    }

    @Test
    public void testUnitFunctionReturnsOne() {
        double expected = 1;
        double actual = unitFunction.apply(5);
        Assertions.assertEquals(expected, actual, 0.0001);

        actual = unitFunction.apply(0);
        Assertions.assertEquals(expected, actual, 0.0001);

        actual = unitFunction.apply(-5);
        Assertions.assertEquals(expected, actual, 0.0001);
    }

    @Test
    public void testGetArg() {
        double expected = 1;
        double actual = unitFunction.getArg();
        Assertions.assertEquals(expected, actual, 0.0001);
    }
}