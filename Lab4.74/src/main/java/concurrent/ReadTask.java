package concurrent;

import functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ReadTask.class);

    private TabulatedFunction function;
    private final Object lock;

    public ReadTask(TabulatedFunction function, Object lock) {
        this.function = function;
        this.lock = lock;
        logger.debug("ReadTask initialized for function with {} points",
                function != null ? function.getCount() : 0);
    }

    @Override
    public void run() {
        logger.info("Starting ReadTask execution");

        try {
            for (int i = 0; i < function.getCount(); i++) {
                synchronized (lock) {
                    logger.trace("Acquired lock for reading point {}", i);

                    double x = function.getX(i);
                    double y = function.getY(i);

                    logger.debug("Read point: index = {}, x = {}, y = {}", i, x, y);
                    System.out.printf("After read: i = %d, x = %f, y = %f%n", i, x, y);

                    logger.trace("Releasing lock after reading point {}", i);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn("ReadTask was interrupted during sleep", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            logger.info("ReadTask completed successfully. Processed {} points",
                    function.getCount());

        } catch (Exception e) {
            logger.error("Error during ReadTask execution", e);
        }
    }
}
