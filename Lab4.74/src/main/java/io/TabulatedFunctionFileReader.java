package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TabulatedFunctionFileReader {

    public static void main(String[] args) {

        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();

        try (
                FileReader fileReader1 = new FileReader("input/function.txt");
                BufferedReader bufferedReader1 = new BufferedReader(fileReader1);

                FileReader fileReader2 = new FileReader("input/function.txt");
                BufferedReader bufferedReader2 = new BufferedReader(fileReader2)
        ) {

            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(bufferedReader1, arrayFactory);
            System.out.println("Array function:");
            System.out.println(arrayFunction.toString());
            System.out.println();

            TabulatedFunction linkedListFunction = FunctionsIO.readTabulatedFunction(bufferedReader2, linkedListFactory);
            System.out.println("Linked list function:");
            System.out.println(linkedListFunction.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
