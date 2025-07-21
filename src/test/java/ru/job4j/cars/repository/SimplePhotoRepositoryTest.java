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
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.photo.PhotoRepository;
import ru.job4j.cars.repository.photo.SimplePhotoRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePhotoRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static PhotoRepository photoRepository;
    private static List<Photo> photos;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        photoRepository = new SimplePhotoRepository(new CrudRepository(sf));
        photos = new ArrayList<>();
        clearAllPhotos();
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        clearAllPhotos();
        photos.clear();
    }

    private static void clearAllPhotos() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Photo").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private void savePhotos(List<Photo> photosToSave) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            photosToSave.forEach(session::save);
            session.getTransaction().commit();
        }
    }

    private List<Photo> createTestPhotos() {
        return List.of(
                new Photo(0, "file1.jpg", "/path/to/file1.jpg"),
                new Photo(0, "file2.jpg", "/path/to/file2.jpg"),
                new Photo(0, "file3.jpg", "/path/to/file3.jpg")
        );
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        photos.addAll(createTestPhotos());
        savePhotos(photos);

        var result = photoRepository.findAll();

        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(photos);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = photoRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        photos.addAll(createTestPhotos());
        savePhotos(photos);
        Photo expected = photos.get(1);
        var result = photoRepository.findById(expected.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    /**
     * Проверяет неуспешный сценарий возврата данных методом {@code findById}
     */
    @Test
    void whenFindByIdWithoutDataThenGetEmpty() {
        var result = photoRepository.findById(1);
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий создания фото методом {@code create}
     */
    @Test
    void whenCreateThenPhotoSaved() {
        Photo photo = new Photo(0, "file4.jpg", "/path/to/file4.jpg");
        photoRepository.create(photo);

        var result = photoRepository.findById(photo.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(photo);
    }

    /**
     * Проверяет успешный сценарий удаления фото методом {@code delete}
     */
    @Test
    void whenDeleteThenPhotoRemoved() {
        Photo photo = new Photo(0, "file5.jpg", "/path/to/file5.jpg");
        savePhotos(List.of(photo));
        int id = photo.getId();

        boolean deleted = photoRepository.delete(id);

        assertThat(deleted).isTrue();
        var result = photoRepository.findById(id);
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий поиска фото по filePath методом {@code findByFilePath}
     */
    @Test
    void whenFindByFilePathThenGetData() {
        Photo photo = new Photo(0, "file6.jpg", "/path/to/file6.jpg");
        savePhotos(List.of(photo));

        var result = photoRepository.findByFilePath(photo.getFilePath());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(photo);
    }

    /**
     * Проверяет неуспешный сценарий поиска фото по filePath методом {@code findByFilePath}
     */
    @Test
    void whenFindByFilePathWithoutDataThenGetEmpty() {
        var result = photoRepository.findByFilePath("/not/exist/path.jpg");
        assertThat(result).isEmpty();
    }
}
