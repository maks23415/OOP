package operations;

import concurrent.SynchronizedTabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TabulatedDifferentialOperatorTest {

    @Test
    void testDeriveWithLinearFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 3.0, 5.0, 7.0, 9.0};

        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        TabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        for (int i = 0; i < derivative.getCount() - 1; i++) {
            assertEquals(2.0, derivative.getY(i), 1e-10, "производная линейной функции должна быть постоянной");
        }
    }

    @Test
    void testDeriveWithQuadraticFunction() {

        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());

        assertEquals(1.0, derivative.getY(0), 1e-5);
        assertEquals(3.0, derivative.getY(1), 1e-5);
        assertEquals(5.0, derivative.getY(2), 1e-5);
        assertEquals(7.0, derivative.getY(3), 1e-5);
        assertEquals(7.0, derivative.getY(4), 1e-5);

    }

    @Test
    void testDeriveWithLinkedListFactory() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        TabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);
        assertTrue(derivative instanceof LinkedListTabulatedFunction, "должна создавать функцию того же типа, что и фабрика");

        assertEquals(3, derivative.getCount());
        assertEquals(3.0, derivative.getY(0), 1e-10);
        assertEquals(5.0, derivative.getY(1), 1e-10);
        assertEquals(5.0, derivative.getY(2), 1e-10);

    }

    @Test
    void testDeriveWithArrayFactory() {
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};

        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        TabulatedFunction function = factory.create(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);
        assertTrue(derivative instanceof ArrayTabulatedFunction, "должна создавать функцию того же типа, что и фабрика");

        assertEquals(3, derivative.getCount());
        assertEquals(0.5, derivative.getY(0), 1e-10);
        assertEquals(1.5, derivative.getY(1), 1e-10);
        assertEquals(1.5, derivative.getY(2), 1e-10);

    }

    @Test
    void testConstructorWithFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        assertEquals(factory, operator.getFactory());
    }

    @Test
    void testDefaultConstructor() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        assertTrue(operator.getFactory() instanceof ArrayTabulatedFunctionFactory, "конструктор по умолчанию должен использовать ArrayTabulatedFunctionFactory");
    }

    @Test
    void testSetFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        operator.setFactory(newFactory);

        assertEquals(newFactory, operator.getFactory());
    }

    @Test
    void testDerivePreservesXValues() {
        double[] xValues = {0.1, 0.5, 1.2, 2.0, 3.5};
        double[] yValues = {0.01, 0.25, 1.44, 4.0, 12.25};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);
        assertEquals(xValues.length, derivative.getCount());
        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], derivative.getX(i), 1e-10, "X-координаты должны сохраняться при дифференцировании");
        }

    }

    @Test
    void testDeriveWithTwoPoints() {

        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(2, derivative.getCount());
        assertEquals(3.0, derivative.getY(0), 1e-10);
        assertEquals(3.0, derivative.getY(1), 1e-10);
    }

    @Test
    void testDeriveWithConstantFunction() {

        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {5.0, 5.0, 5.0, 5.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(4, derivative.getCount());
        for (int i = 0; i < derivative.getCount() - 1; i++) {
            assertEquals(0.0, derivative.getY(i), 1e-10, "Производная константы должна быть нулевой");
        }
    }

    @Test
    void testDeriveSynchronouslyWithRegularFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertNotNull(derivative);
        assertEquals(5, derivative.getCount());

        assertEquals(1.0, derivative.getY(0), 1e-5);
        assertEquals(3.0, derivative.getY(1), 1e-5);
        assertEquals(5.0, derivative.getY(2), 1e-5);
        assertEquals(7.0, derivative.getY(3), 1e-5);
        assertEquals(7.0, derivative.getY(4), 1e-5);
    }

    @Test
    void testDeriveSynchronouslyWithAlreadySynchronizedFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction baseFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);

        TabulatedFunction derivative = operator.deriveSynchronously(syncFunction);

        assertNotNull(derivative);
        assertEquals(4, derivative.getCount());
    }

    @Test
    void testDeriveSynchronouslySameAsDerive() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 8.0, 27.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivativeSync = operator.deriveSynchronously(function);
        TabulatedFunction derivativeRegular = operator.derive(function);

        assertEquals(derivativeRegular.getCount(), derivativeSync.getCount());

        for (int i = 0; i < derivativeRegular.getCount(); i++) {
            assertEquals(derivativeRegular.getX(i), derivativeSync.getX(i), 1e-9);
            assertEquals(derivativeRegular.getY(i), derivativeSync.getY(i), 1e-9);
        }
    }

    @Test
    void testDeriveSynchronouslyWithLinkedList() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);
        TabulatedFunction function = factory.create(xValues, yValues);

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertNotNull(derivative);
        assertEquals(4, derivative.getCount());

        assertEquals(3.0, derivative.getY(0), 1e-5);
        assertEquals(5.0, derivative.getY(1), 1e-5);
        assertEquals(7.0, derivative.getY(2), 1e-5);
        assertEquals(7.0, derivative.getY(3), 1e-5);
    }

    @Test
    void testDeriveSynchronouslyWithConstantFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {5.0, 5.0, 5.0, 5.0};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertNotNull(derivative);
        assertEquals(4, derivative.getCount());

        for (int i = 0; i < derivative.getCount() - 1; i++) {
            assertEquals(0.0, derivative.getY(i), 1e-10);
        }
    }
    @Test
    void testDeriveSynchronouslyPreservesXValues() {
        double[] xValues = {0.1, 0.5, 1.2, 2.0, 3.5};
        double[] yValues = {0.01, 0.25, 1.44, 4.0, 12.25};

        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertEquals(xValues.length, derivative.getCount());
        for (int i = 0; i < xValues.length; i++) {
            assertEquals(xValues[i], derivative.getX(i), 1e-10);
        }
    }
}
