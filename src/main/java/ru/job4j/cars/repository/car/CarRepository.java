package ru.job4j.cars.repository.car;

import ru.job4j.cars.model.Car;

import java.util.List;

public interface CarRepository {
    Car create(Car car);

    List<Car> findAll();

    void delete(int id);
}
