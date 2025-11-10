package functions;

import exceptions.InterpolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(LinkedListTabulatedFunction.class);

    @Serial
    private static final long serialVersionUID = 7380647716575850766L;

    public LinkedListTabulatedFunction(ArrayTabulatedFunction points) {
        super();
        logger.debug("Creating LinkedListTabulatedFunction from ArrayTabulatedFunction");
        // TODO: Implement conversion from array to linked list
    }

    public static class Node {
        public double x;
        public double y;
        public Node next;
        public Node prev;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private Node head;
    private int count;

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        logger.debug("Creating LinkedListTabulatedFunction from arrays: xValues.length={}, yValues.length={}",
                xValues.length, yValues.length);

        if (xValues.length < 2) {
            logger.error("Attempt to create function with insufficient points: {}", xValues.length);
            throw new IllegalArgumentException("Длина должна быть не менее 2");
        }
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }

        logger.info("LinkedListTabulatedFunction created successfully. Count: {}", count);
        logger.debug("X range: [{}, {}], Y range: [{}, {}]",
                xValues[0], xValues[xValues.length-1],
                yValues[0], yValues[yValues.length-1]);
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        logger.debug("Creating LinkedListTabulatedFunction from source function: xFrom={}, xTo={}, count={}",
                xFrom, xTo, count);

        if (count < 2) {
            logger.error("Invalid count provided: {}", count);
            throw new IllegalArgumentException("Длина меньше минимальной 2 точки");
        }

        if (xFrom > xTo) {
            logger.debug("Swapping xFrom and xTo: {} -> {}", xFrom, xTo);
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        double step = (xTo - xFrom) / (count - 1);
        logger.debug("Step calculated: {}", step);

        for (int i = 0; i < count; i++) {
            double x = xFrom + i * step;
            double y = source.apply(x);
            addNode(x, y);
            logger.trace("Added point {}: x={}, y={}", i, x, y);
        }

        logger.info("LinkedListTabulatedFunction created from source. Count: {}", count);
    }

    private void addNode(double x, double y) {
        logger.trace("Adding node: x={}, y={}", x, y);
        Node newNode = new Node(x, y);

        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
            logger.debug("Created first node and established circular reference");
        } else {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
            logger.trace("Added node to end of list");
        }

        count++;
        logger.trace("Node added. New count: {}", count);
    }

    private Node getNode(int index) {
        logger.trace("Getting node at index: {}", index);
        if (index < 0 || index >= count) {
            logger.error("Invalid node index: {} (count: {})", index, count);
            throw new IllegalArgumentException("Индекс выходит за границы: " + index);
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        logger.trace("Node found at index {}: x={}, y={}", index, current.x, current.y);
        return current;
    }

    private Node floorNodeOfX(double x) {
        logger.trace("Finding floor node for x={}", x);
        if (head == null) {
            logger.error("Attempt to find floor node in empty function");
            throw new IllegalStateException("Функция пустая");
        }

        if (x < head.x) {
            logger.error("x={} is less than left bound {}", x, head.x);
            throw new IllegalArgumentException("x меньше левой границы: " + x);
        }

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.x - x) < 1e-10) {
                logger.trace("Exact match found at node: x={}", current.x);
                return current;
            }

            if (current.x < x && (current.next == head || x < current.next.x)) {
                logger.trace("Floor node found: x={}", current.x);
                return current;
            }
            current = current.next;
        }

        Node result = head.prev.prev;
        logger.trace("Using second last node as floor: x={}", result.x);
        return result;
    }

    @Override
    public int getCount() {
        logger.trace("Getting count: {}", count);
        return count;
    }

    @Override
    public double getX(int index) {
        logger.trace("Getting x at index: {}", index);
        Node node = getNode(index);
        double result = node.x;
        logger.trace("x[{}] = {}", index, result);
        return result;
    }

    @Override
    public double getY(int index) {
        logger.trace("Getting y at index: {}", index);
        Node node = getNode(index);
        double result = node.y;
        logger.trace("y[{}] = {}", index, result);
        return result;
    }

    @Override
    public void setY(int index, double value) {
        logger.debug("Setting y at index {} to {}", index, value);
        Node node = getNode(index);
        double oldValue = node.y;
        node.y = value;
        logger.debug("y[{}] changed from {} to {}", index, oldValue, value);
    }

    @Override
    public int indexOfX(double x) {
        logger.trace("Searching for x={}", x);
        if (head == null) {
            logger.trace("Function is empty, x not found");
            return -1;
        }

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.x - x) < 1e-10) {
                logger.trace("x={} found at index {}", x, i);
                return i;
            }
            current = current.next;
        }
        logger.trace("x={} not found", x);
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        logger.trace("Searching for y={}", y);
        if (head == null) {
            logger.trace("Function is empty, y not found");
            return -1;
        }

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.y - y) < 1e-10) {
                logger.trace("y={} found at index {}", y, i);
                return i;
            }
            current = current.next;
        }
        logger.trace("y={} not found", y);
        return -1;
    }

    @Override
    public double leftBound() {
        if (head == null) {
            logger.error("Attempt to get left bound of empty function");
            throw new IllegalStateException("Функция пустая");
        }
        double bound = head.x;
        logger.trace("Left bound: {}", bound);
        return bound;
    }

    @Override
    public double rightBound() {
        if (head == null) {
            logger.error("Attempt to get right bound of empty function");
            throw new IllegalStateException("Функция пустая");
        }
        double bound = head.prev.x;
        logger.trace("Right bound: {}", bound);
        return bound;
    }

    @Override
    protected int floorIndexOfX(double x) {
        logger.trace("Finding floor index for x={}", x);
        if (head == null) {
            logger.warn("Empty function, returning index 0");
            return 0;
        }

        if (x < head.x) {
            logger.error("x={} is less than left bound {}", x, head.x);
            throw new IllegalArgumentException("x меньше левой границы: " + x);
        }

        Node floorNode = floorNodeOfX(x);

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current == floorNode) {
                logger.trace("Floor index found: {}", i);
                return i;
            }
            current = current.next;
        }

        logger.error("Floor node not found in list");
        return -1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        logger.debug("Left extrapolation for x={}", x);
        Node first = head;
        Node second = head.next;
        double result = interpolate(x, first.x, second.x, first.y, second.y);
        logger.debug("Left extrapolation result: {}", result);
        return result;
    }

    @Override
    protected double extrapolateRight(double x) {
        logger.debug("Right extrapolation for x={}", x);
        Node last = head.prev;
        Node prevLast = last.prev;
        double result = interpolate(x, prevLast.x, last.x, prevLast.y, last.y);
        logger.debug("Right extrapolation result: {}", result);
        return result;
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        logger.debug("Interpolating x={} at floorIndex={}", x, floorIndex);
        if (floorIndex < 0 || floorIndex >= count - 1) {
            logger.error("Invalid floorIndex: {} (count: {})", floorIndex, count);
            throw new IllegalArgumentException("Некорректный floorIndex: " + floorIndex);
        }

        Node leftNode = getNode(floorIndex);
        Node rightNode = getNode(floorIndex + 1);
        if (x < leftNode.x || x > rightNode.x) {
            logger.error("x={} out of interpolation range [{}, {}]",
                    x, leftNode.x, rightNode.x);
            throw new InterpolationException("x вышел за пределы interpolation");
        }

        double result = interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
        logger.debug("Interpolation result: {}", result);
        return result;
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        logger.trace("Linear interpolation: x={}, interval=[{}, {}], values=[{}, {}]",
                x, leftX, rightX, leftY, rightY);
        double result = leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
        logger.trace("Interpolation result: {}", result);
        return result;
    }

    @Override
    public void insert(double x, double y) {
        logger.debug("Inserting point: x={}, y={}", x, y);

        if (head == null) {
            logger.debug("Empty list, adding first node");
            addNode(x, y);
            return;
        }

        Node current = head;
        do {
            if (Math.abs(current.x - x) < 1e-10) {
                logger.debug("Point with x={} already exists. Updating y from {} to {}",
                        x, current.y, y);
                current.y = y;
                return;
            }
            if (current.x > x) {
                break;
            }
            current = current.next;
        } while (current != head);

        Node newNode = new Node(x, y);

        if (x < head.x) {
            logger.debug("Inserting at beginning of list");
            Node last = head.prev;
            newNode.next = head;
            newNode.prev = last;
            head.prev = newNode;
            last.next = newNode;
            head = newNode;
            count++;
        } else {
            logger.debug("Inserting in middle of list");
            Node prevNode = current.prev;
            newNode.next = current;
            newNode.prev = prevNode;
            prevNode.next = newNode;
            current.prev = newNode;
            count++;
        }

        logger.info("Point inserted. New count: {}", count);
    }

    @Override
    public void remove(int index) {
        logger.debug("Removing node at index: {}", index);
        if (index < 0 || index >= count) {
            logger.error("Invalid remove index: {} (count: {})", index, count);
            throw new IllegalArgumentException("Индекс за пределами допустимого диапазона: " + index);
        }

        if (count == 1) {
            logger.debug("Removing only node");
            head = null;
            count = 0;
            return;
        }

        Node nodeToRemove = getNode(index);
        double removedX = nodeToRemove.x;
        double removedY = nodeToRemove.y;

        if (nodeToRemove == head) {
            logger.debug("Removing head node");
            head = head.next;
        }

        Node prevNode = nodeToRemove.prev;
        Node nextNode = nodeToRemove.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        count--;

        if (count == 0) {
            logger.debug("List is now empty");
            head = null;
        }

        logger.info("Node removed at index {}: x={}, y={}. New count: {}",
                index, removedX, removedY, count);
    }

    @Override
    public Iterator<Point> iterator() {
        logger.trace("Creating iterator");
        return new Iterator<Point>() {
            private Node currentNode = head;
            private int elementsReturned = 0;

            @Override
            public boolean hasNext() {
                boolean hasNext = elementsReturned < count;
                logger.trace("Iterator hasNext: {} (elementsReturned={}, count={})",
                        hasNext, elementsReturned, count);
                return hasNext;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.error("Iterator next() called with no elements remaining");
                    throw new NoSuchElementException("Нет элементов");
                }
                Point point = new Point(currentNode.x, currentNode.y);
                logger.trace("Iterator next: point=({}, {})", point.x, point.y);
                currentNode = currentNode.next;
                elementsReturned++;
                return point;
            }
        };
    }
}