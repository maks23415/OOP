package operations;

import concurrent.SynchronizedTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {

    private static final Logger logger = LoggerFactory.getLogger(TabulatedDifferentialOperator.class);

    private TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator() {
        logger.debug("Creating TabulatedDifferentialOperator with default ArrayTabulatedFunctionFactory");
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        logger.debug("Creating TabulatedDifferentialOperator with factory: {}",
                factory.getClass().getSimpleName());
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        logger.trace("Getting factory: {}", factory.getClass().getSimpleName());
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        logger.debug("Setting new factory: {}", factory.getClass().getSimpleName());
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        logger.info("Computing derivative of TabulatedFunction. Type: {}, Points: {}",
                function.getClass().getSimpleName(), function.getCount());

        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int count = points.length;
        logger.debug("Converted function to {} points array", count);

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        // Copy x values
        for (int i = 0; i < count; i++) {
            xValues[i] = points[i].x;
        }
        logger.trace("X values copied successfully");

        // Compute derivatives using finite differences
        logger.debug("Computing derivatives using finite differences");
        for (int i = 0; i < count - 1; i++) {
            double deltaX = points[i + 1].x - points[i].x;
            double deltaY = points[i + 1].y - points[i].y;

            if (Math.abs(deltaX) < 1e-10) {
                logger.warn("Very small or zero deltaX at index {}: {}", i, deltaX);
            }

            yValues[i] = deltaY / deltaX;

            logger.trace("Derivative at index {}: deltaX={}, deltaY={}, derivative={}",
                    i, deltaX, deltaY, yValues[i]);
        }

        // Set last derivative value equal to the previous one
        if (count > 1) {
            yValues[count - 1] = yValues[count - 2];
            logger.debug("Set last derivative value to previous value: {}", yValues[count - 1]);
        } else {
            logger.warn("Function has only one point, derivative array may be invalid");
        }

        TabulatedFunction derivative = factory.create(xValues, yValues);
        logger.info("Derivative computation completed. Result type: {}, Points: {}",
                derivative.getClass().getSimpleName(), derivative.getCount());

        // Log some sample derivative values for verification
        if (logger.isDebugEnabled() && count > 0) {
            logger.debug("Sample derivative values - First: {}, Last: {}",
                    yValues[0], yValues[count - 1]);
        }

        return derivative;
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        logger.info("Computing derivative synchronously for function. Type: {}, Points: {}",
                function.getClass().getSimpleName(), function.getCount());

        SynchronizedTabulatedFunction syncFunction;

        if (function instanceof SynchronizedTabulatedFunction) {
            syncFunction = (SynchronizedTabulatedFunction) function;
            logger.debug("Function is already SynchronizedTabulatedFunction, reusing instance");
        } else {
            logger.debug("Wrapping function in SynchronizedTabulatedFunction");
            syncFunction = new SynchronizedTabulatedFunction(function);
        }

        try {
            TabulatedFunction result = syncFunction.doSynchronously(this::derive);
            logger.info("Synchronous derivative computation completed successfully. Result type: {}",
                    result.getClass().getSimpleName());
            return result;
        } catch (Exception e) {
            logger.error("Error during synchronous derivative computation", e);
            throw e;
        }
    }
}