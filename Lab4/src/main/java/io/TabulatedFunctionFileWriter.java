package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TabulatedFunctionFileWriter {

    public static void main(String[] args) {

        double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        try (
                FileWriter arrayFileWriter = new FileWriter("output/array function.txt");
                BufferedWriter arrayBufferedWriter = new BufferedWriter(arrayFileWriter);

                FileWriter linkedListFileWriter = new FileWriter("output/linked list function.txt");
                BufferedWriter linkedListBufferedWriter = new BufferedWriter(linkedListFileWriter)
        ) {
            FunctionsIO.writeTabulatedFunction(arrayBufferedWriter, arrayFunction);
            System.out.println("Array function записан в output/array function.txt");

            FunctionsIO.writeTabulatedFunction(linkedListBufferedWriter, linkedListFunction);
            System.out.println("Linked list function записан в output/linked list function.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
