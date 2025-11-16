package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Point;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:framework/application.properties")
class PointRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PointRepository pointRepository;

    @Test
    void testFindByFuncId() {
        // Генерация данных
        Point point1 = new Point(1L, 1.0, 2.0);
        Point point2 = new Point(1L, 2.0, 4.0);
        Point point3 = new Point(2L, 3.0, 6.0); // Другая функция

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);
        entityManager.flush();
        entityManager.clear();

        // Поиск по ID функции
        List<Point> points = pointRepository.findByFuncId(1L);

        // Проверка
        assertEquals(2, points.size());
        assertTrue(points.stream().anyMatch(p -> p.getXValue().equals(1.0)));
        assertTrue(points.stream().anyMatch(p -> p.getXValue().equals(2.0)));
    }

    @Test
    void testFindByXValueBetween() {
        // Генерация данных
        Point point1 = new Point(1L, 1.0, 2.0);
        Point point2 = new Point(1L, 5.0, 10.0);
        Point point3 = new Point(1L, 10.0, 20.0);

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);
        entityManager.flush();
        entityManager.clear();

        // Поиск по диапазону X
        List<Point> points = pointRepository.findByXValueBetween(2.0, 8.0);

        // Проверка
        assertEquals(1, points.size());
        assertEquals(5.0, points.get(0).getXValue());
    }

    @Test
    void testFindPointsInArea() {
        // Генерация данных
        Point point1 = new Point(1L, 2.0, 3.0);
        Point point2 = new Point(1L, 5.0, 7.0);
        Point point3 = new Point(1L, 8.0, 9.0);

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);
        entityManager.flush();
        entityManager.clear();

        // Поиск в прямоугольной области
        List<Point> points = pointRepository.findPointsInArea(1.0, 6.0, 2.0, 8.0);

        // Проверка
        assertEquals(2, points.size());
        assertTrue(points.stream().anyMatch(p -> p.getXValue().equals(2.0)));
        assertTrue(points.stream().anyMatch(p -> p.getXValue().equals(5.0)));
    }

    @Test
    void testCountByFuncId() {
        // Генерация данных
        Point point1 = new Point(1L, 1.0, 2.0);
        Point point2 = new Point(1L, 2.0, 4.0);
        Point point3 = new Point(1L, 3.0, 6.0);
        Point point4 = new Point(2L, 4.0, 8.0); // Другая функция

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);
        pointRepository.save(point4);
        entityManager.flush();

        // Подсчет точек функции
        long count = pointRepository.countByFuncId(1L);

        // Проверка
        assertEquals(3, count);
    }

    @Test
    void testDeletePoint() {
        // Генерация данных
        Point point = new Point(1L, 10.0, 20.0);
        Point savedPoint = pointRepository.save(point);
        entityManager.flush();

        // Проверка что точка существует
        assertTrue(pointRepository.findById(savedPoint.getId()).isPresent());

        // Удаление
        pointRepository.deleteById(savedPoint.getId());
        entityManager.flush();

        // Проверка что точка удалена
        assertFalse(pointRepository.findById(savedPoint.getId()).isPresent());
    }

    @Test
    void testOrderByXValue() {
        // Генерация данных в разном порядке
        Point point1 = new Point(1L, 3.0, 6.0);
        Point point2 = new Point(1L, 1.0, 2.0);
        Point point3 = new Point(1L, 2.0, 4.0);

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);
        entityManager.flush();
        entityManager.clear();

        // Проверка сортировки по возрастанию X
        List<Point> pointsAsc = pointRepository.findByFuncIdOrderByXValueAsc(1L);

        assertEquals(3, pointsAsc.size());
        assertEquals(1.0, pointsAsc.get(0).getXValue());
        assertEquals(2.0, pointsAsc.get(1).getXValue());
        assertEquals(3.0, pointsAsc.get(2).getXValue());
    }
}