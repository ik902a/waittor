package by.klihal.waittor.controller;

import by.klihal.waittor.dto.TorDto;
import by.klihal.waittor.model.TorrentType;
import by.klihal.waittor.service.TorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class TorController {

    private final TorService torService;

    public TorController(TorService torService) {
        this.torService = torService;
    }

    @GetMapping("/tors")
    public String showPage(Model model) {
        List<TorDto> torDos = torService.findAllDto();
        model.addAttribute("tors", torDos);
        return "index";
    }

    @PostMapping("/tors/add")
    public String addTor(@RequestParam String name,
                         @RequestParam String dateRaw,
                         @RequestParam TorrentType type,
                         Model model) {
        TorDto tor = new TorDto(name, type, Optional.ofNullable(dateRaw).map(LocalDate::parse).orElse(null));
        torService.save(tor);

        model.addAttribute("tors", torService.findAll());

        // 3. Возвращаем ТУ ЖЕ страницу, но указываем имя фрагмента
        // Spring вернет только HTML код таблицы из файла users-list.html
        return "index :: tor-table";
    }
}
