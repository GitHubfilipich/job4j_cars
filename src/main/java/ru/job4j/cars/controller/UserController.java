package ru.job4j.cars.controller;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "users/register";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user, HttpSession session) {
        try {
            userService.save(user);
        } catch (Exception e) {
            String message = isConstraintViolation(e) ? "Пользователь с таким логином уже существует" : e.getMessage();
            model.addAttribute("message", "Ошибка при регистрации пользователя: " + message);
            model.addAttribute("user", null);
            return "errors/404";
        }
        session.setAttribute("user", user);
        return "redirect:/";
    }

    private boolean isConstraintViolation(Throwable e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof ConstraintViolationException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest request) {
        var userOptional = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Логин или пароль введены неверно");
            return "users/login";
        }
        var session = request.getSession();
        session.setAttribute("user", userOptional.get());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
