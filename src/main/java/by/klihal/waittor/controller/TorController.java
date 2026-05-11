package by.klihal.waittor.controller;

import by.klihal.waittor.dto.TorDto;
import by.klihal.waittor.service.TorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/tors")
public class TorController {

    private final TorService torService;

    public TorController(TorService torService) {
        this.torService = torService;
    }

    @GetMapping
    public Mono<String> showPage(Model model) {
        Flux<TorDto> torDos = torService.findAll();
        model.addAttribute("tors", torDos);
        return Mono.just("index");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> addTor(@Valid @ModelAttribute("torDto") TorDto tor,
                         Model model) {
        torService.save(tor).subscribe();

        model.addAttribute("tors", torService.findAll());
        return Mono.just("index :: tor-table");
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody // <-- Обязательно добавьте это!
    public Mono<String> deleteTor(@PathVariable Long id, Model model) {
        torService.delete(id).subscribe();

        model.addAttribute("tors", torService.findAll());
        return Mono.just("");
    }
}
