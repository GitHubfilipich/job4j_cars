package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;

import java.util.List;
import java.util.Set;

public class CarUsage {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try (SessionFactory sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory()) {
            var engineRepository = new EngineRepository(new CrudRepository(sf));
            var ownerRepository = new OwnerRepository(new CrudRepository(sf));
            var carRepository = new CarRepository(new CrudRepository(sf));

            System.out.println("!!! очистка таблиц !!!");
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
            var car = new Car(0, "Car1", engine, owners.get(0), Set.copyOf(owners));
            carRepository.create(car);

            System.out.println(car);

        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
