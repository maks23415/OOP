package io;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class FunctionsIO {
    private static final Logger logger = LoggerFactory.getLogger(FunctionsIO.class);

    private FunctionsIO() {
        throw new UnsupportedOperationException("Невозможно создать экземпляр служебного класса");
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) throws IOException {
        logger.info("Writing tabulated function to text stream. Function type: {}, Points: {}",
                function.getClass().getSimpleName(), function.getCount());

        PrintWriter printWriter = new PrintWriter(writer);

        int count = function.getCount();
        printWriter.println(count);
        logger.debug("Writing point count: {}", count);

        int pointsWritten = 0;
        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
            pointsWritten++;
            logger.trace("Written point {}: x={}, y={}", pointsWritten, point.x, point.y);
        }

        printWriter.flush();
        logger.info("Successfully wrote {} points to text stream", pointsWritten);
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function)
            throws IOException {
        logger.info("Writing tabulated function to binary stream. Function type: {}, Points: {}",
                function.getClass().getSimpleName(), function.getCount());

        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        int count = function.getCount();

        dataOutputStream.writeInt(count);
        logger.debug("Writing binary point count: {}", count);

        int pointsWritten = 0;
        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
            pointsWritten++;
            logger.trace("Written binary point {}: x={}, y={}", pointsWritten, point.x, point.y);
        }

        outputStream.flush();
        logger.info("Successfully wrote {} points to binary stream", pointsWritten);
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory)
            throws IOException {
        logger.info("Reading tabulated function from text stream. Factory: {}",
                factory.getClass().getSimpleName());

        try {
            String countLine = reader.readLine();
            if (countLine == null) {
                logger.error("Empty file encountered while reading tabulated function");
                throw new IOException("Файл пуст");
            }

            int count = Integer.parseInt(countLine.trim());
            logger.debug("Reading {} points from text stream", count);

            if (count <= 0) {
                logger.error("Invalid point count: {}", count);
                throw new IOException("Некорректное количество точек: " + count);
            }

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("ru"));
            logger.debug("Using number format for locale: ru");

            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line == null) {
                    logger.error("Unexpected end of file at point {} of {}", i, count);
                    throw new IOException("Неожиданный конец файла");
                }

                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    logger.error("Invalid line format at line {}: '{}'", i + 2, line);
                    throw new IOException("Неверный формат в строке: " + line);
                }

                try {
                    Number xNumber = numberFormat.parse(parts[0].trim());
                    Number yNumber = numberFormat.parse(parts[1].trim());

                    xValues[i] = xNumber.doubleValue();
                    yValues[i] = yNumber.doubleValue();

                    logger.trace("Parsed point {}: x={}, y={}", i, xValues[i], yValues[i]);

                } catch (ParseException e) {
                    logger.error("Parse error at line {}: '{}'. Error: {}", i + 2, line, e.getMessage());
                    throw new IOException("Ошибка парсинга чисел в строке: " + line, e);
                }
            }

            TabulatedFunction result = factory.create(xValues, yValues);
            logger.info("Successfully read tabulated function from text stream. Result type: {}, Points: {}",
                    result.getClass().getSimpleName(), result.getCount());
            return result;

        } catch (NumberFormatException e) {
            logger.error("Number format error while reading tabulated function", e);
            throw new IOException("Неверный формат числа", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        logger.info("Reading tabulated function from binary stream. Factory: {}",
                factory.getClass().getSimpleName());

        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int count = dataInputStream.readInt();
        logger.debug("Reading binary point count: {}", count);

        if (count <= 0) {
            logger.error("Invalid binary point count: {}", count);
            throw new IOException("Некорректное количество точек в бинарном потоке: " + count);
        }

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        logger.debug("Reading {} points from binary stream", count);
        for (int i = 0; i < count; i++) {
            xValues[i] = dataInputStream.readDouble();
            yValues[i] = dataInputStream.readDouble();
            logger.trace("Read binary point {}: x={}, y={}", i, xValues[i], yValues[i]);
        }

        TabulatedFunction result = factory.create(xValues, yValues);
        logger.info("Successfully read tabulated function from binary stream. Result type: {}, Points: {}",
                result.getClass().getSimpleName(), result.getCount());
        return result;
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        logger.info("Serializing tabulated function. Function type: {}, Points: {}",
                function.getClass().getSimpleName(), function.getCount());

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
        objectOutputStream.writeObject(function);
        objectOutputStream.flush();

        logger.debug("Serialization completed successfully");
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream)
            throws IOException, ClassNotFoundException {
        logger.info("Deserializing tabulated function from stream");

        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        TabulatedFunction function = (TabulatedFunction) objectInputStream.readObject();

        logger.info("Successfully deserialized tabulated function. Type: {}, Points: {}",
                function.getClass().getSimpleName(), function.getCount());
        return function;
    }
}