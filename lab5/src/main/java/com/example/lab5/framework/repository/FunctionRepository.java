package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {
    List<Function> findByUser(User user);
    List<Function> findByNameContaining(String name);
    List<Function> findBySignatureContaining(String signature);

    @Query("SELECT f FROM Function f WHERE f.user.login = :login")
    List<Function> findByUserLogin(@Param("login") String login);

    @Query("SELECT f FROM Function f WHERE f.name LIKE %:keyword% OR f.signature LIKE %:keyword%")
    List<Function> findByNameOrSignatureContaining(@Param("keyword") String keyword);
}