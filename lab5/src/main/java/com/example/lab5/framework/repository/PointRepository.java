package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    // Поиск по ID функции
    List<Point> findByFuncId(Long funcId);

    // Поиск по диапазону X
    List<Point> findByXValueBetween(Double minX, Double maxX);

    // Поиск по диапазону Y
    List<Point> findByYValueBetween(Double minY, Double maxY);

    // Поиск по точному значению X
    List<Point> findByXValue(Double xValue);

    // Поиск по точному значению Y
    List<Point> findByYValue(Double yValue);

    // Поиск точек с X больше указанного
    List<Point> findByXValueGreaterThan(Double xValue);

    // Поиск точек с Y меньше указанного
    List<Point> findByYValueLessThan(Double yValue);

    // Подсчет точек функции
    long countByFuncId(Long funcId);

    // Нативный запрос для поиска точек в прямоугольной области
    @Query("SELECT p FROM Point p WHERE p.xValue BETWEEN :minX AND :maxX AND p.yValue BETWEEN :minY AND :maxY")
    List<Point> findPointsInArea(@Param("minX") Double minX, @Param("maxX") Double maxX,
                                 @Param("minY") Double minY, @Param("maxY") Double maxY);

    // Сортировка по X
    List<Point> findByFuncIdOrderByXValueAsc(Long funcId);
    List<Point> findByFuncIdOrderByXValueDesc(Long funcId);

    // Сортировка по Y
    List<Point> findByFuncIdOrderByYValueAsc(Long funcId);
    List<Point> findByFuncIdOrderByYValueDesc(Long funcId);
}