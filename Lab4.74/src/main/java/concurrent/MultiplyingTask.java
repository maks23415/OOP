package concurrent;

import functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplyingTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTask.class);

    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
        logger.debug("MultiplyingTask initialized for function with {} points",
                function != null ? function.getCount() : 0);
    }

    @Override
    public void run() {
        logger.info("Thread '{}' started MultiplyingTask execution", Thread.currentThread().getName());

        try {
            int totalPoints = function.getCount();
            logger.debug("Processing {} points in function", totalPoints);

            for (int i = 0; i < totalPoints; i++) {
                synchronized (function) {
                    logger.trace("Thread '{}' acquired lock for point {}",
                            Thread.currentThread().getName(), i);

                    double x = function.getX(i);
                    double currentY = function.getY(i);
                    double newY = currentY * 2;

                    function.setY(i, newY);

                    logger.debug("Multiplication: index = {}, x = {}, old Y = {}, new Y = {} ({} × 2)",
                            i, x, currentY, newY, currentY);

                    logger.trace("Thread '{}' releasing lock for point {}",
                            Thread.currentThread().getName(), i);
                }

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    logger.warn("Thread '{}' was interrupted during multiplication task",
                            Thread.currentThread().getName(), e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            logger.info("Thread '{}' successfully completed MultiplyingTask. Processed {} points",
                    Thread.currentThread().getName(), totalPoints);

        } catch (Exception e) {
            logger.error("Thread '{}' encountered error during MultiplyingTask execution",
                    Thread.currentThread().getName(), e);
        }

        System.out.println("Поток " + Thread.currentThread().getName() + " завершил выполнение MultiplyingTask");
    }
}