package functions;

import functions.CompositeFunction;
import functions.IdentityFunction;
import functions.MathFunction;
import functions.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeFunctionTest {

    @Test
    void testSimpleComposition() {
        MathFunction sqr = new SqrFunction();
        MathFunction increment = x -> x + 1;
        CompositeFunction composite = new CompositeFunction(sqr, increment);

        assertEquals(1.0, composite.apply(0.0), 1e-9);
        assertEquals(2.0, composite.apply(1.0), 1e-9);
        assertEquals(5.0, composite.apply(2.0), 1e-9);
        assertEquals(26.0, composite.apply(5.0), 1e-9);
    }

    @Test
    void testNestedComposition() {
        MathFunction id = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        MathFunction increment = x -> x + 1;

        CompositeFunction comp1 = new CompositeFunction(id, sqr);
        CompositeFunction comp2 = new CompositeFunction(comp1, increment);

        assertEquals(1.0, comp2.apply(0.0), 1e-9);
        assertEquals(2.0, comp2.apply(1.0), 1e-9);
        assertEquals(5.0, comp2.apply(2.0), 1e-9);
    }
}