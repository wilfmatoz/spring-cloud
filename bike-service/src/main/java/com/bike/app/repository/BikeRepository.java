package com.bike.app.repository;

import com.bike.app.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeRepository extends JpaRepository<Bike, Integer> {

    List<Bike> findByUserId(Integer userId);

}
