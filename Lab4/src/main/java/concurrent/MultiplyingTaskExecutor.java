package concurrent;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.UnitFunction;

import java.util.ArrayList;
import java.util.List;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) {
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);

        List<Thread> threads = new ArrayList<>();
        int threadCount = 10;

        for (int i = 0; i < threadCount; i++) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.err.println("Главный поток был прерван: " + e.getMessage());
        }

        System.out.println("Табулированная функция после выполнения потоков:");
        for (int i = 0; i < function.getCount(); i++) {
            System.out.printf("x = %f, y = %f%n", function.getX(i), function.getY(i));
        }
    }
}
