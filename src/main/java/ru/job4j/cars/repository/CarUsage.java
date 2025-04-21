package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.Ownership;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

public class CarUsage {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try (SessionFactory sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory()) {
            var engineRepository = new EngineRepository(new CrudRepository(sf));
            var ownerRepository = new OwnerRepository(new CrudRepository(sf));
            var carRepository = new CarRepository(new CrudRepository(sf));
            var ownershipRepository = new OwnershipRepository(new CrudRepository(sf));

            System.out.println("!!! очистка таблиц !!!");
            carRepository.findAll().forEach(car -> car.getOwnerships().clear());
            ownershipRepository.findAll().forEach(ownership -> ownershipRepository.delete(ownership.getId()));
            carRepository.findAll().forEach(car -> carRepository.delete(car.getId()));
            ownerRepository.findAll().forEach(owner -> ownerRepository.delete(owner.getId()));
            engineRepository.findAll().forEach(engine -> engineRepository.delete(engine.getId()));

            System.out.println("!!! заполнение Engine !!!");
            var engine = new Engine(0, "Engine1");

            System.out.println("!!! заполнение Owner !!!");
            var owners = List.of(new Owner(0, "Owner1"),
                    new Owner(0, "Owner2"),
                    new Owner(0, "Owner3"));

            System.out.println("!!! заполнение Car !!!");

            var car = new Car(0, "Car1", engine, owners.get(0), new HashSet<>());
            car.getOwnerships().add(new Ownership(0, owners.get(0), car, LocalDateTime.of(2024, 1, 1, 10, 0),
                    LocalDateTime.of(2024, 6, 1, 10, 0)));
            car.getOwnerships().add(new Ownership(0, owners.get(1), car, LocalDateTime.of(2024, 6, 1, 10, 0),
                    LocalDateTime.of(2025, 1, 1, 10, 0)));
            car.getOwnerships().add(new Ownership(0, owners.get(2), car, LocalDateTime.of(2025, 1, 1, 10, 0),
                    null));
            carRepository.create(car);

            System.out.println(car);

        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
