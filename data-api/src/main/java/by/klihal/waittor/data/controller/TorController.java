package by.klihal.waittor.data.controller;

import by.klihal.waittor.common.dto.TorDto;
import by.klihal.waittor.data.service.DataService;
import by.klihal.waittor.data.service.TorService;
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
    private final DataService dataService;

    public TorController(TorService torService, DataService dataService) {
        this.torService = torService;
        this.dataService = dataService;
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

    @GetMapping("/check")
    @ResponseBody
    public Mono<String> checkTorrents(Model model) {
        dataService.begin();
        return Mono.just("");
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody // <-- Обязательно добавьте это!
    public Mono<String> deleteTor(@PathVariable Long id, Model model) {
        torService.delete(id).subscribe();

        model.addAttribute("tors", torService.findAll());
        return Mono.just("");
    }
}
