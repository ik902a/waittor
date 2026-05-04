package by.klihal.waittor.controller;

import by.klihal.waittor.dto.TorDto;
import by.klihal.waittor.service.TorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tors")
public class TorController {

    private final TorService torService;

    public TorController(TorService torService) {
        this.torService = torService;
    }

    @GetMapping
    public String showPage(Model model) {
        List<TorDto> torDos = torService.findAllDto();
        model.addAttribute("tors", torDos);
        return "index";
    }

    @PostMapping("/add")
    public String addTor(@Valid @ModelAttribute("torDto") TorDto tor,
                         BindingResult bindingResult,
                         Model model) {
        torService.save(tor);

        model.addAttribute("tors", torService.findAll());
        // 3. Возвращаем ТУ ЖЕ страницу, но указываем имя фрагмента
        // Spring вернет только HTML код таблицы из файла users-list.html
        return "index :: tor-table";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody // <-- Обязательно добавьте это!
    public String deleteTor(@PathVariable Long id, Model model) {
        torService.delete(id);

        model.addAttribute("tors", torService.findAll());
        return "";
    }
}
