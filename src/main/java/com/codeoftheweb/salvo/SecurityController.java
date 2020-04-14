package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;


@Controller
public class SecurityController {

    @Autowired PlayerRepository playerRepository;

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public Player currentUserName(Principal principal) {

        return playerRepository.findByEmail(principal.getName());
    }
}