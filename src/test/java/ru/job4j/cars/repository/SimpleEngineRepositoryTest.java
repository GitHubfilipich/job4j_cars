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
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.engine.EngineRepository;
import ru.job4j.cars.repository.engine.SimpleEngineRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleEngineRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static EngineRepository engineRepository;
    private static List<Engine> engines;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        engineRepository = new SimpleEngineRepository(new CrudRepository(sf));
        engines = new ArrayList<>();
        clearAllEngines();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllEngines();
        engines.clear();
    }

    private static void clearAllEngines() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Engine").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void saveEngines(List<Engine> enginesToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            enginesToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    private List<Engine> createTestEngines() {
        return List.of(
                new Engine(0, "V6"),
                new Engine(0, "V8"),
                new Engine(0, "Electric")
        );
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        engines.addAll(createTestEngines());
        saveEngines(engines);

        var result = engineRepository.findAll();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(engines);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = engineRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        engines.addAll(createTestEngines());
        saveEngines(engines);
        Engine expected = engines.get(1);
        var result = engineRepository.findById(expected.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdWithoutDataThenGetEmpty() {
        var result = engineRepository.findById(1);
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий создания двигателя методом {@code create}
     */
    @Test
    void whenCreateThenEngineSaved() {
        Engine engine = new Engine(0, "Hybrid");
        engineRepository.create(engine);

        var result = engineRepository.findById(engine.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(engine);
    }

    /**
     * Проверяет успешный сценарий удаления двигателя методом {@code delete}
     */
    @Test
    void whenDeleteThenEngineRemoved() {
        Engine engine = new Engine(0, "Diesel");
        saveEngines(List.of(engine));
        int id = engine.getId();

        engineRepository.delete(id);

        var result = engineRepository.findById(id);
        assertThat(result).isEmpty();
    }
}