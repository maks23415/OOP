package operations;

import exceptions.InconsistentFunctionsException;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabulatedFunctionOperationService {

    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionOperationService.class);

    private TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        logger.debug("Creating TabulatedFunctionOperationService with factory: {}",
                factory.getClass().getSimpleName());
        this.factory = factory;
    }

    public TabulatedFunctionOperationService() {
        logger.debug("Creating TabulatedFunctionOperationService with default ArrayTabulatedFunctionFactory");
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        logger.trace("Getting factory: {}", factory.getClass().getSimpleName());
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        logger.debug("Setting new factory: {}", factory.getClass().getSimpleName());
        this.factory = factory;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        logger.debug("Performing binary operation on functions. Function A: {} points, Function B: {} points",
                a.getCount(), b.getCount());

        if (a.getCount() != b.getCount()) {
            logger.error("Function count mismatch: A has {} points, B has {} points",
                    a.getCount(), b.getCount());
            throw new InconsistentFunctionsException("Функции имеют разное количество точек");
        }

        logger.trace("Converting functions to points arrays");
        Point[] pointsA = asPoints(a);
        Point[] pointsB = asPoints(b);

        double[] xValues = new double[a.getCount()];
        double[] yValues = new double[a.getCount()];

        logger.debug("Processing {} points for operation", a.getCount());
        for (int i = 0; i < a.getCount(); i++) {
            logger.trace("Processing point {}: A(x={}, y={}), B(x={}, y={})",
                    i, pointsA[i].x, pointsA[i].y, pointsB[i].x, pointsB[i].y);

            if (Math.abs(pointsA[i].x - pointsB[i].x) > 1e-10) {
                logger.error("X value mismatch at index {}: A.x={}, B.x={}",
                        i, pointsA[i].x, pointsB[i].x);
                throw new InconsistentFunctionsException("Значения X не совпадают по индексу " + i);
            }

            xValues[i] = pointsA[i].x;
            double yA = pointsA[i].y;
            double yB = pointsB[i].y;
            double resultY = operation.apply(yA, yB);
            yValues[i] = resultY;

            logger.trace("Operation result at index {}: {}({}, {}) = {}",
                    i, operation.getClass().getSimpleName(), yA, yB, resultY);
        }

        logger.debug("Creating new function with factory: {}", factory.getClass().getSimpleName());
        TabulatedFunction result = factory.create(xValues, yValues);
        logger.info("Binary operation completed successfully. Result function: {} points", result.getCount());
        return result;
    }

    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Performing addition operation");
        logger.debug("Function A type: {}, points: {}",
                a.getClass().getSimpleName(), a.getCount());
        logger.debug("Function B type: {}, points: {}",
                b.getClass().getSimpleName(), b.getCount());

        TabulatedFunction result = doOperation(a, b, (u, v) -> u + v);

        logger.debug("Addition operation completed. Result type: {}",
                result.getClass().getSimpleName());
        return result;
    }

    public TabulatedFunction subtract(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Performing subtraction operation");
        logger.debug("Function A type: {}, points: {}",
                a.getClass().getSimpleName(), a.getCount());
        logger.debug("Function B type: {}, points: {}",
                b.getClass().getSimpleName(), b.getCount());

        TabulatedFunction result = doOperation(a, b, (u, v) -> u - v);

        logger.debug("Subtraction operation completed. Result type: {}",
                result.getClass().getSimpleName());
        return result;
    }

    public TabulatedFunction multiply(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Performing multiplication operation");
        logger.debug("Function A type: {}, points: {}",
                a.getClass().getSimpleName(), a.getCount());
        logger.debug("Function B type: {}, points: {}",
                b.getClass().getSimpleName(), b.getCount());

        TabulatedFunction result = doOperation(a, b, (u, v) -> u * v);

        logger.debug("Multiplication operation completed. Result type: {}",
                result.getClass().getSimpleName());
        return result;
    }

    public TabulatedFunction divide(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Performing division operation");
        logger.debug("Function A type: {}, points: {}",
                a.getClass().getSimpleName(), a.getCount());
        logger.debug("Function B type: {}, points: {}",
                b.getClass().getSimpleName(), b.getCount());

        TabulatedFunction result = doOperation(a, b, (u, v) -> {
            if (Math.abs(v) < 1e-10) {
                logger.error("Division by zero detected: numerator={}, denominator={}", u, v);
                throw new ArithmeticException("Деление на ноль");
            }
            double divisionResult = u / v;
            logger.trace("Division: {} / {} = {}", u, v, divisionResult);
            return divisionResult;
        });

        logger.debug("Division operation completed. Result type: {}",
                result.getClass().getSimpleName());
        return result;
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        logger.debug("Converting TabulatedFunction to Points array. Function type: {}, points: {}",
                tabulatedFunction.getClass().getSimpleName(), tabulatedFunction.getCount());

        Point[] points = new Point[tabulatedFunction.getCount()];
        int i = 0;

        logger.trace("Iterating through function points");
        for (Point point : tabulatedFunction) {
            points[i] = point;
            logger.trace("Point {}: x={}, y={}", i, point.x, point.y);
            i++;
        }

        logger.debug("Successfully converted {} points to array", points.length);
        return points;
    }
}