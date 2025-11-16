package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:framework/application.properties")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        // Генерация данных
        User user = new User("testuser", "password123", "test@example.com");

        // Сохранение
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // Поиск
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Проверка
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getLogin());
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    void testFindByLogin() {
        // Генерация данных
        User user1 = new User("john_doe", "pass123", "john@example.com");
        User user2 = new User("jane_smith", "pass456", "jane@example.com");

        userRepository.save(user1);
        userRepository.save(user2);
        entityManager.flush();
        entityManager.clear();

        // Поиск по логину
        Optional<User> found = userRepository.findByLogin("john_doe");

        // Проверка
        assertTrue(found.isPresent());
        assertEquals("john@example.com", found.get().getEmail());
    }

    @Test
    void testExistsByLogin() {
        // Генерация данных
        User user = new User("existing_user", "password", "existing@example.com");
        userRepository.save(user);
        entityManager.flush();

        // Проверка существования
        boolean exists = userRepository.existsByLogin("existing_user");
        boolean notExists = userRepository.existsByLogin("nonexistent_user");

        // Проверка
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByLoginContaining() {
        // Генерация разнообразных данных
        User user1 = new User("alice_wonder", "pass1", "alice@example.com");
        User user2 = new User("bob_builder", "pass2", "bob@example.com");
        User user3 = new User("charlie_brown", "pass3", "charlie@example.com");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        entityManager.flush();
        entityManager.clear();

        // Поиск по части логина
        List<User> usersWithB = userRepository.findByLoginContainingIgnoreCase("b");

        // Проверка
        assertEquals(2, usersWithB.size());
        assertTrue(usersWithB.stream().anyMatch(u -> u.getLogin().equals("bob_builder")));
        assertTrue(usersWithB.stream().anyMatch(u -> u.getLogin().equals("charlie_brown")));
    }

    @Test
    void testDeleteUser() {
        // Генерация данных
        User user = new User("user_to_delete", "password", "delete@example.com");
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Проверка что пользователь существует
        assertTrue(userRepository.existsByLogin("user_to_delete"));

        // Удаление
        userRepository.deleteById(savedUser.getId());
        entityManager.flush();

        // Проверка что пользователь удален
        assertFalse(userRepository.existsByLogin("user_to_delete"));
        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
    }

    @Test
    void testFindAllByOrderByCreatedAt() {
        // Генерация данных с разными датами
        User user1 = new User("user1", "pass1", "user1@example.com");
        User user2 = new User("user2", "pass2", "user2@example.com");
        User user3 = new User("user3", "pass3", "user3@example.com");

        userRepository.save(user1);
        entityManager.flush();
        try { Thread.sleep(10); } catch (InterruptedException e) {} // Задержка для разных временных меток

        userRepository.save(user2);
        entityManager.flush();
        try { Thread.sleep(10); } catch (InterruptedException e) {}

        userRepository.save(user3);
        entityManager.flush();
        entityManager.clear();

        // Проверка сортировки
        List<User> usersDesc = userRepository.findAllByOrderByCreatedAtDesc();
        assertEquals(3, usersDesc.size());
        assertEquals("user3", usersDesc.get(0).getLogin()); // Последний созданный должен быть первым
    }
}