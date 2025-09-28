package functions;

import functions.CompositeFunction;
import functions.IdentityFunction;
import functions.MathFunction;
import functions.SqrFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeFunctionTest {

    @Test
    void testSimpleComposition()
    {
        MathFunction sqr = new SqrFunction();
        MathFunction increment = x -> x + 1;
        CompositeFunction composite = new CompositeFunction(sqr, increment);

        assertEquals(1.0, composite.apply(0.0), 1e-9);
        assertEquals(2.0, composite.apply(1.0), 1e-9);
        assertEquals(5.0, composite.apply(2.0), 1e-9);
        assertEquals(26.0, composite.apply(5.0), 1e-9);
    }

    @Test
    void testNestedComposition()
    {
        MathFunction id = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        MathFunction increment = x -> x + 1;

        CompositeFunction comp1 = new CompositeFunction(id, sqr);
        CompositeFunction comp2 = new CompositeFunction(comp1, increment);

        assertEquals(1.0, comp2.apply(0.0), 1e-9);
        assertEquals(2.0, comp2.apply(1.0), 1e-9);
        assertEquals(5.0, comp2.apply(2.0), 1e-9);
    }

    @Test
    void testArrayTabulatedWithLinearFunction()
    {
        double[] xValues = {1, 2, 3};
        double[] yValues = {2, 4, 6};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        MathFunction sqrFunc = new SqrFunction();

        MathFunction composite = arrayFunc.andThen(sqrFunc);

        assertEquals(4.0, composite.apply(1), 1e-9);
        assertEquals(16.0, composite.apply(2), 1e-9);
        assertEquals(36.0, composite.apply(3), 1e-9);
    }

    @Test
    void testLinkedListTabulatedWithSqr()
    {
        double[] xValues = {0, 1, 2};
        double[] yValues = {5, 6, 7};
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);
        MathFunction sqr = new SqrFunction();

        MathFunction composite = listFunc.andThen(sqr);

        assertEquals(25.0, composite.apply(0), 1e-9);
        assertEquals(36.0, composite.apply(1), 1e-9);
        assertEquals(49.0, composite.apply(2), 1e-9);
    }

    @Test
    void testArrayAndLinkedListCombination()
    {
        double[] xValues1 = {0, 1, 2};
        double[] yValues1 = {0, 2, 4};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0, 1, 2};
        double[] yValues2 = {1, 2, 3};
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues2, yValues2);

        MathFunction composite = arrayFunc.andThen(listFunc);

        assertEquals(1.0, composite.apply(0), 1e-9);
        assertEquals(3.0, composite.apply(1), 1e-9);
        assertEquals(5.0, composite.apply(2), 1e-9);
    }
}