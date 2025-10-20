package io;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.factory.LinkedListTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    public static void main(String[] args) {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0}; // f(x) = x^2

        LinkedListTabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction firstDerivative = differentialOperator.derive(originalFunction);
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream("output/serialized linked list functions.bin"))) {
            FunctionsIO.serialize(outputStream, originalFunction);
            FunctionsIO.serialize(outputStream, firstDerivative);
            FunctionsIO.serialize(outputStream, secondDerivative);
            System.out.println("Функции успешно сериализованы в файл");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(
                new FileInputStream("output/serialized linked list functions.bin"))) {

            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(inputStream);
            System.out.println("\nИсходная функция:");
            System.out.println(deserializedOriginal.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
