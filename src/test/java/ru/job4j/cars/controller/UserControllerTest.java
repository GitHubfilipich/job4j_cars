package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpSession session;

    @BeforeEach
    void init() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
        session = Mockito.mock(HttpSession.class);
    }

    /**
     * Проверяет сценарий возврата страницы регистрации пользователя методом {@code getRegistrationPage}
     */
    @Test
    void whenGetRegistrationPageThenGetPageAndModelArgument() {
        var user = new User(0, "testLogin", "testPassword", "testName");
        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(session.getAttribute(stringArgumentCaptor.capture())).thenReturn(user);
        var model = new ConcurrentModel();

        var actualResponse = userController.getRegistrationPage(model, session);
        var actualAttribute = stringArgumentCaptor.getValue();

        assertThat(actualAttribute).isEqualTo("user");
        assertThat(actualResponse).isEqualTo("users/register");
        assertThat(model).hasFieldOrPropertyWithValue("user", user);
    }

    /**
     * Проверяет успешный сценарий регистрации пользователя методом {@code register}
     */
    @Test
    void whenRegisterThenGetPageAndSessionAttribute() {
        var user = new User(0, "testLogin", "testPassword", "testName");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(true);
        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var userSessionArgumentCaptor = ArgumentCaptor.forClass(User.class);
        doNothing().when(session).setAttribute(stringArgumentCaptor.capture(), userSessionArgumentCaptor.capture());
        var model = new ConcurrentModel();

        var actualResponse = userController.register(model, user, session);
        var actualUser = userArgumentCaptor.getValue();
        var actualAttribute = stringArgumentCaptor.getValue();
        var actualUserSession = userSessionArgumentCaptor.getValue();

        assertThat(actualResponse).isEqualTo("redirect:/");
        assertThat(actualUser).isEqualTo(user);
        assertThat(actualAttribute).isEqualTo("user");
        assertThat(actualUserSession).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий регистрации пользователя методом {@code register}
     */
    @Test
    void whenRegisterUnSuccessfulThenGetErrorPageAndClearSessionAttribute() {
        var user = new User(0, "testLogin", "testPassword", "testName");
        when(userService.save(any(User.class))).thenReturn(false);
        var model = new ConcurrentModel();

        var actualResponse = userController.register(model, user, session);

        assertThat(actualResponse).isEqualTo("errors/404");
        assertThat(model).hasFieldOrPropertyWithValue("message", "Пользователь с таким логином уже существует");
        assertThat(model.containsAttribute("user")).isFalse();
    }

    /**
     * Проверяет сценарий возврата страницы входа пользователя методом {@code getLoginPage}
     */
    @Test
    void whenGetLoginPageThenGetPage() {
        var actualResponse = userController.getLoginPage();

        assertThat(actualResponse).isEqualTo("users/login");
    }

    /**
     * Проверяет успешный сценарий входа пользователя методом {@code loginUser}
     */
    @Test
    void whenLoginUserThenGetPageAndSessionAttribute() {
        var user = new User(0, "testLogin", "testPassword", "testName");
        var stringArgumentCaptorLogin = ArgumentCaptor.forClass(String.class);
        var stringArgumentCaptorPassword = ArgumentCaptor.forClass(String.class);
        when(userService.findByLoginAndPassword(stringArgumentCaptorLogin.capture(),
                stringArgumentCaptorPassword.capture())).thenReturn(Optional.of(user));
        var stringArgumentCaptorSession = ArgumentCaptor.forClass(String.class);
        var userArgumentCaptorSession = ArgumentCaptor.forClass(User.class);
        doNothing().when(session).setAttribute(stringArgumentCaptorSession.capture(),
                userArgumentCaptorSession.capture());
        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);

        var actualResponse = userController.loginUser(user, model, request);
        var actualLogin = stringArgumentCaptorLogin.getValue();
        var actualPassword = stringArgumentCaptorPassword.getValue();
        var actualSessionAttribute = stringArgumentCaptorSession.getValue();
        var actualSessionUser = userArgumentCaptorSession.getValue();

        assertThat(actualResponse).isEqualTo("redirect:/");
        assertThat(actualLogin).isEqualTo(user.getLogin());
        assertThat(actualPassword).isEqualTo(user.getPassword());
        assertThat(actualSessionAttribute).isEqualTo("user");
        assertThat(actualSessionUser).isEqualTo(user);
    }

    /**
     * Проверяет неуспешный сценарий входа пользователя методом {@code loginUser}
     */
    @Test
    void whenLoginUserUnSuccessfulThenGetErrorMessage() {
        var user = new User(0, "testLogin", "testPassword", "testName");
        when(userService.findByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);

        var actualResponse = userController.loginUser(user, model, request);

        assertThat(actualResponse).isEqualTo("users/login");
        assertThat(model).hasFieldOrPropertyWithValue("error", "Логин или пароль введены неверно");
    }

    /**
     * Проверяет сценарий выхода пользователя методом {@code logout}
     */
    @Test
    void whenLogoutThenGetPageAndCallInvalidate() {
        var actualResponse = userController.logout(session);

        verify(session, times(1)).invalidate();
        assertThat(actualResponse).isEqualTo("redirect:/");
    }
}