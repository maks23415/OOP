package functions;

import exceptions.InterpolationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable
{

    public static class Node
    {
        public double x;
        public double y;
        public Node next;
        public Node prev;

        public Node(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
    }

    private Node head;
    private int count;

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues)
    {
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Длина должна быть не менее 2");
        }
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count)
    {
        if (count < 2)
        {
            throw new IllegalArgumentException("Длина меньше минимальной 2 точки");
        }

        if (xFrom > xTo)
        {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        double step =  (xTo - xFrom) / (count - 1);

        for (int i = 0; i < count; i++)
        {
            double x = xFrom + i * step;
            double y = source.apply(x);
            addNode(x, y);
        }
    }

    private void addNode(double x, double y)
    {
        Node newNode = new Node(x, y);

        if (head == null)
        {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else
        {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }

        count++;
    }

    private Node getNode(int index)
    {
        if (index < 0 || index >= count)
        {
            throw new IllegalArgumentException("Индекс выходит за границы: " + index);
        }

        Node current = head;
        for (int i = 0; i < index; i++)
        {
            current = current.next;
        }
        return current;
    }

    private Node floorNodeOfX(double x) {
        if (head == null) {
            throw new IllegalStateException("Функция пустая");
        }

        if (x < head.x) {
            throw new IllegalArgumentException("x меньше левой границы: " + x);
        }
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.x - x) < 1e-10) {
                return current;
            }

            if (current.x < x && (current.next == head || x < current.next.x)) {
                return current;
            }
            current = current.next;

        }
        return head.prev.prev;
    }


        @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public double getX(int index)
    {
        Node node = getNode(index);
        return node.x;
    }

    @Override
    public double getY(int index)
    {
        Node node = getNode(index);
        return node.y;
    }

    @Override
    public void setY(int index, double value)
    {
        Node node = getNode(index);
        node.y = value;
    }

    @Override
    public int indexOfX(double x)
    {
        if (head == null)
        {
            return -1;
        }

        Node current = head;
        for (int i = 0; i < count; i++)
        {
            if (Math.abs(current.x - x) < 1e-10)
            {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y)
    {
        if (head == null)
        {
            return -1;
        }

        Node current = head;
        for (int i = 0; i < count; i++)
        {
            if (Math.abs(current.y - y) < 1e-10)
            {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public double leftBound()
    {
        if (head == null)
        {
            throw new IllegalStateException("Функция пустая");
        }
        return head.x;
    }

    @Override
    public double rightBound()
    {
        if (head == null)
        {
            throw new IllegalStateException("Функция пустая");
        }
        return head.prev.x;
    }

    @Override
    protected int floorIndexOfX(double x)
    {
        if (head == null)
        {
            return 0;
        }

        if (x < head.x)
        {
            throw new IllegalArgumentException("x меньше левой границы: " + x);
        }

        Node floorNode = floorNodeOfX(x);

        Node current = head;
        for (int i = 0; i < count; i++)
        {
            if (current == floorNode)
            {
                return i;
            }
            current = current.next;
        }

        return - 1;
    }

    @Override
    protected double extrapolateLeft(double x)
    {

        Node first = head;
        Node second = head.next;
        return interpolate(x, first.x, second.x, first.y, second.y);
    }

    @Override
    protected double extrapolateRight(double x)
    {

        Node last = head.prev;
        Node prevLast = last.prev;
        return interpolate(x, prevLast.x, last.x, prevLast.y, last.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex)
    {
        if (floorIndex < 0 || floorIndex >= count - 1)
        {
            throw new IllegalArgumentException("Некорректный floorIndex: " + floorIndex);
        }

        Node leftNode = getNode(floorIndex);
        Node rightNode = getNode(floorIndex + 1);
        if (x < leftNode.x || x > rightNode.x) {
            throw new InterpolationException("x вышел за пределы interpolation");
        }
        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    @Override
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY)
    {
        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
    }
    @Override
    public void insert(double x, double y) {
        if (head == null) {
            addNode(x, y);
            return;
        }

        Node current = head;
        do {
            if (Math.abs(current.x - x) < 1e-10) {
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
            Node last = head.prev;
            newNode.next = head;
            newNode.prev = last;
            head.prev = newNode;
            last.next = newNode;
            head = newNode;
            count++;
        } else {
            Node prevNode = current.prev;
            newNode.next = current;
            newNode.prev = prevNode;
            prevNode.next = newNode;
            current.prev = newNode;
            count++;
        }
    }
    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс за пределами допустимого диапазона: " + index);
        }

        if (count == 1) {
            head = null;
            count = 0;
            return;
        }

        Node nodeToRemove = getNode(index);

        if (nodeToRemove == head) {
            head = head.next;
        }

        Node prevNode = nodeToRemove.prev;
        Node nextNode = nodeToRemove.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        count--;

        if (count == 0) {
            head = null;
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private Node currentNode = head;
            private int elementsReturned = 0;

            @Override
            public boolean hasNext() {
                return elementsReturned < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Нет элементов");
                }
                Point point = new Point(currentNode.x, currentNode.y);
                currentNode = currentNode.next;
                elementsReturned++;
                return point;
            }
        };
    }

}