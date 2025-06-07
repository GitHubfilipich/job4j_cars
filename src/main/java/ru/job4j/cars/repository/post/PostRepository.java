package ru.job4j.cars.repository.post;

import ru.job4j.cars.model.Model;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    public Post create(Post post);

    public List<Post> findAll();

    public boolean delete(int id);

    public List<Post> findLastDayPosts();

    public List<Post> findPostsWithPhoto();

    public List<Post> findPostsByModel(Model model);

    boolean save(Post post);

    Optional<Post> findById(int id);

    boolean update(Post post);
}
