package functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeFunction implements MathFunction {
    private static final Logger logger = LoggerFactory.getLogger(CompositeFunction.class);

    private MathFunction firstFunction;
    private MathFunction secondFunction;

    public CompositeFunction(MathFunction firstFunction, MathFunction secondFunction) {
        logger.debug("Creating CompositeFunction: firstFunction={}, secondFunction={}",
                firstFunction.getClass().getSimpleName(),
                secondFunction.getClass().getSimpleName());
        this.firstFunction = firstFunction;
        this.secondFunction = secondFunction;
    }

    @Override
    public double apply(double x) {
        logger.debug("Applying composite function for x={}", x);

        double firstResult = firstFunction.apply(x);
        logger.trace("First function applied: f({}) = {}", x, firstResult);

        double finalResult = secondFunction.apply(firstResult);
        logger.debug("Composite function result: g(f({})) = {}", x, finalResult);

        return finalResult;
    }

    // Геттеры для отладки (опционально)
    public MathFunction getFirstFunction() {
        logger.trace("Accessing first function");
        return firstFunction;
    }

    public MathFunction getSecondFunction() {
        logger.trace("Accessing second function");
        return secondFunction;
    }
}