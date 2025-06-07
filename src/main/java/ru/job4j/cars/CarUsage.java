package ru.job4j.cars;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CrudRepository;
import ru.job4j.cars.repository.car.SimpleCarRepository;
import ru.job4j.cars.repository.engine.SimpleEngineRepository;
import ru.job4j.cars.repository.model.SimpleModelRepository;
import ru.job4j.cars.repository.photo.SimplePhotoRepository;
import ru.job4j.cars.repository.post.SimplePostRepository;
import ru.job4j.cars.repository.user.SimpleUserRepository;

import java.util.*;

public class CarUsage {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try (SessionFactory sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory()) {

            var postRepository = new SimplePostRepository(new CrudRepository(sf));
            var modelRepository = new SimpleModelRepository(new CrudRepository(sf));

            clearData(sf);

            addData(sf);

            printData(sf);

            var lastDayPosts = postRepository.findLastDayPosts();
            var postsWithPhoto = postRepository.findPostsWithPhoto();

            var model = modelRepository.findAll().get(0);
            var postsByModel = postRepository.findPostsByModel(model);

            System.out.println("!!! findLastDayPosts !!!");
            lastDayPosts.forEach(System.out::println);

            System.out.println("!!! findPostsWithPhoto !!!");
            postsWithPhoto.forEach(System.out::println);

            System.out.println("!!! findPostsByModel(" + model.getName() + ") !!!");
            postsByModel.forEach(System.out::println);

        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    private static void clearData(SessionFactory sf) {
        var engineRepository = new SimpleEngineRepository(new CrudRepository(sf));
        var ownerRepository = new OwnerRepository(new CrudRepository(sf));
        var carRepository = new SimpleCarRepository(new CrudRepository(sf));
        var ownershipRepository = new OwnershipRepository(new CrudRepository(sf));
        var modelRepository = new SimpleModelRepository(new CrudRepository(sf));
        var photoRepository = new SimplePhotoRepository(new CrudRepository(sf));
        var postRepository = new SimplePostRepository(new CrudRepository(sf));

        System.out.println("!!! очистка таблиц !!!");
        carRepository.findAll().forEach(car -> car.getOwnerships().clear());
        ownershipRepository.findAll().forEach(ownership -> ownershipRepository.delete(ownership.getId()));
        postRepository.findAll().forEach(post -> postRepository.delete(post.getId()));
        carRepository.findAll().forEach(car -> carRepository.delete(car.getId()));
        ownerRepository.findAll().forEach(owner -> ownerRepository.delete(owner.getId()));
        engineRepository.findAll().forEach(engine -> engineRepository.delete(engine.getId()));
        modelRepository.findAll().forEach(model -> modelRepository.delete(model.getId()));
        photoRepository.findAll().forEach(photo -> photoRepository.delete(photo.getId()));
    }

    private static void addData(SessionFactory sf) {
        var carRepository = new SimpleCarRepository(new CrudRepository(sf));
        var postRepository = new SimplePostRepository(new CrudRepository(sf));
        var userRepository = new SimpleUserRepository(new CrudRepository(sf));

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
    }

    private static void printData(SessionFactory sf) {
        var engineRepository = new SimpleEngineRepository(new CrudRepository(sf));
        var ownerRepository = new OwnerRepository(new CrudRepository(sf));
        var carRepository = new SimpleCarRepository(new CrudRepository(sf));
        var ownershipRepository = new OwnershipRepository(new CrudRepository(sf));
        var modelRepository = new SimpleModelRepository(new CrudRepository(sf));
        var photoRepository = new SimplePhotoRepository(new CrudRepository(sf));
        var postRepository = new SimplePostRepository(new CrudRepository(sf));

        Map.of("1 !!! Engine !!!", engineRepository.findAll(),
                        "2 !!! Owner !!!", ownerRepository.findAll(),
                        "3 !!! Ownership !!!", ownershipRepository.findAll(),
                        "4 !!! Model !!!", modelRepository.findAll(),
                        "5 !!! Photo !!!", photoRepository.findAll(),
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
