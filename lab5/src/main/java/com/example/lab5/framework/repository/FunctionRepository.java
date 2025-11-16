package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {

    // Поиск по ID пользователя
    List<Function> findByUserId(Long userId);

    // Поиск по типу функции
    List<Function> findByType(String type);

    // Поиск по имени (регистронезависимый)
    List<Function> findByNameContainingIgnoreCase(String name);

    // Поиск по типу и пользователю
    List<Function> findByUserIdAndType(Long userId, String type);

    // Подсчет функций пользователя
    long countByUserId(Long userId);

    // Поиск функций созданных после указанной даты
    List<Function> findByCreatedAtAfter(java.time.LocalDateTime date);

    // Нативный запрос для поиска функций с количеством точек
    @Query("SELECT f FROM Function f WHERE SIZE(f.points) > :minPointsCount")
    List<Function> findFunctionsWithMinimumPoints(@Param("minPointsCount") int minPointsCount);

    // Сортировка по различным полям
    List<Function> findAllByOrderByNameAsc();
    List<Function> findAllByOrderByCreatedAtDesc();
}