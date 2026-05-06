package by.klihal.waittor.controller;

import by.klihal.waittor.security.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response) {
        // 1. Проверяем пользователя через БД (UserRepository)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 2. Если пароль верный:
        String token = jwtUtils.generateToken(username, List.of("ROLE_ADMIN"));

        // 3. Создаем куки с токеном
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true); // Защита от XSS (JS не сможет прочитать куку)
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 24 часа
        response.addCookie(cookie);

        return "redirect:tors";
    }
}
