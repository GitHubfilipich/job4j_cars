package ru.job4j.cars.repository.car;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class SimpleCarRepository implements CarRepository {
    private final CrudRepository crudRepository;

    @Override
    public Car create(Car car) {
        crudRepository.run(session -> session.persist(car));
        return car;
    }

    @Override
    public List<Car> findAll() {
        return crudRepository.query("SELECT DISTINCT c FROM Car c LEFT JOIN FETCH c.ownerships", Car.class);
    }

    @Override
    public void delete(int id) {
        crudRepository.run(
                "delete from Car where id = :id",
                Map.of("id", id)
        );
    }
}
