package com.codeoftheweb.salvo;



    import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.*;

    import java.security.Principal;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;
    import java.util.Set;


@RestController
    @RequestMapping("/api")
    public class SalvoController {


        @Autowired
        private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;


        @RequestMapping("/games_view")
        public List<Game> getAll() {
            return gameRepository.findAll();
            }


        @RequestMapping("/games_view/{gamesId}")
        public Game getGame(@PathVariable int gamesId) {
          return gameRepository.findAll().get(gamesId-1);
        }




         @RequestMapping("/gamePlayer_view")
            public List<GamePlayer> getGamePlayers() {

            return gamePlayerRepository.findAll();
            }

        @RequestMapping("/gamePlayer_view/{gamesId}")
        public Optional<GamePlayer> getGamePlayer(@PathVariable long gamesId) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        Player myUser = currentUserName(principal);



        return gamePlayerRepository.findById(gamesId);
    }




   @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public Player currentUserName(Principal principal) {

        return playerRepository.findByEmail(principal.getName());
    }

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping("/players_view")
    public List<Player> getAllPlayers()  {

        return playerRepository.findAll();
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createPlayer (@RequestParam String email, @RequestParam String password) {
           if (email.isEmpty()) {
               return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
           }
           Player playerMail = playerRepository.findByEmail(email);
           if (playerMail !=  null) {
               return new ResponseEntity<>("name already used", HttpStatus.CONFLICT);
           }

            playerRepository.save(new Player (email, password));
            return new ResponseEntity<>("name added", HttpStatus.CREATED);

    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<String> createGame (@RequestParam String email) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        }
        Game newGame = new Game();
        gameRepository.save(newGame);

        GamePlayer newGP = new GamePlayer( playerRepository.findByEmail(email), newGame);
        gamePlayerRepository.save(newGP);

        Score newScore = new Score(0);
        scoreRepository.save(newScore);

        newScore.setGame(newGame);
        newScore.setPlayer(playerRepository.findByEmail(email));

        return new ResponseEntity<>("game added", HttpStatus.CREATED);

    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<String> joinExistingGame (@RequestParam String email, @PathVariable int gameId) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        }

        GamePlayer newGamePlayer = new GamePlayer( playerRepository.findByEmail(email), gameRepository.findAll().get(gameId-1));
        gamePlayerRepository.save(newGamePlayer);


        return new ResponseEntity<>("game joined", HttpStatus.CREATED);

    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<String> placeShips (@RequestParam String email, @RequestParam String shipType, @PathVariable int gamePlayerId) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        }
        else if (gamePlayerRepository.findAll().get(gamePlayerId) == null) {
            return new ResponseEntity<>("This GamePlayer Id does not exist", HttpStatus.UNAUTHORIZED);
        }

        Ship newShip = new Ship(shipType, "G4", true);
        shipRepository.save(newShip);
              Player myFourthPlayer = new Player("testtest@mail.com", "leylapass");
        playerRepository.save(myFourthPlayer);

        Ship myFirstCruiser = new Ship("patrol", "B5", true);
        Ship myFirstDestroyer = new Ship("stuff","G6", false);


        GamePlayer newGP = new GamePlayer(myFourthPlayer, gameRepository.findAll().get(gamePlayerId));
        gamePlayerRepository.save(newGP);
        newShip.setGamePlayer(newGP);
        myFirstCruiser.setGamePlayer(newGP);
        myFirstDestroyer.setGamePlayer(newGP);
        
        shipRepository.save(myFirstCruiser);
        shipRepository.save(myFirstDestroyer);


        return new ResponseEntity<>("game added", HttpStatus.CREATED);

    }



}































