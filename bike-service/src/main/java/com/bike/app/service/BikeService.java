package com.bike.app.service;

import com.bike.app.entity.Bike;
import com.bike.app.repository.BikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeService {

    private final BikeRepository bikeRepository;

    public List<Bike> getAll() {
        return bikeRepository.findAll();
    }

    public Bike getUserById(Integer id) {
        return bikeRepository.findById(id).orElse(null);
    }

    public Bike save(Bike car) {
        return bikeRepository.save(car);
    }

    public List<Bike> byUserId(Integer id) {
        return bikeRepository.findByUserId(id);
    }

}
