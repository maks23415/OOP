package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.entity.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByFunction(Function function);

    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId")
    List<Point> findByFunctionId(@Param("functionId") Long functionId);

    @Query("SELECT p FROM Point p WHERE p.function.user.login = :userLogin")
    List<Point> findByUserLogin(@Param("userLogin") String userLogin);

    @Query("SELECT p FROM Point p WHERE p.xValue BETWEEN :minX AND :maxX")
    List<Point> findByXValueBetween(@Param("minX") Double minX, @Param("maxX") Double maxX);

    @Query("SELECT p FROM Point p WHERE p.yValue BETWEEN :minY AND :maxY")
    List<Point> findByYValueBetween(@Param("minY") Double minY, @Param("maxY") Double maxY);

    @Modifying
    @Transactional
    @Query("DELETE FROM Point p WHERE p.function.id = :functionId")
    void deleteByFunctionId(@Param("functionId") Long functionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Point p WHERE p.function IN (SELECT f FROM Function f WHERE f.user.login = :userLogin)")
    void deleteByUserLogin(@Param("userLogin") String userLogin);
}
