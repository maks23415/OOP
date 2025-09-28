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
    @Test
    void testThreeFunctionChain()
    {
        MathFunction f1 = new SqrFunction();
        MathFunction f2 = new IdentityFunction();
        MathFunction f3 = new ConstantFunction(5);

        MathFunction chain = f1.andThen(f2).andThen(f3);

        assertEquals(5.0, chain.apply(2), 1e-9);
        assertEquals(5.0, chain.apply(10), 1e-9);
        assertEquals(5.0, chain.apply(-3), 1e-9);
    }

    @Test
    void testExtrapolationWithComposite() {
        // Табличная функция: y = x²
        double[] xValues = {1, 2, 3};
        double[] yValues = {1, 4, 9};
        TabulatedFunction tabulatedFunc = new ArrayTabulatedFunction(xValues, yValues);
        MathFunction increment = x -> x + 1;
        MathFunction composite = tabulatedFunc.andThen(increment);
        assertEquals(0.5, composite.apply(0.5), 1e-9);

        assertEquals(7.5, composite.apply(2.5), 1e-9);
    }

    @Test
    void testLinkedListWithInterpolation() {
        double[] xValues = {0, 2, 4};
        double[] yValues = {0, 4, 8};
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);
        MathFunction increment = x -> x + 1;

        MathFunction composite = listFunc.andThen(increment);

        assertEquals(1.0, composite.apply(0), 1e-9);   // 2*0 + 1 = 1
        assertEquals(3.0, composite.apply(1), 1e-9);   // 2*1 + 1 = 3
        assertEquals(5.0, composite.apply(2), 1e-9);   // 2*2 + 1 = 5
        assertEquals(9.0, composite.apply(4), 1e-9);   // 2*4 + 1 = 9
    }

    @Test
    void testComplexChainWithDifferentTypes() {

        double[] xValues = {0, 1, 2};
        double[] yValues = {1, 2, 3};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

        MathFunction sqr = new SqrFunction();
        MathFunction constant = new ConstantFunction(10); // 10

        MathFunction complexChain = arrayFunc.andThen(sqr).andThen(constant);

        assertEquals(10.0, complexChain.apply(0), 1e-9);
        assertEquals(10.0, complexChain.apply(1), 1e-9);
        assertEquals(10.0, complexChain.apply(2), 1e-9);
    }

}