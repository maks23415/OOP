package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:framework/application.properties")
class FunctionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUserId() {
        // Генерация данных
        User user = new User("testuser", "password", "test@example.com");
        User savedUser = userRepository.save(user);

        Function func1 = new Function(savedUser.getId(), "sin(x)", "trigonometric");
        Function func2 = new Function(savedUser.getId(), "x^2", "polynomial");
        Function func3 = new Function(999L, "other_user_func", "other"); // Другой пользователь

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);
        entityManager.flush();
        entityManager.clear();

        // Поиск по ID пользователя
        List<Function> userFunctions = functionRepository.findByUserId(savedUser.getId());

        // Проверка
        assertEquals(2, userFunctions.size());
        assertTrue(userFunctions.stream().anyMatch(f -> f.getName().equals("sin(x)")));
        assertTrue(userFunctions.stream().anyMatch(f -> f.getName().equals("x^2")));
    }

    @Test
    void testFindByType() {
        // Генерация данных
        Function func1 = new Function(1L, "sin(x)", "trigonometric");
        Function func2 = new Function(1L, "cos(x)", "trigonometric");
        Function func3 = new Function(1L, "x^2", "polynomial");

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);
        entityManager.flush();
        entityManager.clear();

        // Поиск по типу
        List<Function> trigFunctions = functionRepository.findByType("trigonometric");

        // Проверка
        assertEquals(2, trigFunctions.size());
        assertTrue(trigFunctions.stream().anyMatch(f -> f.getName().equals("sin(x)")));
        assertTrue(trigFunctions.stream().anyMatch(f -> f.getName().equals("cos(x)")));
    }

    @Test
    void testFindByNameContaining() {
        // Генерация данных
        Function func1 = new Function(1L, "linear_function", "linear");
        Function func2 = new Function(1L, "quadratic_function", "polynomial");
        Function func3 = new Function(1L, "exponential", "exponential");

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);
        entityManager.flush();
        entityManager.clear();

        // Поиск по части имени
        List<Function> functionsWithFunc = functionRepository.findByNameContainingIgnoreCase("function");

        // Проверка
        assertEquals(2, functionsWithFunc.size());
        assertTrue(functionsWithFunc.stream().anyMatch(f -> f.getName().equals("linear_function")));
        assertTrue(functionsWithFunc.stream().anyMatch(f -> f.getName().equals("quadratic_function")));
    }

    @Test
    void testCountByUserId() {
        // Генерация данных
        User user = new User("testuser", "password", "test@example.com");
        User savedUser = userRepository.save(user);

        Function func1 = new Function(savedUser.getId(), "f1", "type1");
        Function func2 = new Function(savedUser.getId(), "f2", "type1");
        Function func3 = new Function(savedUser.getId(), "f3", "type2");

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);
        entityManager.flush();

        // Подсчет функций пользователя
        long count = functionRepository.countByUserId(savedUser.getId());

        // Проверка
        assertEquals(3, count);
    }

    @Test
    void testDeleteFunction() {
        // Генерация данных
        Function function = new Function(1L, "function_to_delete", "test");
        Function savedFunction = functionRepository.save(function);
        entityManager.flush();

        // Проверка что функция существует
        assertTrue(functionRepository.findById(savedFunction.getId()).isPresent());

        // Удаление
        functionRepository.deleteById(savedFunction.getId());
        entityManager.flush();

        // Проверка что функция удалена
        assertFalse(functionRepository.findById(savedFunction.getId()).isPresent());
    }
}