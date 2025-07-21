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
import ru.job4j.cars.model.Gearbox;
import ru.job4j.cars.repository.gearbox.GearboxRepository;
import ru.job4j.cars.repository.gearbox.SimpleGearboxRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleGearboxRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static GearboxRepository gearboxRepository;
    private static List<Gearbox> gearboxes;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        gearboxRepository = new SimpleGearboxRepository(new CrudRepository(sf));
        gearboxes = new ArrayList<>();
        clearAllGearboxes();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllGearboxes();
        gearboxes.clear();
    }

    private static void clearAllGearboxes() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Gearbox").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void saveGearboxes(List<Gearbox> gearboxesToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            gearboxesToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    private List<Gearbox> createTestGearboxes() {
        return List.of(
                new Gearbox(0, "Manual"),
                new Gearbox(0, "Automatic"),
                new Gearbox(0, "CVT")
        );
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        gearboxes.addAll(createTestGearboxes());
        saveGearboxes(gearboxes);

        var result = gearboxRepository.findAll();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(gearboxes);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = gearboxRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        gearboxes.addAll(createTestGearboxes());
        saveGearboxes(gearboxes);
        Gearbox expected = gearboxes.get(1);
        var result = gearboxRepository.findById(expected.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdWithoutDataThenGetEmpty() {
        var result = gearboxRepository.findById(1);
        assertThat(result).isEmpty();
    }
}
