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
import ru.job4j.cars.model.Model;
import ru.job4j.cars.repository.model.ModelRepository;
import ru.job4j.cars.repository.model.SimpleModelRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleModelRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static ModelRepository modelRepository;
    private static List<Model> models;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        modelRepository = new SimpleModelRepository(new CrudRepository(sf));
        models = new ArrayList<>();
        clearAllModels();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllModels();
        models.clear();
    }

    private static void clearAllModels() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Model").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void saveModels(List<Model> modelsToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            modelsToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    private List<Model> createTestModels() {
        return List.of(
                new Model(0, "Camry"),
                new Model(0, "Civic"),
                new Model(0, "Focus")
        );
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        models.addAll(createTestModels());
        saveModels(models);

        var result = modelRepository.findAll();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(models);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = modelRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        models.addAll(createTestModels());
        saveModels(models);
        Model expected = models.get(1);
        var result = modelRepository.findById(expected.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdWithoutDataThenGetEmpty() {
        var result = modelRepository.findById(1);
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий создания модели методом {@code create}
     */
    @Test
    void whenCreateThenModelSaved() {
        Model model = new Model(0, "Solaris");
        modelRepository.create(model);

        var result = modelRepository.findById(model.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(model);
    }

    /**
     * Проверяет успешный сценарий удаления модели методом {@code delete}
     */
    @Test
    void whenDeleteThenModelRemoved() {
        Model model = new Model(0, "Octavia");
        saveModels(List.of(model));
        int id = model.getId();

        modelRepository.delete(id);

        var result = modelRepository.findById(id);
        assertThat(result).isEmpty();
    }
}
