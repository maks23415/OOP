package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск по логину
    Optional<User> findByLogin(String login);

    // Поиск по email
    Optional<User> findByEmail(String email);

    // Проверка существования
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);

    // Поиск по части логина
    List<User> findByLoginContainingIgnoreCase(String loginPart);

    // Поиск по части email
    List<User> findByEmailContainingIgnoreCase(String emailPart);

    // Сортировка по дате создания
    List<User> findAllByOrderByCreatedAtDesc();
    List<User> findAllByOrderByCreatedAtAsc();

    // Поиск пользователей созданных после указанной даты
    List<User> findByCreatedAtAfter(java.time.LocalDateTime date);

    // Поиск по нескольким логинам
    List<User> findByLoginIn(List<String> logins);
}