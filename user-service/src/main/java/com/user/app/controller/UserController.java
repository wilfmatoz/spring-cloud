package com.user.app.controller;

import com.user.app.entity.User;
import com.user.app.model.Bike;
import com.user.app.model.Car;
import com.user.app.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") Integer id) {
        User user = userService.getUserById(id);
        if (ObjectUtils.isEmpty(user)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user) {
        User userNew = userService.save(user);
        return ResponseEntity.ok(userNew);
    }

    @CircuitBreaker(name = "carsCB", fallbackMethod = "fallBackGetCars")
    @GetMapping("/cars/{userId}")
    public ResponseEntity<List<Car>> getCars(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        if (ObjectUtils.isEmpty(user)) {
            return ResponseEntity.notFound().build();
        }
        List<Car> cars = userService.getCars(userId);
        return ResponseEntity.ok(cars);
    }

    @CircuitBreaker(name = "carsCB", fallbackMethod = "fallBackSaveCar")
    @PostMapping("/savecar/{userId}")
    public ResponseEntity<Car> saveCar(@PathVariable("userId") Integer userId, @RequestBody Car car) {
        User user = userService.getUserById(userId);
        if (ObjectUtils.isEmpty(user)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.saveCar(userId, car));
    }

    @CircuitBreaker(name = "bikesCB", fallbackMethod = "fallBackGetBikes")
    @GetMapping("/bikes/{userId}")
    public ResponseEntity<List<Bike>> getBikes(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        if (ObjectUtils.isEmpty(user)) {
            return ResponseEntity.notFound().build();
        }
        List<Bike> bikes = userService.getBikes(userId);
        return ResponseEntity.ok(bikes);
    }

    @CircuitBreaker(name = "bikesCB", fallbackMethod = "fallBackSaveBike")
    @PostMapping("/savebike/{userId}")
    public ResponseEntity<Bike> saveBike(@PathVariable("userId") Integer userId, @RequestBody Bike bike) {
        User user = userService.getUserById(userId);
        if (ObjectUtils.isEmpty(user)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.saveBike(userId, bike));
    }

    @CircuitBreaker(name = "allCB", fallbackMethod = "fallBackGetAll")
    @GetMapping("/getAll/{userId}")
    public ResponseEntity<Map<String, Object>> getAllVehicles(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserAndVehicles(userId));
    }

    private ResponseEntity<Car> fallBackGetCars(@PathVariable Integer userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tienes los coches en el taller", HttpStatus.OK);
    }

    private ResponseEntity<Car> fallBackSaveCar(@PathVariable Integer userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " no tuene dinero para coches", HttpStatus.OK);
    }

    private ResponseEntity<Bike> fallBackGetBikes(@PathVariable Integer userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tienes los motos en el taller", HttpStatus.OK);
    }

    private ResponseEntity<Bike> fallBackSaveBike(@PathVariable Integer userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " no tuene dinero para motos", HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> fallBackGetAll(@PathVariable Integer userId, RuntimeException e) {
        return new ResponseEntity("El usuario " + userId + " tienes los vehiculos en el taller", HttpStatus.OK);
    }

}
