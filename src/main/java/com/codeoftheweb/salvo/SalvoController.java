package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;


    @RequestMapping("/games_view")
    public List<Game> getAll() {
        return gameRepository.findAll();
    }


    @RequestMapping("/games_view/{gamesId}")
    public Game getGame(@PathVariable int gamesId) {
        return gameRepository.findAll().get(gamesId - 1);
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


    @RequestMapping("/players_view")
    public List<Player> getAllPlayers() {

        return playerRepository.findAll();
    }

    //Create new player in the database
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createPlayer(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.FORBIDDEN);
        }
        Player playerMail = playerRepository.findByEmail(email);
        if (playerMail != null) {
            return new ResponseEntity<>("name already used", HttpStatus.CONFLICT);
        }

        playerRepository.save(new Player(email, password));
        return new ResponseEntity<>("name added", HttpStatus.CREATED);
    }

    //Create new Game
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<String> createGame(@RequestParam String email) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        }
        Game newGame = new Game();
        gameRepository.save(newGame);

        GamePlayer newGP = new GamePlayer(playerRepository.findByEmail(email), newGame);
        gamePlayerRepository.save(newGP);

        Score newScore = new Score(0);
        scoreRepository.save(newScore);

        newScore.setGame(newGame);
        newScore.setPlayer(playerRepository.findByEmail(email));

        return new ResponseEntity<>("game added", HttpStatus.CREATED);

    }

    //Join an existing game
    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<String> joinExistingGame(@RequestParam String email, @PathVariable int gameId) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        }

        GamePlayer newGamePlayer = new GamePlayer(playerRepository.findByEmail(email), gameRepository.findAll().get(gameId - 1));
        gamePlayerRepository.save(newGamePlayer);


        return new ResponseEntity<>("game joined", HttpStatus.CREATED);

    }

    //add a ship with its locations to a gamePlayer
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<String> placeShips(@RequestParam String email, @PathVariable int gamePlayerId, @RequestParam String shipType, @RequestParam List<String> location, @RequestParam boolean horizontal) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findAll().get(gamePlayerId) == null) {
            return new ResponseEntity<>("This GamePlayer Id does not exist", HttpStatus.UNAUTHORIZED);
        }

        Ship newShip = new Ship(shipType, location);

        newShip.setGamePlayer(gamePlayerRepository.findAll().get(gamePlayerId));

        shipRepository.save(newShip);
        return new ResponseEntity<>("Ship added", HttpStatus.CREATED);

    }

    //Add a salvo with its locations to a gamePlayer
    @RequestMapping(path = "/games/players/{gamesId}/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<String> placeSalvos(@RequestParam String email, @PathVariable long gamePlayerId, @PathVariable long gamesId, @RequestParam List<String> locations) {
        GamePlayer myOpponent = getOppositeGamePlayer(gamesId, gamePlayerId);
        GamePlayer thePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        List listSalvosHit = new ArrayList();

        List<String> shipsSunk = new ArrayList<>();

        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findById(gamePlayerId) == null) {
            return new ResponseEntity<>("This GamePlayer Id does not exist", HttpStatus.UNAUTHORIZED);
        } else if (/*myOpponent.getTurnGame() == thePlayer.getTurnGame()*/ true){


            Salvo newSalvo = new Salvo(locations, thePlayer.getTurnGame());

            newSalvo.setGamePlayer(thePlayer);
            thePlayer.incrementTurnGame();

            gamePlayerRepository.save(thePlayer);

            locations.forEach(location -> myOpponent.getShips().forEach(ship -> ship.getLocation().forEach(shipLocation -> {
                if (location.equals(shipLocation)) {

                    thePlayer.addSalvoHit(location);
                    listSalvosHit.add(location);
                    ship.isTheShipSunk(thePlayer.getSalvoHitLocations());
                }
            })));

            thePlayer.getOpponentShipsSunk().clear();
            myOpponent.setRemainingShips(5);
            myOpponent.getShips().forEach(ship->
            {if (ship.getSunk() == true) {
                myOpponent.shipSunk();
                ship.getLocation().forEach(location ->
                { thePlayer.getOpponentShipsSunk().add(location);

                });}});


            if(thePlayer.getOpponentShipsSunk().size() == 5) {
                thePlayer.wonGame();
                thePlayer.endGame();
            } else if (thePlayer.getRemainingShips() ==0) {
                thePlayer.endGame();
            }

            salvoRepository.save(newSalvo);
            gamePlayerRepository.save(thePlayer);
            gamePlayerRepository.save(myOpponent);

            return new ResponseEntity<>("S" + newSalvo.getSalvoLocations(), HttpStatus.CREATED);
        }
        else {

            return new ResponseEntity<>("wait for the opponent to fire", HttpStatus.FORBIDDEN);
        }




    }


    @RequestMapping("/gamePlayer/{gamesId}/{gamePlayerId}/opponent")
    public GamePlayer getOppositeGamePlayer(@PathVariable long gamesId, @PathVariable long gamePlayerId) {
        Optional<Game> myGame = gameRepository.findById(gamesId);
        List<GamePlayer> myOpponent = myGame.get().getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId() != gamePlayerId).collect(Collectors.toList());

        return myOpponent.get(0);
    }


}































