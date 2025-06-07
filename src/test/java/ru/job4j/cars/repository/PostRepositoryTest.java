package ru.job4j.cars.repository;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.OwnerRepository;
import ru.job4j.cars.OwnershipRepository;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.car.SimpleCarRepository;
import ru.job4j.cars.repository.engine.SimpleEngineRepository;
import ru.job4j.cars.repository.model.SimpleModelRepository;
import ru.job4j.cars.repository.photo.SimplePhotoRepository;
import ru.job4j.cars.repository.post.SimplePostRepository;
import ru.job4j.cars.repository.user.SimpleUserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class PostRepositoryTest {

    private static StandardServiceRegistry registry;
    private static SessionFactory sf;
    private static SimplePostRepository postRepository;
    private static SimpleUserRepository userRepository;
    private static SimpleCarRepository carRepository;

    @BeforeAll
    public static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        postRepository = new SimplePostRepository(new CrudRepository(sf));
        userRepository = new SimpleUserRepository(new CrudRepository(sf));
        carRepository = new SimpleCarRepository(new CrudRepository(sf));
    }

    @AfterAll
    public static void close() {
        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @AfterEach
    public void clearData() {
        var engineRepository = new SimpleEngineRepository(new CrudRepository(sf));
        var ownerRepository = new OwnerRepository(new CrudRepository(sf));
        var carRepository = new SimpleCarRepository(new CrudRepository(sf));
        var ownershipRepository = new OwnershipRepository(new CrudRepository(sf));
        var modelRepository = new SimpleModelRepository(new CrudRepository(sf));
        var photoRepository = new SimplePhotoRepository(new CrudRepository(sf));
        var postRepository = new SimplePostRepository(new CrudRepository(sf));

        carRepository.findAll().forEach(car -> car.getOwnerships().clear());
        ownershipRepository.findAll().forEach(ownership -> ownershipRepository.delete(ownership.getId()));
        postRepository.findAll().forEach(post -> postRepository.delete(post.getId()));
        carRepository.findAll().forEach(car -> carRepository.delete(car.getId()));
        ownerRepository.findAll().forEach(owner -> ownerRepository.delete(owner.getId()));
        engineRepository.findAll().forEach(engine -> engineRepository.delete(engine.getId()));
        modelRepository.findAll().forEach(model -> modelRepository.delete(model.getId()));
        photoRepository.findAll().forEach(photo -> photoRepository.delete(photo.getId()));
    }

    @Test
    void whenCreateThenHavePost() {
        var users = userRepository.findAllOrderById();
        var post = new Post(0, "Test1", LocalDateTime.now(), users.get(0), List.of(), Set.of(), null, Set.of());

        var actualPost = postRepository.create(post);

        assertThat(actualPost).usingRecursiveComparison(
                        RecursiveComparisonConfiguration.builder()
                                .withEqualsForType((o, o2) -> o.truncatedTo(ChronoUnit.SECONDS)
                                        .equals(o2.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
                                .build()
                )
                .isEqualTo(post);
    }

    @Test
    void whenFindAllThenHaveAllPosts() {
        var users = userRepository.findAllOrderById();
        var posts = List.of(new Post(0, "Test1", LocalDateTime.now(), users.get(0), List.of(), Set.of(), null, Set.of()),
                new Post(0, "Test2", LocalDateTime.now(), users.get(1), List.of(), Set.of(), null, Set.of()),
                new Post(0, "Test3", LocalDateTime.now(), users.get(2), List.of(), Set.of(), null, Set.of()));
        posts.forEach(postRepository::create);

        var actualPosts = postRepository.findAll();

        assertThat(actualPosts).usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withEqualsForType((o, o2) -> o.truncatedTo(ChronoUnit.SECONDS)
                                        .equals(o2.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
                                .build()
                )
                .containsExactlyInAnyOrderElementsOf(posts);
    }

    @Test
    void whenDeleteThenHaveNotPosts() {
        var users = userRepository.findAllOrderById();
        var post = new Post(0, "Test1", LocalDateTime.now(), users.get(0), List.of(), Set.of(), null, Set.of());
        postRepository.create(post);

        postRepository.delete(post.getId());
        var actualPosts = postRepository.findAll();

        assertThat(actualPosts).isEmpty();
    }

    @Test
    void whenFindLastDayPostsThenHaveCorrectPosts() {
        var users = userRepository.findAllOrderById();
        var posts = List.of(new Post(0, "Test1", LocalDateTime.now(), users.get(0), List.of(), Set.of(), null, Set.of()),
                new Post(0, "Test2", LocalDateTime.now().minusHours(12), users.get(1), List.of(), Set.of(), null, Set.of()),
                new Post(0, "Test3", LocalDateTime.now().minusDays(2), users.get(2), List.of(), Set.of(), null, Set.of()));
        posts.forEach(postRepository::create);
        var expectedPosts = posts.stream().filter(post -> post.getCreated().isAfter(LocalDateTime.now().minusHours(24))).toList();

        var actualPosts = postRepository.findLastDayPosts();

        assertThat(actualPosts).usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withEqualsForType((o, o2) -> o.truncatedTo(ChronoUnit.SECONDS)
                                        .equals(o2.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
                                .build()
                )
                .containsExactlyInAnyOrderElementsOf(expectedPosts);
    }

    @Test
    void whenFindPostsWithPhotoThenHaveCorrectPosts() {
        var users = userRepository.findAllOrderById();
        var photos = List.of(new Photo(0, "Photo1", "FilePath1"),
                new Photo(0, "Photo2", "FilePath2"),
                new Photo(0, "Photo3", "FilePath3"));
        var posts = List.of(new Post(0, "Test1", LocalDateTime.now(), users.get(0), List.of(), Set.of(), null, Set.of(photos.get(0), photos.get(1))),
                new Post(0, "Test2", LocalDateTime.now().minusHours(12), users.get(1), List.of(), Set.of(), null, Set.of(photos.get(2))),
                new Post(0, "Test3", LocalDateTime.now().minusDays(2), users.get(2), List.of(), Set.of(), null, Set.of()));
        posts.forEach(postRepository::create);
        var expectedPosts = posts.stream().filter(post -> !post.getPhotos().isEmpty()).toList();

        var actualPosts = postRepository.findPostsWithPhoto();

        assertThat(actualPosts).usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withEqualsForType((o, o2) -> o.truncatedTo(ChronoUnit.SECONDS)
                                        .equals(o2.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
                                .build()
                )
                .containsExactlyInAnyOrderElementsOf(expectedPosts);
    }

    @Test
    void whenFindPostsByModelThenHaveCorrectPosts() {
        var users = userRepository.findAllOrderById();
        var models = List.of(new Model(0, "Model1"), new Model(0, "Model2"), new Model(0, "Model3"));
        var engines = List.of(new Engine(0, "Engine1"), new Engine(0, "Engine2"), new Engine(0, "Engine3"));
        var owners = List.of(new Owner(0, "Owner1"), new Owner(0, "Owner2"), new Owner(0, "Owner3"));
        var cars = List.of(new Car(0, "Car1", engines.get(0), owners.get(0), Set.of(), models.get(0)),
                new Car(0, "Car2", engines.get(1), owners.get(1), Set.of(), models.get(1)),
                new Car(0, "Car3", engines.get(2), owners.get(2), Set.of(), models.get(2)));
        cars.forEach(carRepository::create);
        var posts = List.of(new Post(0, "Test1", LocalDateTime.now(), users.get(0), List.of(), Set.of(), cars.get(0), Set.of()),
                new Post(0, "Test2", LocalDateTime.now().minusHours(12), users.get(1), List.of(), Set.of(), cars.get(1), Set.of()),
                new Post(0, "Test3", LocalDateTime.now().minusDays(2), users.get(2), List.of(), Set.of(), cars.get(2), Set.of()));
        posts.forEach(postRepository::create);
        var model = models.get(1);
        var expectedPosts = posts.stream().filter(post -> post.getCar().getModel() == model).toList();

        var actualPosts = postRepository.findPostsByModel(model);

        assertThat(actualPosts)
                .usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withEqualsForType((o, o2) -> o.truncatedTo(ChronoUnit.SECONDS)
                                        .equals(o2.truncatedTo(ChronoUnit.SECONDS)), LocalDateTime.class)
                                .withIgnoredFields("car.ownerships")
                                .build()
                )
                .containsExactlyInAnyOrderElementsOf(expectedPosts);
    }
}