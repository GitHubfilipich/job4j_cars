package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class CarUsage {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try (SessionFactory sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory()) {

            var postRepository = new PostRepository(new CrudRepository(sf));
            var modelRepository = new ModelRepository(new CrudRepository(sf));

            clearData(sf);

            addData(sf);

            printData(sf);

            var lastDayPosts = postRepository.findLastDayPosts();
            var postsWithFoto = postRepository.findPostsWithFoto();

            var model = modelRepository.findAll().get(0);
            var postsByModel = postRepository.findPostsByModel(model);

            System.out.println("!!! findLastDayPosts !!!");
            lastDayPosts.forEach(System.out::println);

            System.out.println("!!! findPostsWithFoto !!!");
            postsWithFoto.forEach(System.out::println);

            System.out.println("!!! findPostsByModel(" + model.getName() + ") !!!");
            postsByModel.forEach(System.out::println);

        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static void clearData(SessionFactory sf) {
        var engineRepository = new EngineRepository(new CrudRepository(sf));
        var ownerRepository = new OwnerRepository(new CrudRepository(sf));
        var carRepository = new CarRepository(new CrudRepository(sf));
        var ownershipRepository = new OwnershipRepository(new CrudRepository(sf));
        var modelRepository = new ModelRepository(new CrudRepository(sf));
        var fotoRepository = new FotoRepository(new CrudRepository(sf));
        var postRepository = new PostRepository(new CrudRepository(sf));

        System.out.println("!!! очистка таблиц !!!");
        carRepository.findAll().forEach(car -> car.getOwnerships().clear());
        ownershipRepository.findAll().forEach(ownership -> ownershipRepository.delete(ownership.getId()));
        postRepository.findAll().forEach(post -> postRepository.delete(post.getId()));
        carRepository.findAll().forEach(car -> carRepository.delete(car.getId()));
        ownerRepository.findAll().forEach(owner -> ownerRepository.delete(owner.getId()));
        engineRepository.findAll().forEach(engine -> engineRepository.delete(engine.getId()));
        modelRepository.findAll().forEach(model -> modelRepository.delete(model.getId()));
        fotoRepository.findAll().forEach(foto -> fotoRepository.delete(foto.getId()));
    }

    private static void addData(SessionFactory sf) {
        var carRepository = new CarRepository(new CrudRepository(sf));
        var postRepository = new PostRepository(new CrudRepository(sf));
        var userRepository = new UserRepository(new CrudRepository(sf));

        System.out.println("!!! заполнение Engine !!!");
        var engines = List.of(new Engine(0, "Engine1"),
                new Engine(0, "Engine2"),
                new Engine(0, "Engine3"));

        System.out.println("!!! заполнение Owner !!!");
        var owners = List.of(new Owner(0, "Owner1"),
                new Owner(0, "Owner2"),
                new Owner(0, "Owner3"),
                new Owner(0, "Owner4"),
                new Owner(0, "Owner5"));

        System.out.println("!!! заполнение Model !!!");
        var models = List.of(new Model(0, "Model1"),
                new Model(0, "Model2"),
                new Model(0, "Model3"));

        System.out.println("!!! заполнение Car !!!");

        var car1 = new Car(0, "Car1", engines.get(0), owners.get(0), new HashSet<>(), models.get(0));
        car1.getOwnerships().add(new Ownership(0, owners.get(0), car1, LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 6, 1, 10, 0)));
        car1.getOwnerships().add(new Ownership(0, owners.get(1), car1, LocalDateTime.of(2024, 6, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 10, 0)));
        car1.getOwnerships().add(new Ownership(0, owners.get(2), car1, LocalDateTime.of(2025, 1, 1, 10, 0),
                null));
        carRepository.create(car1);

        var car2 = new Car(0, "Car2", engines.get(1), owners.get(3), new HashSet<>(), models.get(1));
        car2.getOwnerships().add(new Ownership(0, owners.get(3), car2, LocalDateTime.of(2020, 1, 1, 10, 0),
                null));

        carRepository.create(car2);

        var car3 = new Car(0, "Car3", engines.get(2), owners.get(4), new HashSet<>(), models.get(2));
        car3.getOwnerships().add(new Ownership(0, owners.get(4), car3, LocalDateTime.of(2020, 1, 1, 10, 0),
                null));

        carRepository.create(car3);

        System.out.println("!!! заполнение Foto !!!");
        var fotos = List.of(new Foto(0, "Foto1", "FilePath1"),
                new Foto(0, "Foto2", "FilePath2"),
                new Foto(0, "Foto3", "FilePath3"));

        System.out.println("!!! заполнение Post !!!");
        var users = userRepository.findAllOrderById();

        var post1 = new Post(0, "Description1", LocalDateTime.now(), users.get(0), new ArrayList<>(), new HashSet<>(),
                car1, Set.of(fotos.get(0), fotos.get(1)));
        postRepository.create(post1);

        var post2 = new Post(0, "Description2", LocalDateTime.now(), users.get(1), new ArrayList<>(), new HashSet<>(),
                car2, Set.of(fotos.get(2)));
        postRepository.create(post2);

        var post3 = new Post(0, "Description3", LocalDateTime.of(2025, 1, 1, 0, 0),
                users.get(2), new ArrayList<>(), new HashSet<>(),
                car3, new HashSet<>());
        postRepository.create(post3);
    }

    private static void printData(SessionFactory sf) {
        var engineRepository = new EngineRepository(new CrudRepository(sf));
        var ownerRepository = new OwnerRepository(new CrudRepository(sf));
        var carRepository = new CarRepository(new CrudRepository(sf));
        var ownershipRepository = new OwnershipRepository(new CrudRepository(sf));
        var modelRepository = new ModelRepository(new CrudRepository(sf));
        var fotoRepository = new FotoRepository(new CrudRepository(sf));
        var postRepository = new PostRepository(new CrudRepository(sf));

        Map.of("1 !!! Engine !!!", engineRepository.findAll(),
                        "2 !!! Owner !!!", ownerRepository.findAll(),
                        "3 !!! Ownership !!!", ownershipRepository.findAll(),
                        "4 !!! Model !!!", modelRepository.findAll(),
                        "5 !!! Foto !!!", fotoRepository.findAll(),
                        "6 !!! Car !!!", carRepository.findAll(),
                        "7 !!! Post !!!", postRepository.findAll())
                .entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    System.out.println(entry.getKey());
                    entry.getValue().forEach(System.out::println);
                });
    }

}
