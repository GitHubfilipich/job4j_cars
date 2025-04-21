package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CarRepository {
    private final CrudRepository crudRepository;

    public Car create(Car car) {
        crudRepository.run(session -> session.persist(car));
        return car;
    }

    public List<Car> findAll() {
        return crudRepository.query("SELECT DISTINCT c FROM Car c LEFT JOIN FETCH c.ownerships", Car.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Car where id = :id",
                Map.of("id", id)
        );
    }
}
