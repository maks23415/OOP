package io;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class FunctionsIO {
    private FunctionsIO() {
        throw new UnsupportedOperationException("Невозможно создать экземпляр служебного класса");
    }
    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.println(function.getCount());

        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
        }

        printWriter.flush();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function)
            throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(function.getCount());

        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
        }

        outputStream.flush();
    }


    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory)
            throws IOException {
        try {
            String countLine = reader.readLine();
            if (countLine == null) {
                throw new IOException("Файл пуст");
            }

            int count = Integer.parseInt(countLine.trim());

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Неожиданный конец файла");
                }

                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    throw new IOException("Неверный формат в строке: " + line);
                }

                try {
                    Number xNumber = numberFormat.parse(parts[0].trim());
                    Number yNumber = numberFormat.parse(parts[1].trim());

                    xValues[i] = xNumber.doubleValue();
                    yValues[i] = yNumber.doubleValue();

                } catch (ParseException e) {
                    throw new IOException("Ошибка парсинга чисел в строке: " + line, e);
                }
            }

            return factory.create(xValues, yValues);

        } catch (NumberFormatException e) {
            throw new IOException("Неверный формат числа", e);
        }
    }

}
