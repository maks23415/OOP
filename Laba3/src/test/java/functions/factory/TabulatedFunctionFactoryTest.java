package functions.factory;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TabulatedFunctionFactoryTest {
    private final double[] xValues = {1.0, 2.0, 3.0, 4.0};
    private final double[] yValues = {1.0, 4.0, 9.0, 16.0};

    @Test
    void testArrayTabulatedFunctionFactoryCreatesCorrectType() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        TabulatedFunction function = factory.create(xValues, yValues);
        assertTrue(function instanceof ArrayTabulatedFunction, "должна создать экземп. ArrayTabulatedFunction ");

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(4.0, function.getX(3));

        assertEquals(1.0, function.getY(0));
        assertEquals(4.0, function.getY(1));
        assertEquals(9.0, function.getY(2));
        assertEquals(16.0, function.getY(3));
    }

    @Test
    void testLinkedListTabulatedFunctionFactoryCreatesCorrectType() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        TabulatedFunction function = factory.create(xValues, yValues);
        assertTrue(function instanceof LinkedListTabulatedFunction, "должна создать экземп. LinkedListTabulatedFunctionFactory ");

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(9.0, function.getY(2));
        assertEquals(16.0, function.getY(3));

        assertEquals(1.0, function.getY(0));
        assertEquals(4.0, function.getY(1));
        assertEquals(9.0, function.getY(2));
        assertEquals(16.0, function.getY(3));

    }

    @Test
    void testArrayFactoryWithDifferentData() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] testX = {0.0, 0.5, 1.0};
        double[] testY = {0.0, 0.25, 1.0};

        TabulatedFunction function = factory.create(testX, testY);

        assertTrue(function instanceof ArrayTabulatedFunction);
        assertEquals(3, function.getCount());
        assertEquals(0.0, function.getX(0));
        assertEquals(0.5, function.getX(1));
        assertEquals(1.0, function.getX(2));
    }

    @Test
    void testLinkedListFactoryWithDifferentData() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] testX = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] testY = {4.0, 1.0, 0.0, 1.0, 4.0};

        TabulatedFunction function = factory.create(testX, testY);

        assertTrue(function instanceof LinkedListTabulatedFunction);
        assertEquals(5, function.getCount());
        assertEquals(-2.0, function.getX(0));
        assertEquals(0.0, function.getX(2));
        assertEquals(2.0, function.getX(4));
    }

    @Test
    void testFactoriesCreateFunctionalObjects() {
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();

        double[] x = {1.0, 2.0, 3.0};
        double[] y = {2.0, 4.0, 6.0};

        TabulatedFunction arrayFunction = arrayFactory.create(x, y);
        TabulatedFunction linkedListFunction = linkedListFactory.create(x, y);

        assertEquals(2.0, arrayFunction.apply(1.0));
        assertEquals(5.0, arrayFunction.apply(2.5));
        assertEquals(6.0, arrayFunction.apply(3.0));

        assertEquals(2.0, linkedListFunction.apply(1.0));
        assertEquals(5.0, linkedListFunction.apply(2.5));
        assertEquals(6.0, linkedListFunction.apply(3.0));

        assertEquals(1.0, arrayFunction.leftBound());
        assertEquals(3.0, arrayFunction.rightBound());
        assertEquals(1.0, linkedListFunction.leftBound());
        assertEquals(3.0, linkedListFunction.rightBound());
    }

    @Test
    void testFactoryMethodsReturnNewInstances() {
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();

        double[] x1 = {1.0, 2.0};
        double[] y1 = {1.0, 2.0};
        double[] x2 = {3.0, 4.0};
        double[] y2 = {3.0, 4.0};

        TabulatedFunction func1 = arrayFactory.create(x1, y1);
        TabulatedFunction func2 = arrayFactory.create(x2, y2);

        assertNotSame(func1, func2, "Factory should create new instances each time");

        assertEquals(1.0, func1.getX(0));
        assertEquals(3.0, func2.getX(0));
    }
}

