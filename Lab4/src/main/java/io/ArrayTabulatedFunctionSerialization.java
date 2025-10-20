package io;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import java.io.*;

public class ArrayTabulatedFunctionSerialization {

    public static void main(String[] args) {

        double[] xValues1 = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues1 = {0.0, 0.25, 1.0, 2.25, 4.0};
        ArrayTabulatedFunction function1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues2 = {1.0, 2.0, 3.0, 4.0, 5.0};
        ArrayTabulatedFunction function2 = new ArrayTabulatedFunction(xValues2, yValues2);

        double[] xValues3 = {0.0, 0.5, 1.0, 1.5, 2.0};
        double[] yValues3 = {2.0, 1.5, 1.0, 1.5, 2.0};
        ArrayTabulatedFunction function3 = new ArrayTabulatedFunction(xValues3, yValues3);

        try (FileOutputStream fileOutputStream = new FileOutputStream("output/serialized array functions.bin");
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

            FunctionsIO.serialize(bufferedOutputStream, function1);
            FunctionsIO.serialize(bufferedOutputStream, function2);
            FunctionsIO.serialize(bufferedOutputStream, function3);

            System.out.println("Сериализация завершена успешно!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fileInputStream = new FileInputStream("output/serialized array functions.bin");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            TabulatedFunction deserializedFunction1 = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFunction2 = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFunction3 = FunctionsIO.deserialize(bufferedInputStream);

            System.out.println("\nФункция 1:");
            System.out.println(deserializedFunction1.toString());

            System.out.println("\nФункция 2:");
            System.out.println(deserializedFunction2.toString());

            System.out.println("\nФункция 3:");
            System.out.println(deserializedFunction3.toString());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}