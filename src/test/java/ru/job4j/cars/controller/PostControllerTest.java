package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PostDTO;
import ru.job4j.cars.dto.PostFilterDTO;
import ru.job4j.cars.model.*;
import ru.job4j.cars.service.bodytype.BodyTypeService;
import ru.job4j.cars.service.brand.BrandService;
import ru.job4j.cars.service.engine.EngineService;
import ru.job4j.cars.service.gearbox.GearboxService;
import ru.job4j.cars.service.model.ModelService;
import ru.job4j.cars.service.post.PostService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostControllerTest {

    private PostController postController;
    private BodyTypeService bodyTypeService;
    private BrandService brandService;
    private EngineService engineService;
    private GearboxService gearboxService;
    private ModelService modelService;
    private PostService postService;

    private List<BodyType> bodyTypes;
    private List<Brand> brands;
    private List<Engine> engines;
    private List<Gearbox> gearboxes;
    private List<ru.job4j.cars.model.Model> models;
    private List<PostDTO> posts;

    @BeforeEach
    public void init() {
        this.bodyTypeService = mock(BodyTypeService.class);
        this.brandService = mock(BrandService.class);
        this.engineService = mock(EngineService.class);
        this.gearboxService = mock(GearboxService.class);
        this.modelService = mock(ModelService.class);
        this.postService = mock(PostService.class);

        this.postController = new PostController(
                bodyTypeService, brandService, engineService, gearboxService, modelService, postService);

        bodyTypes = List.of(new BodyType(1, "Sedan"), new BodyType(2, "Hatchback"), new BodyType(3, "SUV"));
        brands = List.of(new Brand(1, "Toyota"), new Brand(2, "Honda"), new Brand(3, "Ford"));
        engines = List.of(new Engine(1, "Petrol"), new Engine(2, "Diesel"), new Engine(3, "Hybrid"));
        gearboxes = List.of(new Gearbox(1, "Automatic"), new Gearbox(2, "Manual"), new Gearbox(3, "Robotic"));
        models = List.of(new ru.job4j.cars.model.Model(1, "Camry"), new ru.job4j.cars.model.Model(2, "Civic"), new ru.job4j.cars.model.Model(3, "Focus"));
        posts = List.of(new PostDTO(1, 101, "testUser1", "Отличный автомобиль в идеальном состоянии", 201, 1, "Седан", 1, "Toyota", 1, "Camry", 1, "Бензин 2.0L", 1, "Автомат", 2020, 2500000, 35000, 150, true, true, null, LocalDateTime.now().minusDays(1)),
                new PostDTO(2, 102, "autoDealer", "Продажа нового автомобиля из салона", 202, 2, "Внедорожник", 2, "Honda", 2, "CR-V", 2, "Дизель 2.2L", 2, "Робот", 2023, 3500000, 100, 190, true, false, null, LocalDateTime.now().minusHours(5)),
                new PostDTO(3, 103, "carEnthusiast", "Редкий экземпляр в отличном состоянии", 203, 3, "Купе", 3, "BMW", 3, "M4", 3, "Бензин 3.0L Twin-Turbo", 3, "Робот", 2019, 4500000, 75000, 431, true, true, null, LocalDateTime.now().minusDays(3)));
    }

    /**
     * Проверяет успешный сценарий возврата данных объявлений методом {@code getPosts}
     */
    @Test
    void whetGetPostsThenGetPageWithPostDTOsAndCarParameters() {
        PostFilterDTO filter = new PostFilterDTO(
                true, 1, 1, 1, 1, 1, 100, 200,
                2010, 2020, 1000, 2000, true, true,
                SortType.DATE_DESC, PostingPeriod.DAY);
        Model model = new ConcurrentModel();

        when(bodyTypeService.findAll()).thenReturn(bodyTypes);
        when(brandService.findAll()).thenReturn(brands);
        when(engineService.findAll()).thenReturn(engines);
        when(gearboxService.findAll()).thenReturn(gearboxes);
        when(modelService.findAll()).thenReturn(models);
        when(postService.findAllByFilter(filter)).thenReturn(posts);

        String viewName = postController.getPosts(filter, model);

        assertThat(viewName).isEqualTo("posts/list");

        assertThat(model).hasFieldOrPropertyWithValue("posts", posts);
        assertThat(model).hasFieldOrPropertyWithValue("bodyTypes", bodyTypes);
        assertThat(model).hasFieldOrPropertyWithValue("brands", brands);
        assertThat(model).hasFieldOrPropertyWithValue("engines", engines);
        assertThat(model).hasFieldOrPropertyWithValue("gearboxes", gearboxes);
        assertThat(model).hasFieldOrPropertyWithValue("models", models);
        assertThat(model).hasFieldOrPropertyWithValue("sortTypes", SortType.values());
        assertThat(model).hasFieldOrPropertyWithValue("postingPeriods", PostingPeriod.values());
    }

    /**
     * Проверяет успешный сценарий возврата данных нового объявления методом {@code addPost}
     */
    @Test
    void whenAddPostThenGetPostPageWithCarParameters() {
        Model model = new ConcurrentModel();
        User user = new User(1, "testUser1", "testLogin1", "testPassword1");

        when(bodyTypeService.findAll()).thenReturn(bodyTypes);
        when(brandService.findAll()).thenReturn(brands);
        when(engineService.findAll()).thenReturn(engines);
        when(gearboxService.findAll()).thenReturn(gearboxes);
        when(modelService.findAll()).thenReturn(models);

        String viewName = postController.addPost(model, user);
        Object actualPost = model.getAttribute("post");

        assertThat(viewName).isEqualTo("posts/post");
        assertThat(actualPost).isInstanceOf(PostDTO.class)
                .hasFieldOrPropertyWithValue("userId", user.getId())
                .hasFieldOrPropertyWithValue("userName", user.getName());

        assertThat(model).hasFieldOrPropertyWithValue("status", "new");
        assertThat(model).hasFieldOrPropertyWithValue("bodyTypes", bodyTypes);
        assertThat(model).hasFieldOrPropertyWithValue("brands", brands);
        assertThat(model).hasFieldOrPropertyWithValue("engines", engines);
        assertThat(model).hasFieldOrPropertyWithValue("gearboxes", gearboxes);
        assertThat(model).hasFieldOrPropertyWithValue("models", models);
        assertThat(model).hasFieldOrPropertyWithValue("sortTypes", SortType.values());
        assertThat(model).hasFieldOrPropertyWithValue("postingPeriods", PostingPeriod.values());
    }

    /**
     * Проверяет успешный сценарий возврата данных объявления методом {@code getPost}
     */
    @Test
    void whenGetPostThenGetPostPageWithParameters() {
        int id = 1;
        Model model = new ConcurrentModel();

        when(bodyTypeService.findAll()).thenReturn(bodyTypes);
        when(brandService.findAll()).thenReturn(brands);
        when(engineService.findAll()).thenReturn(engines);
        when(gearboxService.findAll()).thenReturn(gearboxes);
        when(modelService.findAll()).thenReturn(models);

        ArgumentCaptor<Integer> intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(postService.findById(intArgCaptor.capture())).thenReturn(Optional.of(posts.get(0)));

        String viewName = postController.getPost(model, id);
        int actualId = intArgCaptor.getValue();

        assertThat(viewName).isEqualTo("posts/post");
        assertThat(actualId).isEqualTo(id);

        assertThat(model).hasFieldOrPropertyWithValue("post", posts.get(0));
        assertThat(model).hasFieldOrPropertyWithValue("bodyTypes", bodyTypes);
        assertThat(model).hasFieldOrPropertyWithValue("brands", brands);
        assertThat(model).hasFieldOrPropertyWithValue("engines", engines);
        assertThat(model).hasFieldOrPropertyWithValue("gearboxes", gearboxes);
        assertThat(model).hasFieldOrPropertyWithValue("models", models);
        assertThat(model).hasFieldOrPropertyWithValue("sortTypes", SortType.values());
        assertThat(model).hasFieldOrPropertyWithValue("postingPeriods", PostingPeriod.values());
    }

    /**
     * Проверяет неуспешный сценарий возврата данных объявления методом {@code getPost}
     */
    @Test
    void whenGetPostUnsuccessfulThenGetErrorPage() {
        int id = 1;
        Model model = new ConcurrentModel();

        when(postService.findById(any(Integer.class))).thenReturn(Optional.empty());

        String viewName = postController.getPost(model, id);

        assertThat(viewName).isEqualTo("errors/404");

        assertThat(model).hasFieldOrPropertyWithValue("message", "Не удалось найти объявление");
    }

    /**
     * Проверяет успешный сценарий возврата данных объявления для редактирования методом {@code editPost}
     */
    @Test
    void whenEditPostThenGetPostPageWithParameters() {
        int id = 1;
        Model model = new ConcurrentModel();

        when(bodyTypeService.findAll()).thenReturn(bodyTypes);
        when(brandService.findAll()).thenReturn(brands);
        when(engineService.findAll()).thenReturn(engines);
        when(gearboxService.findAll()).thenReturn(gearboxes);
        when(modelService.findAll()).thenReturn(models);

        ArgumentCaptor<Integer> intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(postService.findById(intArgCaptor.capture())).thenReturn(Optional.of(posts.get(0)));

        String viewName = postController.editPost(model, id);
        int actualId = intArgCaptor.getValue();

        assertThat(viewName).isEqualTo("posts/post");
        assertThat(actualId).isEqualTo(id);

        assertThat(model).hasFieldOrPropertyWithValue("post", posts.get(0));
        assertThat(model).hasFieldOrPropertyWithValue("status", "edit");
        assertThat(model).hasFieldOrPropertyWithValue("bodyTypes", bodyTypes);
        assertThat(model).hasFieldOrPropertyWithValue("brands", brands);
        assertThat(model).hasFieldOrPropertyWithValue("engines", engines);
        assertThat(model).hasFieldOrPropertyWithValue("gearboxes", gearboxes);
        assertThat(model).hasFieldOrPropertyWithValue("models", models);
        assertThat(model).hasFieldOrPropertyWithValue("sortTypes", SortType.values());
        assertThat(model).hasFieldOrPropertyWithValue("postingPeriods", PostingPeriod.values());
    }

    /**
     * Проверяет неуспешный сценарий возврата данных объявления для редактирования методом {@code editPost}
     */
    @Test
    void whenEditPostUnsuccessfulThenGetErrorPage() {
        int id = 1;
        Model model = new ConcurrentModel();

        when(postService.findById(any(Integer.class))).thenReturn(Optional.empty());

        String viewName = postController.editPost(model, id);

        assertThat(viewName).isEqualTo("errors/404");

        assertThat(model).hasFieldOrPropertyWithValue("message", "Не удалось найти объявление");
    }

    /**
     * Проверяет успешный сценарий сохранения данных объявления методом {@code saveOrUpdate}
     */
    @Test
    void whenSaveOrUpdateOperationSaveThenGetPostPageAndCallSave() {
        Model model = new ConcurrentModel();
        List<MultipartFile> photos = List.of(mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class));

        ArgumentCaptor<PostDTO> postArgCaptor = ArgumentCaptor.forClass(PostDTO.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MultipartFile>> listArgCaptor = ArgumentCaptor.forClass(List.class);
        when(postService.save(postArgCaptor.capture(), listArgCaptor.capture())).thenReturn(true);

        String viewName = postController.saveOrUpdate(posts.get(0), "new", photos, model);
        PostDTO actualPost = postArgCaptor.getValue();
        List<MultipartFile> actualPhotos = listArgCaptor.getValue();

        assertThat(viewName).isEqualTo("redirect:/");
        assertThat(actualPost).isEqualTo(posts.get(0));
        assertThat(actualPhotos).isEqualTo(photos);
    }

    /**
     * Проверяет неуспешный сценарий сохранения данных объявления методом {@code saveOrUpdate}
     */
    @Test
    void whenSaveOrUpdateOperationSaveUnsuccessfulThenGetErrorPage() {
        Model model = new ConcurrentModel();

        when(postService.save(any(PostDTO.class), anyList())).thenReturn(false);

        String viewName = postController.saveOrUpdate(posts.get(0), "new", List.of(), model);

        assertThat(viewName).isEqualTo("errors/404");
        assertThat(model).hasFieldOrPropertyWithValue("message", "Не удалось сохранить объявление");
    }

    /**
     * Проверяет успешный сценарий обновления данных объявления методом {@code saveOrUpdate}
     */
    @Test
    void whenSaveOrUpdateOperationUpdateThenGetPostPageAndCallUpdate() {
        Model model = new ConcurrentModel();
        List<MultipartFile> photos = List.of(mock(MultipartFile.class), mock(MultipartFile.class),
                mock(MultipartFile.class));

        ArgumentCaptor<PostDTO> postArgCaptor = ArgumentCaptor.forClass(PostDTO.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MultipartFile>> listArgCaptor = ArgumentCaptor.forClass(List.class);
        when(postService.update(postArgCaptor.capture(), listArgCaptor.capture())).thenReturn(true);

        String viewName = postController.saveOrUpdate(posts.get(0), "", photos, model);
        PostDTO actualPost = postArgCaptor.getValue();
        List<MultipartFile> actualPhotos = listArgCaptor.getValue();

        assertThat(viewName).isEqualTo("redirect:/");
        assertThat(actualPost).isEqualTo(posts.get(0));
        assertThat(actualPhotos).isEqualTo(photos);
    }

    /**
     * Проверяет неуспешный сценарий обновления данных объявления методом {@code saveOrUpdate}
     */
    @Test
    void whenSaveOrUpdateOperationUpdateUnsuccessfulThenGetErrorPage() {
        Model model = new ConcurrentModel();

        when(postService.save(any(PostDTO.class), anyList())).thenReturn(false);

        String viewName = postController.saveOrUpdate(posts.get(0), "", List.of(), model);

        assertThat(viewName).isEqualTo("errors/404");
        assertThat(model).hasFieldOrPropertyWithValue("message", "Не удалось обновить объявление");
    }

    /**
     * Проверяет успешный сценарий удаления объявления методом {@code deletePost}
     */
    @Test
    void whenDeletePostThenGetPostPage() {
        int id = 1;
        Model model = new ConcurrentModel();

        ArgumentCaptor<Integer> intArgCaptor = ArgumentCaptor.forClass(Integer.class);
        when(postService.delete(intArgCaptor.capture())).thenReturn(true);

        String viewName = postController.deletePost(model, id);
        int actualId = intArgCaptor.getValue();

        assertThat(viewName).isEqualTo("redirect:/");
        assertThat(actualId).isEqualTo(id);
    }

    /**
     * Проверяет неуспешный сценарий удаления объявления методом {@code deletePost}
     */
    @Test
    void whenDeletePostUnsuccessfulThenGetErrorPage() {
        int id = 1;
        Model model = new ConcurrentModel();

        when(postService.delete(any(Integer.class))).thenReturn(false);

        String viewName = postController.deletePost(model, id);

        assertThat(viewName).isEqualTo("errors/404");
        assertThat(model).hasFieldOrPropertyWithValue("message", "Не удалось удалить объявление");
    }
}