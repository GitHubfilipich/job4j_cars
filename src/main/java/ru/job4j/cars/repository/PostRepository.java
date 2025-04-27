package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Model;
import ru.job4j.cars.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PostRepository {
    private final CrudRepository crudRepository;

    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    public List<Post> findAll() {
        return crudRepository.query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.photos", Post.class);
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Post where id = :id",
                Map.of("id", id)
        );
    }

    public List<Post> findLastDayPosts() {

        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.photos "
                + "where p.created >= :lastDay", Post.class, Map.of("lastDay", LocalDateTime.now().minusHours(24)));
    }

    public List<Post> findPostsWithPhoto() {
        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates INNER JOIN FETCH p.photos", Post.class);
    }

    public List<Post> findPostsByModel(Model model) {
        return crudRepository.query("SELECT DISTINCT p FROM Post p "
                + "LEFT JOIN FETCH p.priceHistory LEFT JOIN FETCH p.participates LEFT JOIN FETCH p.photos "
                + "where p.car.model = :model", Post.class, Map.of("model", model));
    }
}
