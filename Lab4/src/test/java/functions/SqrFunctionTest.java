package functions;

import functions.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SqrFunctionTest {

    @Test
    public void testApplyWithZero() {
        SqrFunction function = new SqrFunction();
        assertEquals(0.0, function.apply(0.0), 1e-9);
    }

    @Test
    public void testApplyWithPositiveNumbers() {
        SqrFunction function = new SqrFunction();
        assertEquals(1.0, function.apply(1.0), 1e-9);
        assertEquals(4.0, function.apply(2.0), 1e-9);
        assertEquals(9.0, function.apply(3.0), 1e-9);
    }

    @Test
    public void testApplyWithNegativeNumbers() {
        SqrFunction function = new SqrFunction();
        assertEquals(4.0, function.apply(-2.0), 1e-9);
        assertEquals(9.0, function.apply(-3.0), 1e-9);
    }

    @Test
    public void testApplyWithFractionalNumbers() {
        SqrFunction function = new SqrFunction();
        assertEquals(0.25, function.apply(0.5), 1e-9);
        assertEquals(2.25, function.apply(1.5), 1e-9);
    }
}