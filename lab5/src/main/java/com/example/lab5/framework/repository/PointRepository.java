package com.example.lab5.framework.repository;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByFunction(Function function);

    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId")
    List<Point> findByFunctionId(@Param("functionId") Long functionId);

    @Query("SELECT p FROM Point p WHERE p.function.user.login = :userLogin")
    List<Point> findByUserLogin(@Param("userLogin") String userLogin);

    List<Point> findByXValueBetween(Double minX, Double maxX);
    List<Point> findByYValueBetween(Double minY, Double maxY);
}