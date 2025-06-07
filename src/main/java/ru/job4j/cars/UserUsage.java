package ru.job4j.cars;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.CrudRepository;
import ru.job4j.cars.repository.user.SimpleUserRepository;

public class UserUsage {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try (SessionFactory sf = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory()) {
            var userRepository = new SimpleUserRepository(new CrudRepository(sf));
            var user = new User();
            user.setLogin("admin");
            user.setPassword("admin");
            userRepository.create(user);

            var rsl1 = userRepository.findAllOrderById();
            System.out.println("1. findAllOrderById:");
            rsl1.forEach(System.out::println);

            var rsl2 = userRepository.findByLikeLogin("e");
            System.out.println("2. findByLikeLogin \"e\":");
            rsl2.forEach(System.out::println);

            var rsl3 = userRepository.findById(user.getId());
            System.out.println("3. findById \"" + user.getId() + "\":");
            rsl3.ifPresent(System.out::println);

            var rsl4 = userRepository.findByLogin("admin");
            System.out.println("4. findByLogin \"admin\":");
            rsl4.ifPresent(System.out::println);

            user.setPassword("password");
            userRepository.update(user);

            var rsl5 = userRepository.findById(user.getId());
            System.out.println("5. findById \"" + user.getId() + "\":");
            rsl5.ifPresent(System.out::println);

            userRepository.delete(user.getId());

            var rsl6 = userRepository.findAllOrderById();
            System.out.println("6. findAllOrderById:");
            rsl6.forEach(System.out::println);
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
