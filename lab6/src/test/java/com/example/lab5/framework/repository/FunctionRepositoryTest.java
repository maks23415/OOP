package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Function;
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

class FunctionRepositoryTest {

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        functionRepository.deleteAll();
        userRepository.deleteAll();

        // Генерация тестовых данных
        generateTestData();
    }

    private void generateTestData() {
        // Создаем пользователей
        testUser1 = new User("math_user", "mathematician", "math123");
        testUser2 = new User("physics_user", "physicist", "physics123");

        userRepository.save(testUser1);
        userRepository.save(testUser2);

        // Создаем функции для пользователей
        Function func1 = new Function("linear_function", "y = x + 1", testUser1);
        Function func2 = new Function("quadratic_function", "y = x^2", testUser1);
        Function func3 = new Function("sin_function", "y = sin(x)", testUser2);
        Function func4 = new Function("cos_function", "y = cos(x)", testUser2);

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);
        functionRepository.save(func4);
    }

    @Test
    void whenFindByUser_thenReturnFunctions() {
        // Поиск функций по пользователю
        List<Function> user1Functions = functionRepository.findByUser(testUser1);

        assertThat(user1Functions).hasSize(2);
        assertThat(user1Functions)
                .extracting(Function::getName)
                .containsExactlyInAnyOrder("linear_function", "quadratic_function");
    }

    @Test
    void whenFindByUserLogin_thenReturnFunctions() {
        // Поиск функций по логину пользователя
        List<Function> mathFunctions = functionRepository.findByUserLogin("math_user");

        assertThat(mathFunctions).hasSize(2);
        assertThat(mathFunctions)
                .extracting(Function::getSignature)
                .contains("y = x + 1", "y = x^2");
    }

    @Test
    void whenFindByNameContaining_thenReturnMatchingFunctions() {
        // Поиск функций по части имени
        List<Function> functionResults = functionRepository.findByNameContaining("function");

        assertThat(functionResults).hasSize(4); // Все функции содержат "function"
    }

    @Test
    void whenFindBySignatureContaining_thenReturnMatchingFunctions() {
        // Поиск функций по части сигнатуры
        List<Function> sinFunctions = functionRepository.findBySignatureContaining("sin");
        List<Function> xFunctions = functionRepository.findBySignatureContaining("x");

        assertThat(sinFunctions).hasSize(1);
        assertThat(xFunctions).hasSize(4); // Все функции содержат "x"
    }

    @Test
    void whenFindByNameOrSignatureContaining_thenReturnMatchingFunctions() {
        // Комплексный поиск по имени или сигнатуре
        List<Function> linearResults = functionRepository.findByNameOrSignatureContaining("linear");
        List<Function> cosResults = functionRepository.findByNameOrSignatureContaining("cos");

        assertThat(linearResults).hasSize(1);
        assertThat(cosResults).hasSize(1);
    }

    @Test
    void whenCreateNewFunction_thenFunctionShouldBePersisted() {
        // Создание новой функции
        Function newFunction = new Function("exponential", "y = e^x", testUser1);
        Function saved = functionRepository.save(newFunction);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("exponential");
        assertThat(saved.getUser().getLogin()).isEqualTo("math_user");
    }

    @Test
    void whenUpdateFunction_thenChangesShouldBePersisted() {
        // Обновление функции
        List<Function> functions = functionRepository.findByNameContaining("linear_function");
        assertThat(functions).hasSize(1);

        Function function = functions.get(0);
        function.setName("updated_linear_function");
        function.setSignature("y = ax + b");

        Function updated = functionRepository.save(function);

        assertThat(updated.getName()).isEqualTo("updated_linear_function");
        assertThat(updated.getSignature()).isEqualTo("y = ax + b");
    }

    @Test
    void whenDeleteFunction_thenFunctionShouldBeRemoved() {
        // Удаление функции
        List<Function> functions = functionRepository.findByNameContaining("sin_function");
        assertThat(functions).hasSize(1);

        functionRepository.delete(functions.get(0));

        // Проверяем, что функция удалена
        List<Function> deleted = functionRepository.findByNameContaining("sin_function");
        assertThat(deleted).isEmpty();

        // Проверяем, что остальные функции остались
        assertThat(functionRepository.count()).isEqualTo(3);
    }

    @Test
    void whenFindAllFunctions_thenReturnAllFunctions() {
        // Получение всех функций
        List<Function> allFunctions = functionRepository.findAll();

        assertThat(allFunctions).hasSize(4);
        assertThat(allFunctions)
                .extracting(Function::getName)
                .containsExactlyInAnyOrder(
                        "linear_function", "quadratic_function",
                        "sin_function", "cos_function"
                );
    }
}
