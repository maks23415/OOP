package concurrent;

import functions.TabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.ConstantFunction;

public class ReadWriteTaskExecutor {

    public static void main(String[] args) {
        TabulatedFunction function = new LinkedListTabulatedFunction(
                new ConstantFunction(-1), 1, 1000, 1000
        );

        ReadTask readTask = new ReadTask(function);
        WriteTask writeTask = new WriteTask(function, 0.5);

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);

        readThread.start();
        writeThread.start();

        try {
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
