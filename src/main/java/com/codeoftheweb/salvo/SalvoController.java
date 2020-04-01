package com.codeoftheweb.salvo;



    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    import java.util.List;
    import java.util.Optional;


@RestController
    @RequestMapping("/api")
    public class SalvoController {


        @Autowired
        private GameRepository gameRepository;


        @RequestMapping("/games_view")
        public List<Game> getAll() {
            return gameRepository.findAll();
            }


        @RequestMapping("/games_view/{gamesId}")
        public Game getGame(@PathVariable int gamesId) {
          return gameRepository.findAll().get(gamesId-1);
        }


        @Autowired
        private GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/gamePlayer_view/{gamesId}")
    public Optional<GamePlayer> getGamePlayer(@PathVariable long gamesId) {
        return gamePlayerRepository.findById(gamesId);
    }


    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/players_view")
    public List<Player> getAllPlayers()  {
        return playerRepository.findAll();
    }










}








