package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
class PointRepositoryTest {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    private Function testFunction1;
    private Function testFunction2;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        pointRepository.deleteAll();
        functionRepository.deleteAll();
        userRepository.deleteAll();

        // Генерация тестовых данных
        generateTestData();
    }

    private void generateTestData() {
        // Создаем пользователя и функции
        User user = new User("points_user", "researcher", "points123");
        userRepository.save(user);

        testFunction1 = new Function("test_func_1", "y = x", user);
        testFunction2 = new Function("test_func_2", "y = x^2", user);

        functionRepository.save(testFunction1);
        functionRepository.save(testFunction2);

        // Создаем точки для функций
        Point point1 = new Point(1.0, 1.0, testFunction1);
        Point point2 = new Point(2.0, 2.0, testFunction1);
        Point point3 = new Point(1.0, 1.0, testFunction2);
        Point point4 = new Point(2.0, 4.0, testFunction2);
        Point point5 = new Point(3.0, 9.0, testFunction2);

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);
        pointRepository.save(point4);
        pointRepository.save(point5);
    }

    @Test
    void whenFindByFunction_thenReturnPoints() {
        // Поиск точек по функции
        List<Point> function1Points = pointRepository.findByFunction(testFunction1);

        assertThat(function1Points).hasSize(2);
        assertThat(function1Points)
                .extracting(Point::getXValue)
                .containsExactlyInAnyOrder(1.0, 2.0);
    }

    @Test
    void whenFindByFunctionId_thenReturnPoints() {
        // Поиск точек по ID функции
        List<Point> function2Points = pointRepository.findByFunctionId(testFunction2.getId());

        assertThat(function2Points).hasSize(3);
        assertThat(function2Points)
                .extracting(Point::getYValue)
                .containsExactlyInAnyOrder(1.0, 4.0, 9.0);
    }

    @Test
    void whenFindByUserLogin_thenReturnPoints() {
        // Поиск точек по логину пользователя
        List<Point> userPoints = pointRepository.findByUserLogin("points_user");

        assertThat(userPoints).hasSize(5); // Все точки принадлежат этому пользователю
    }

    @Test
    void whenFindByXValueBetween_thenReturnPointsInRange() {
        // Поиск точек в диапазоне X
        List<Point> pointsInRange = pointRepository.findByXValueBetween(1.5, 2.5);

        assertThat(pointsInRange).hasSize(2); // Точки с x=2.0
        assertThat(pointsInRange)
                .extracting(Point::getXValue)
                .containsOnly(2.0);
    }

    @Test
    void whenFindByYValueBetween_thenReturnPointsInRange() {
        // Поиск точек в диапазоне Y
        List<Point> pointsInRange = pointRepository.findByYValueBetween(3.0, 8.0);

        assertThat(pointsInRange).hasSize(1); // Точка с y=4.0
        assertThat(pointsInRange.get(0).getYValue()).isEqualTo(4.0);
    }

    @Test
    void whenCreateNewPoint_thenPointShouldBePersisted() {
        // Создание новой точки
        Point newPoint = new Point(5.0, 25.0, testFunction2);
        Point saved = pointRepository.save(newPoint);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getXValue()).isEqualTo(5.0);
        assertThat(saved.getYValue()).isEqualTo(25.0);
        assertThat(saved.getFunction().getId()).isEqualTo(testFunction2.getId());
    }

    @Test
    void whenUpdatePoint_thenChangesShouldBePersisted() {
        // Обновление точки
        List<Point> points = pointRepository.findByFunctionId(testFunction1.getId());
        assertThat(points).isNotEmpty();

        Point point = points.get(0);
        point.setXValue(10.0);
        point.setYValue(10.0);

        Point updated = pointRepository.save(point);

        assertThat(updated.getXValue()).isEqualTo(10.0);
        assertThat(updated.getYValue()).isEqualTo(10.0);
    }

    @Test
    void whenDeletePoint_thenPointShouldBeRemoved() {
        // Удаление точки
        List<Point> points = pointRepository.findByFunctionId(testFunction2.getId());
        assertThat(points).hasSize(3);

        pointRepository.delete(points.get(0));

        // Проверяем, что точка удалена
        List<Point> remainingPoints = pointRepository.findByFunctionId(testFunction2.getId());
        assertThat(remainingPoints).hasSize(2);
    }

    @Test
    void whenDeleteByFunctionId_thenAllFunctionPointsShouldBeRemoved() {
        // Удаление всех точек функции
        pointRepository.deleteByFunctionId(testFunction1.getId());

        // Проверяем, что точки удалены
        List<Point> remainingPoints = pointRepository.findByFunctionId(testFunction1.getId());
        assertThat(remainingPoints).isEmpty();

        // Проверяем, что точки другой функции остались
        List<Point> function2Points = pointRepository.findByFunctionId(testFunction2.getId());
        assertThat(function2Points).hasSize(3);
    }
}