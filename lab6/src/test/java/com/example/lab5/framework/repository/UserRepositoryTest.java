package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")

class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        userRepository.deleteAll();

        // Генерация тестовых данных
        generateTestUsers();
    }

    private void generateTestUsers() {
        // Создаем разнообразных пользователей для тестов
        User admin = new User("admin_john", "administrator", "admin123");
        User developer = new User("dev_alice", "developer", "dev123");
        User analyst = new User("analyst_bob", "analyst", "analyst123");
        User manager = new User("manager_carol", "manager", "manager123");

        userRepository.save(admin);
        userRepository.save(developer);
        userRepository.save(analyst);
        userRepository.save(manager);
    }

    @Test
    void whenFindByLogin_thenReturnUser() {
        // Поиск по логину
        Optional<User> found = userRepository.findByLogin("admin_john");

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo("administrator");
        assertThat(found.get().getPassword()).isEqualTo("admin123");
    }

    @Test
    void whenFindByRole_thenReturnUsers() {
        // Поиск по роли
        List<User> developers = userRepository.findByRole("developer");

        assertThat(developers).hasSize(1);
        assertThat(developers.get(0).getLogin()).isEqualTo("dev_alice");
    }

    @Test
    void whenSearchByKeyword_thenReturnMatchingUsers() {
        // Поиск по ключевому слову в логине или роли
        List<User> adminResults = userRepository.findByLoginOrRoleContaining("admin");
        List<User> userResults = userRepository.findByLoginOrRoleContaining("user");

        assertThat(adminResults).hasSize(1);
        assertThat(userResults).hasSize(0); // Никто не содержит "user"
    }

    @Test
    void whenCheckExistsByLogin_thenReturnBoolean() {
        // Проверка существования пользователя
        boolean exists = userRepository.existsByLogin("dev_alice");
        boolean notExists = userRepository.existsByLogin("nonexistent_user");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void whenCreateNewUser_thenUserShouldBePersisted() {
        // Создание нового пользователя
        User newUser = new User("new_tester", "tester", "test123");

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLogin()).isEqualTo("new_tester");

        // Проверяем, что пользователь действительно сохранен
        Optional<User> found = userRepository.findByLogin("new_tester");
        assertThat(found).isPresent();
    }

    @Test
    void whenUpdateUser_thenChangesShouldBePersisted() {
        // Обновление пользователя
        Optional<User> userOpt = userRepository.findByLogin("analyst_bob");
        assertThat(userOpt).isPresent();

        User user = userOpt.get();
        user.setRole("senior_analyst");
        user.setPassword("new_password");

        User updated = userRepository.save(user);

        assertThat(updated.getRole()).isEqualTo("senior_analyst");
        assertThat(updated.getPassword()).isEqualTo("new_password");
    }

    @Test
    void whenDeleteUser_thenUserShouldBeRemoved() {
        // Удаление пользователя
        Optional<User> userOpt = userRepository.findByLogin("manager_carol");
        assertThat(userOpt).isPresent();

        userRepository.delete(userOpt.get());

        // Проверяем, что пользователь удален
        Optional<User> deleted = userRepository.findByLogin("manager_carol");
        assertThat(deleted).isNotPresent();

        // Проверяем, что остальные пользователи остались
        assertThat(userRepository.count()).isEqualTo(3);
    }

    @Test
    void whenFindAllUsers_thenReturnAllUsers() {
        // Получение всех пользователей
        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSize(4);
        assertThat(allUsers)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder("admin_john", "dev_alice", "analyst_bob", "manager_carol");
    }
}
