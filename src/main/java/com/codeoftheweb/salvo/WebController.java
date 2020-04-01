package com.codeoftheweb.salvo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    @RequestMapping("/web")
    public String games(@RequestParam("games.html") String game, Model model) {
        model.addAttribute("game", game);
        return "games.html";
    }


    public String game(@RequestParam("game.html") String game, Model model) {
        model.addAttribute("game", game);
        return "game.html";
    }
}
