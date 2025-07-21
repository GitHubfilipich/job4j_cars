package ru.job4j.cars.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.car.CarRepository;
import ru.job4j.cars.repository.car.SimpleCarRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleCarRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static CarRepository carRepository;
    private static List<Car> cars;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        carRepository = new SimpleCarRepository(new CrudRepository(sf));
        cars = new ArrayList<>();
        clearAllCarsAndRelated();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllCarsAndRelated();
        cars.clear();
    }

    private static void clearAllCarsAndRelated() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Ownership").executeUpdate();
            session.createQuery("delete from Car").executeUpdate();
            session.createQuery("delete from Engine").executeUpdate();
            session.createQuery("delete from Owner").executeUpdate();
            session.createQuery("delete from Model").executeUpdate();
            session.createQuery("delete from BodyType").executeUpdate();
            session.createQuery("delete from Brand").executeUpdate();
            session.createQuery("delete from Gearbox").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private Engine saveEngine(String name) {
        Engine engine = new Engine(0, name);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(engine);
            session.getTransaction().commit();
        }
        return engine;
    }

    private Owner saveOwner(String name) {
        Owner owner = new Owner(0, name);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(owner);
            session.getTransaction().commit();
        }
        return owner;
    }

    private Model saveModel(String name) {
        Model model = new Model(0, name);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(model);
            session.getTransaction().commit();
        }
        return model;
    }

    private BodyType saveBodyType(String name) {
        BodyType bodyType = new BodyType(0, name);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(bodyType);
            session.getTransaction().commit();
        }
        return bodyType;
    }

    private Brand saveBrand(String name) {
        Brand brand = new Brand(0, name);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(brand);
            session.getTransaction().commit();
        }
        return brand;
    }

    private Gearbox saveGearbox(String name) {
        Gearbox gearbox = new Gearbox(0, name);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(gearbox);
            session.getTransaction().commit();
        }
        return gearbox;
    }

    private List<Car> createTestCars() {
        Engine engine1 = saveEngine("Engine1");
        Engine engine2 = saveEngine("Engine2");
        Engine engine3 = saveEngine("Engine3");

        Owner owner1 = saveOwner("Owner1");
        Owner owner2 = saveOwner("Owner2");
        Owner owner3 = saveOwner("Owner3");

        Model model1 = saveModel("Camry");
        Model model2 = saveModel("2101");
        Model model3 = saveModel("Solaris");

        BodyType bodyType1 = saveBodyType("Sedan");
        BodyType bodyType2 = saveBodyType("Универсал");
        BodyType bodyType3 = saveBodyType("Седан");

        Brand brand1 = saveBrand("Toyota");
        Brand brand2 = saveBrand("Жигули");
        Brand brand3 = saveBrand("Hyundai");

        Gearbox gearbox1 = saveGearbox("Automatic");
        Gearbox gearbox2 = saveGearbox("Ручка");
        Gearbox gearbox3 = saveGearbox("Автомат");

        return List.of(
                new Car(0, "Toyota Camry", engine1, owner1, new HashSet<>(),
                        model1, bodyType1, brand1, gearbox1, 2020, 15000, 100, false),
                new Car(0, "Жигули 2102", engine2, owner2, new HashSet<>(),
                        model2, bodyType2, brand2, gearbox2, 1990, 150000, 60, true),
                new Car(0, "Solaris", engine3, owner3, new HashSet<>(),
                        model3, bodyType3, brand3, gearbox3, 2022, 10000, 120, false)
        );
    }

    private void saveCars(List<Car> carsToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            carsToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        cars.addAll(createTestCars());
        saveCars(cars);

        var result = carRepository.findAll();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(cars);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = carRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий создания автомобиля методом {@code create}
     */
    @Test
    void whenCreateThenCarSaved() {
        Engine engine = saveEngine("Engine4");
        Owner owner = saveOwner("Owner4");
        Model model = saveModel("Octavia");
        BodyType bodyType = saveBodyType("Лифтбек");
        Brand brand = saveBrand("Skoda");
        Gearbox gearbox = saveGearbox("Механика");

        Car car = new Car(0, "Octavia", engine, owner, new HashSet<>(),
                model, bodyType, brand, gearbox, 2018, 50000, 150, true);

        carRepository.create(car);

        var result = carRepository.findAll();
        assertThat(result).usingRecursiveFieldByFieldElementComparator().contains(car);
    }

    /**
     * Проверяет успешный сценарий удаления автомобиля методом {@code delete}
     */
    @Test
    void whenDeleteThenCarRemoved() {
        Engine engine = saveEngine("Engine5");
        Owner owner = saveOwner("Owner5");
        Model model = saveModel("Polo");
        BodyType bodyType = saveBodyType("Седан");
        Brand brand = saveBrand("Volkswagen");
        Gearbox gearbox = saveGearbox("Автомат");

        Car car = new Car(0, "Polo", engine, owner, new HashSet<>(),
                model, bodyType, brand, gearbox, 2019, 30000, 110, false);
        saveCars(List.of(car));
        int id = car.getId();

        carRepository.delete(id);

        var result = carRepository.findAll();
        assertThat(result).doesNotContain(car);
    }
}
