import functions.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathFunctionTest {
    @Test
    public void testAndThenWithSqrAndConstant() {
        MathFunction sqr = new SqrFunction();
        MathFunction constant = new ConstantFunction(5);

        MathFunction composite = sqr.andThen(constant);
        assertEquals(5.0, composite.apply(2.0), 1e-9);
        assertEquals(5.0, composite.apply(10.0), 1e-9);
    }

    @Test
    public void testAndThenChain() {
        MathFunction sqr = new SqrFunction();
        MathFunction constant = new ConstantFunction(3);
        MathFunction zero = new ZeroFunction();

        double result = sqr.andThen(constant).andThen(zero).apply(4.0);
        assertEquals(0.0, result, 1e-9);
    }

    @Test
    public void testAndThenWithBSpline() {
        MathFunction constant = new ConstantFunction(1.5);
        MathFunction bSpline = new CubicBSplineFunction();

        MathFunction composite = constant.andThen(bSpline);
        assertEquals(bSpline.apply(1.5), composite.apply(100.0), 1e-9);
    }

    @Test
    public void testAndThenOrder() {
        MathFunction f = new SqrFunction();
        MathFunction g = new ConstantFunction(10);

        // Используйте MathFunction вместо CompositeFunction
        MathFunction composite1 = f.andThen(g);
        assertEquals(10.0, composite1.apply(5.0), 1e-9);
    }
}