package by.klihal.waittor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class AuthController {

    @GetMapping("/login")
    public Mono<String> showLoginForm() {
        System.out.println("LOGIN");
        return Mono.just("login");
    }
}
