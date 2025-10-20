package functions;

import functions.ConstantFunction;
import functions.DerivativeFunction;
import functions.MathFunction;
import functions.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DerivativeFunctionTest {
    @Test
    void testDerivativeOfSqrFunction()
    {
        MathFunction sqr = new SqrFunction();
        MathFunction derivative = new DerivativeFunction(sqr);
        assertEquals(0.0, derivative.apply(0.0), 1e-5);
        assertEquals(2.0, derivative.apply(1.0), 1e-5);
        assertEquals(4.0, derivative.apply(2.0), 1e-5);
        assertEquals(-6.0, derivative.apply(-3.0), 1e-5);
    }

    @Test
    void testDerivativeOfConstantFunction() {
        // f(x) = 5, f'(x) = 0
        MathFunction constant = new ConstantFunction(5.0);
        MathFunction derivative = new DerivativeFunction(constant);

        assertEquals(0.0, derivative.apply(0.0), 1e-9);
        assertEquals(0.0, derivative.apply(10.0), 1e-9);
        assertEquals(0.0, derivative.apply(-5.0), 1e-9);
    }

}