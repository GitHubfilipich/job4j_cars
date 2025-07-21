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
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.repository.bodytype.BodyTypeRepository;
import ru.job4j.cars.repository.bodytype.SimpleBodyTypeRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleBodyTypeRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static BodyTypeRepository bodyTypeRepository;
    private static List<BodyType> bodyTypes;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        bodyTypeRepository = new SimpleBodyTypeRepository(new CrudRepository(sf));
        bodyTypes = new ArrayList<>();
        clearAllBodyTypes();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllBodyTypes();
        bodyTypes.clear();
    }

    private static void clearAllBodyTypes() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from BodyType").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void saveBodyTypes(List<BodyType> typesToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            typesToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    private List<BodyType> createTestBodyTypes() {
        return List.of(
                new BodyType(0, "TestType1"),
                new BodyType(0, "TestType2"),
                new BodyType(0, "TestType3")
        );
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        bodyTypes.addAll(createTestBodyTypes());
        saveBodyTypes(bodyTypes);

        var result = bodyTypeRepository.findAll();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(bodyTypes);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = bodyTypeRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        bodyTypes.addAll(createTestBodyTypes());
        saveBodyTypes(bodyTypes);
        BodyType expected = bodyTypes.get(1);
        var result = bodyTypeRepository.findById(expected.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdWithoutDataThenGetEmpty() {
        var result = bodyTypeRepository.findById(1);
        assertThat(result).isEmpty();
    }
}
