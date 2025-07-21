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
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.brand.BrandRepository;
import ru.job4j.cars.repository.brand.SimpleBrandRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleBrandRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static BrandRepository brandRepository;
    private static List<Brand> brands;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        brandRepository = new SimpleBrandRepository(new CrudRepository(sf));
        brands = new ArrayList<>();
        clearAllBrands();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllBrands();
        brands.clear();
    }

    private static void clearAllBrands() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Brand").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void saveBrands(List<Brand> brandsToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            brandsToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    private List<Brand> createTestBrands() {
        return List.of(
                new Brand(0, "Toyota"),
                new Brand(0, "BMW"),
                new Brand(0, "Audi")
        );
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        brands.addAll(createTestBrands());
        saveBrands(brands);

        var result = brandRepository.findAll();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(brands);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = brandRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        brands.addAll(createTestBrands());
        saveBrands(brands);
        Brand expected = brands.get(1);
        var result = brandRepository.findById(expected.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdWithoutDataThenGetEmpty() {
        var result = brandRepository.findById(1);
        assertThat(result).isEmpty();
    }
}
