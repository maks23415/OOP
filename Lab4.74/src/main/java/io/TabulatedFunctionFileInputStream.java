package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {

    public static void main(String[] args) {
        System.out.println("=== Чтение из бинарного файла ===");

        try (FileInputStream fis = new FileInputStream("input");
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction functionFromFile = FunctionsIO.readTabulatedFunction(bis, arrayFactory);

            System.out.println("Функция, прочитанная из файла:");
            System.out.println(functionFromFile.toString());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении из файла:");
            e.printStackTrace();
        }

        System.out.println("\n=== Чтение из консоли ===");

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Введите размер и значения функции");
            System.out.println("Формат ввода:");
            System.out.println("3          <- количество точек");
            System.out.println("1.0 2.0    <- первая точка: x=1.0, y=2.0");
            System.out.println("2.0 4.0    <- вторая точка: x=2.0, y=4.0");
            System.out.println("3.0 6.0    <- третья точка: x=3.0, y=6.0");
            System.out.println("\nВведите данные:");

            TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
            TabulatedFunction functionFromConsole = FunctionsIO.readTabulatedFunction(consoleReader, linkedListFactory);

            System.out.println("\nФункция, введенная из консоли:");
            System.out.println(functionFromConsole.toString());

            TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = differentialOperator.derive(functionFromConsole);

            System.out.println("Производная функции:");
            System.out.println(derivative.toString());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении из консоли:");
            e.printStackTrace();
        }
    }
}

