package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.login LIKE %:keyword% OR u.role LIKE %:keyword%")
    List<User> findByLoginOrRoleContaining(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    boolean existsByLogin(String login);
}