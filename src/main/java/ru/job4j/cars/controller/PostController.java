package ru.job4j.cars.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

@Controller
@RequestMapping
public class PostController {
    private final BodyTypeService bodyTypeService;
    private final BrandService brandService;
    private final EngineService engineService;
    private final GearboxService gearboxService;
    private final ModelService modelService;
    private final PostService postService;

    public PostController(BodyTypeService bodyTypeService, BrandService brandService, EngineService engineService, GearboxService gearboxService, ModelService modelService, PostService postService) {
        this.bodyTypeService = bodyTypeService;
        this.brandService = brandService;
        this.engineService = engineService;
        this.gearboxService = gearboxService;
        this.modelService = modelService;
        this.postService = postService;
    }

    @GetMapping
    public String getPosts(@ModelAttribute("filter") PostFilterDTO filter, Model model) {
        addCarParameters(model);
        model.addAttribute("posts", postService.findAllByFilter(filter));
        return "posts/list";
    }

    @GetMapping("/add")
    public String addPost(Model model, @SessionAttribute(name = "user") User user) {
        addCarParameters(model);
        PostDTO post = new PostDTO();
        post.setUserId(user.getId());
        post.setUserName(user.getName());
        model.addAttribute("post", post);
        model.addAttribute("status", "new");
        return "posts/post";
    }

    private void addCarParameters(Model model) {
        model.addAttribute("bodyTypes", bodyTypeService.findAll());
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("engines", engineService.findAll());
        model.addAttribute("gearboxes", gearboxService.findAll());
        model.addAttribute("models", modelService.findAll());
        model.addAttribute("sortTypes", SortType.values());
        model.addAttribute("postingPeriods", PostingPeriod.values());
    }

    @GetMapping("/post/{id}")
    public String getPost(Model model, @PathVariable int id) {
        Optional<PostDTO> post = postService.findById(id);
        if (post.isEmpty()) {
            model.addAttribute("message", "Не удалось найти объявление");
            return "errors/404";
        }
        model.addAttribute("post", post.get());
        addCarParameters(model);
        return "posts/post";
    }

    @GetMapping("/post/edit/{id}")
    public String editPost(Model model, @PathVariable int id) {
        Optional<PostDTO> post = postService.findById(id);
        if (post.isEmpty()) {
            model.addAttribute("message", "Не удалось найти объявление");
            return "errors/404";
        }
        model.addAttribute("post", post.get());
        model.addAttribute("status", "edit");
        addCarParameters(model);
        return "posts/post";
    }

    @PostMapping("/post/update")
    public String saveOrUpdate(@ModelAttribute PostDTO post, @RequestParam("status") String status, @RequestParam("photos") List<MultipartFile> photos, Model model) {
        if ("new".equals(status)) {
            post.setCreated(LocalDateTime.now());
            return save(post, model, photos);
        }
        return update(post, model, photos);
    }

    @GetMapping("/post/delete/{id}")
    public String deletePost(Model model, @PathVariable int id) {
        if (!postService.delete(id)) {
            model.addAttribute("message", "Не удалось удалить объявление");
            return "errors/404";
        }
        return "redirect:/";
    }

    private String save(PostDTO post, Model model, List<MultipartFile> photos) {
        if (!postService.save(post, photos)) {
            model.addAttribute("message", "Не удалось сохранить объявление");
            return "errors/404";
        }
        return "redirect:/";
    }

    private String update(PostDTO post, Model model, List<MultipartFile> photos) {
        if (!postService.update(post, photos)) {
            model.addAttribute("message", "Не удалось обновить объявление");
            return "errors/404";
        }
        return "redirect:/";
    }
}
