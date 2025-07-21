package ru.job4j.cars.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.repository.post.SimplePostRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePostRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static PostRepository postRepository;

    @BeforeAll
    static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        postRepository = new SimplePostRepository(new CrudRepository(sf));
        clearAll();
    }

    @AfterAll
    static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    void clearData() {
        clearAll();
    }

    private static void clearAll() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from PriceHistory").executeUpdate();
            session.createQuery("delete from Post").executeUpdate();
            session.createQuery("delete from Car").executeUpdate();
            session.createQuery("delete from Engine").executeUpdate();
            session.createQuery("delete from Owner").executeUpdate();
            session.createQuery("delete from Model").executeUpdate();
            session.createQuery("delete from BodyType").executeUpdate();
            session.createQuery("delete from Brand").executeUpdate();
            session.createQuery("delete from Gearbox").executeUpdate();
            session.createQuery("delete from Photo").executeUpdate();
            session.createQuery("delete from PriceHistory").executeUpdate();
            session.createQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private User saveUser(String login) {
        User user = new User(0, login, "pass", "name");
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        }
        return user;
    }

    private Car saveCar(String name) {
        Engine engine = saveEngine("Engine_" + name);
        Owner owner = saveOwner("Owner_" + name);
        Model model = saveModel("Model_" + name);
        BodyType bodyType = saveBodyType("Body_" + name);
        Brand brand = saveBrand("Brand_" + name);
        Gearbox gearbox = saveGearbox("Gearbox_" + name);
        Car car = new Car(0, name, engine, owner, new HashSet<>(), model, bodyType, brand, gearbox, 2020, 10000, 100, false);
        return car;
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

    private Photo savePhoto(String name, String path) {
        Photo photo = new Photo(0, name, path);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(photo);
            session.getTransaction().commit();
        }
        return photo;
    }

    private Post createFullPost(String desc, boolean actual) {
        User user = saveUser("user_" + desc);
        Car car = saveCar("car_" + desc);
        Photo photo = savePhoto("photo_" + desc, "/path/" + desc + ".jpg");
        Set<User> participates = new HashSet<>();
        Set<Photo> photos = new HashSet<>();
        photos.add(photo);
        List<PriceHistory> priceHistory = new ArrayList<>();
        return new Post(0, desc, LocalDateTime.now(), user, priceHistory, participates, car, photos, 1200, actual);
    }

    /**
     * Проверяет успешный сценарий создания поста методом {@code create}
     */
    @Test
    void whenCreateThenPostSaved() {
        Post post1 = createFullPost("desc_create", true);

        postRepository.create(post1);

        var result = postRepository.findAll();
        assertThat(result).hasSize(1);
        var resultPost = result.get(0);
        assertThat(resultPost.getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(post1.getCar());
        assertThat(resultPost.getPhotos()).usingRecursiveComparison()
                .isEqualTo(post1.getPhotos());
        assertThat(resultPost.getUser()).usingRecursiveComparison()
                .isEqualTo(post1.getUser());
    }

    /**
     * Проверяет успешный сценарий получения всех постов методом {@code findAll}
     */
    @Test
    void whenFindAllThenGetData() {
        Post post1 = createFullPost("desc1", true);
        Post post2 = createFullPost("desc2", false);
        postRepository.create(post1);
        postRepository.create(post2);

        var result = postRepository.findAll();

        result.sort(Comparator.comparing(Post::getDescription));

        assertThat(result).hasSize(2);

        var resultPost1 = result.get(0);
        assertThat(resultPost1.getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(post1.getCar());
        assertThat(resultPost1.getPhotos()).usingRecursiveComparison()
                .isEqualTo(post1.getPhotos());
        assertThat(resultPost1.getUser()).usingRecursiveComparison()
                .isEqualTo(post1.getUser());

        var resultPost2 = result.get(1);
        assertThat(resultPost2.getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(post2.getCar());
        assertThat(resultPost2.getPhotos()).usingRecursiveComparison()
                .isEqualTo(post2.getPhotos());
        assertThat(resultPost2.getUser()).usingRecursiveComparison()
                .isEqualTo(post2.getUser());
    }

    /**
     * Проверяет неуспешный сценарий получения всех постов методом {@code findAll}
     */
    @Test
    void whenFindAllWithoutDataThenGetEmpty() {
        var result = postRepository.findAll();
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий удаления поста методом {@code delete}
     */
    @Test
    void whenDeleteThenPostRemoved() {
        Post post = createFullPost("desc_delete", true);
        postRepository.create(post);
        int id = post.getId();

        boolean deleted = postRepository.delete(id);

        assertThat(deleted).isTrue();
        var result = postRepository.findById(id);
        assertThat(result).isEmpty();
    }

    /**
     * Проверяет успешный сценарий получения постов за последние 24 часа методом {@code findLastDayPosts}
     */
    @Test
    void whenFindLastDayPostsThenGetData() {
        Post post1 = createFullPost("desc_lastday1", true);
        Post post2 = createFullPost("desc_lastday2", false);
        post1.setCreated(LocalDateTime.now().minusHours(12));
        post2.setCreated(LocalDateTime.now().minusDays(2));
        postRepository.create(post1);
        postRepository.create(post2);

        var result = postRepository.findLastDayPosts();

        assertThat(result).hasSize(1);
        var resultPost = result.get(0);
        assertThat(resultPost.getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(post1.getCar());
        assertThat(resultPost.getPhotos()).usingRecursiveComparison()
                .isEqualTo(post1.getPhotos());
        assertThat(resultPost.getUser()).usingRecursiveComparison()
                .isEqualTo(post1.getUser());
    }

    /**
     * Проверяет успешный сценарий получения постов с фото методом {@code findPostsWithPhoto}
     */
    @Test
    void whenFindPostsWithPhotoThenGetData() {
        Post post1 = createFullPost("desc_photo1", true);
        Post post2 = createFullPost("desc_photo2", false);
        post2.getPhotos().clear();
        postRepository.create(post1);
        postRepository.create(post2);

        var result = postRepository.findPostsWithPhoto();

        assertThat(result).hasSize(1);
        var resultPost = result.get(0);
        assertThat(resultPost.getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(post1.getCar());
        assertThat(resultPost.getPhotos()).usingRecursiveComparison()
                .isEqualTo(post1.getPhotos());
        assertThat(resultPost.getUser()).usingRecursiveComparison()
                .isEqualTo(post1.getUser());
    }

    /**
     * Проверяет успешный сценарий получения постов по модели методом {@code findPostsByModel}
     */
    @Test
    void whenFindPostsByModelThenGetData() {
        Post post1 = createFullPost("desc_model1", true);
        Post post2 = createFullPost("desc_model2", false);
        postRepository.create(post1);
        postRepository.create(post2);

        Model model = post1.getCar().getModel();
        var result = postRepository.findPostsByModel(model);

        assertThat(result).hasSize(1);
        var resultPost = result.get(0);
        assertThat(resultPost.getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(post1.getCar());
        assertThat(resultPost.getPhotos()).usingRecursiveComparison()
                .isEqualTo(post1.getPhotos());
        assertThat(resultPost.getUser()).usingRecursiveComparison()
                .isEqualTo(post1.getUser());
    }

    /**
     * Проверяет успешный сценарий сохранения поста методом {@code save}
     */
    @Test
    void whenSaveThenPostSaved() {
        User user = saveUser("user_desc_save");
        Car car = saveCar("car_desc_save");
        Photo photo = savePhoto("photo_desc_save", "/path/desc_save.jpg");
        Set<User> participates = new HashSet<>();
        Set<Photo> photos = new HashSet<>();
        photos.add(photo);
        List<PriceHistory> priceHistory = new ArrayList<>();
        Post post = new Post(0, "desc_save", LocalDateTime.now(), user, priceHistory, participates, car, photos, 1200, true);

        boolean saved = postRepository.save(post);

        assertThat(saved).isTrue();
        var result = postRepository.findById(post.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(car);
        assertThat(result.get().getPhotos()).usingRecursiveComparison()
                .isEqualTo(photos);
        assertThat(result.get().getUser()).usingRecursiveComparison()
                .isEqualTo(user);
    }

    /**
     * Проверяет успешный сценарий получения поста по id методом {@code findById}
     */
    @Test
    void whenFindByIdThenGetData() {
        Post post = createFullPost("desc_findid", true);
        postRepository.create(post);

        var result = postRepository.findById(post.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getCar()).usingRecursiveComparison()
                .ignoringFields("ownerships").isEqualTo(post.getCar());
        assertThat(result.get().getPhotos()).usingRecursiveComparison()
                .isEqualTo(post.getPhotos());
        assertThat(result.get().getUser()).usingRecursiveComparison()
                .isEqualTo(post.getUser());
    }

    /**
     * Проверяет успешный сценарий обновления поста методом {@code update}
     */
    @Test
    void whenUpdateThenPostUpdated() {
        Post post = createFullPost("desc_update", true);
        postRepository.create(post);

        post.setDescription("updated_desc");
        boolean updated = postRepository.update(post);

        assertThat(updated).isTrue();
        var result = postRepository.findById(post.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("updated_desc");
    }
}
