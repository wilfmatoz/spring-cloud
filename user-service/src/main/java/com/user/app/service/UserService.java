package com.user.app.service;

import com.user.app.entity.User;
import com.user.app.feignclients.BikeFeignClient;
import com.user.app.feignclients.CarFeignClient;
import com.user.app.model.Bike;
import com.user.app.model.Car;
import com.user.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final CarFeignClient carFeignClient;
    private final BikeFeignClient bikeFeignClient;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<Car> getCars(Integer userId) {
        return restTemplate.getForObject("http://car-service/car/byuser/" + userId, List.class);
    }

    public List<Bike> getBikes(Integer userId) {
        return restTemplate.getForObject("http://bike-service/bike/byuser/" + userId, List.class);
    }

    public Car saveCar(Integer userId, Car car) {
        car.setUserId(userId);
        return carFeignClient.save(car);
    }

    public Bike saveBike(Integer userId, Bike bike) {
        bike.setUserId(userId);
        return bikeFeignClient.save(bike);
    }

    public Map<String, Object> getUserAndVehicles(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            result.put("Mensaje", "no existe el usuario");
            return result;
        }
        result.put("User", user);
        List<Car> cars = carFeignClient.getCars(userId);
        if (cars.isEmpty()) {
            result.put("Mensaje", "ese user no tiene coches");
        } else {
            result.put("Cars", cars);
        }
        List<Bike> bikes = bikeFeignClient.getBikes(userId);
        if (bikes.isEmpty()) {
            result.put("Mensaje", "ese user no tiene motos");
        } else {
            result.put("Bikes", bikes);
        }
        return result;
    }

}
