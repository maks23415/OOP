package concurrent;

import functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WriteTask.class);

    private TabulatedFunction function;
    private double value;
    private final Object lock;

    public WriteTask(TabulatedFunction function, double value, Object lock) {
        this.function = function;
        this.value = value;
        this.lock = lock;
        logger.debug("WriteTask initialized for function with {} points, value to set: {}",
                function != null ? function.getCount() : 0, value);
    }

    @Override
    public void run() {
        logger.info("Starting WriteTask execution. Setting Y values to: {}", value);

        try {
            for (int i = 0; i < function.getCount(); i++) {
                synchronized (lock) {
                    logger.trace("Acquired lock for writing point {}", i);

                    double oldValue = function.getY(i);
                    function.setY(i, value);

                    logger.debug("Write operation: index = {}, old Y = {}, new Y = {}",
                            i, oldValue, value);
                    System.out.printf("Writing for index %d complete%n", i);

                    logger.trace("Releasing lock after writing point {}", i);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn("WriteTask was interrupted during sleep", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            logger.info("WriteTask completed successfully. Updated {} points with value {}",
                    function.getCount(), value);

        } catch (Exception e) {
            logger.error("Error during WriteTask execution", e);
        }
    }
}
