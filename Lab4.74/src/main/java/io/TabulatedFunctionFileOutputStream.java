package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {

        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        try (FileOutputStream fos1 = new FileOutputStream("output/array function.bin");
             BufferedOutputStream bos1 = new BufferedOutputStream(fos1);
             FileOutputStream fos2 = new FileOutputStream("output/linked list function.bin");
             BufferedOutputStream bos2 = new BufferedOutputStream(fos2)) {

            FunctionsIO.writeTabulatedFunction(bos1, arrayFunction);
            FunctionsIO.writeTabulatedFunction(bos2, linkedListFunction);

            System.out.println("Функции успешно записаны в бинарные файлы:");
            System.out.println("- output/array function.bin");
            System.out.println("- output/linked list function.bin");
        } catch (IOException e){
            e.printStackTrace();
        }

        File arrayFile = new File("output/array function.bin");
        File linkedListFile = new File("output/linked list function.bin");

        System.out.println("\nПроверка создания файлов:");
        System.out.println("Array function file exists: " + arrayFile.exists() + ", size: " + arrayFile.length() + " bytes");
        System.out.println("LinkedList function file exists: " + linkedListFile.exists() + ", size: " + linkedListFile.length() + " bytes");

        if (arrayFile.exists() && linkedListFile.exists()) {
            System.out.println("\nОба файла созданы успешно!");
            System.out.println("Размер каждого файла: " + arrayFile.length() + " байт");
            System.out.println("Ожидаемый размер: " + (4 + 5 * 2 * 8) + " байт " + "(4 байта на count + 5 точек × 2 значения × 8 байт на double)");
        }
    }
}
