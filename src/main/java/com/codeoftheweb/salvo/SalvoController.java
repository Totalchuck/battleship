package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public Game getGame(@PathVariable long gamesId) {

        return gameRepository.getOne(gamesId);
    }


    @RequestMapping("/gamePlayer_view")
    public List<GamePlayer> getGamePlayers() {
        return gamePlayerRepository.findAll();
    }

    @RequestMapping("/gamePlayer_view/{gamesId}")
    public Optional<GamePlayer> getGamePlayer(Authentication authentication, @PathVariable long gamesId) {

        GamePlayer myGamePlayer = gamePlayerRepository.findById(gamesId).get();

        if (myGamePlayer != null) {
            if (authentication.getName() == myGamePlayer.getPlayer().getEmail()) {
                return gamePlayerRepository.findById(gamesId);
            } else {
                return null;
            }

        } else {
            return null;
        }

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
    public ResponseEntity<String> joinExistingGame(Authentication authentication, @PathVariable long gameId) {
        if (authentication == null) {
            return new ResponseEntity<>("You should be logged in to enter the game", HttpStatus.UNAUTHORIZED);
        }

        Game game = gameRepository.getOne(gameId);

        if (game == null) {
            return new ResponseEntity<>("This game doesn't exist", HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByEmail(authentication.getName());

        GamePlayer gamePlayer = game.getGamePlayers().stream().filter(gP -> gP.getPlayer().getEmail() == authentication.getName()).findFirst().orElse(null);

        if (game.getGamePlayers().size() == 2) {
            return new ResponseEntity<>("Game is full", HttpStatus.UNAUTHORIZED);
        }

        if (game.getGamePlayers().size() == 1 && gamePlayer != null ) {
            return new ResponseEntity<>("You already in the game", HttpStatus.UNAUTHORIZED);
        }

        GamePlayer newGamePlayer = new GamePlayer(player, game);
        gamePlayerRepository.save(newGamePlayer);


        return new ResponseEntity<>("game joined", HttpStatus.CREATED);

    }

    //add a ship with its locations to a gamePlayer
    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<String> placeShips(@RequestParam String email, @PathVariable long gamePlayerId, @RequestParam String shipType, @RequestParam List<String> location, @RequestParam boolean horizontal) {
        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findById(gamePlayerId) == null) {
            return new ResponseEntity<>("This GamePlayer Id does not exist", HttpStatus.UNAUTHORIZED);
        }

        Ship newShip = new Ship(shipType, location, horizontal);

        newShip.setGamePlayer(gamePlayerRepository.findById(gamePlayerId).get());

        shipRepository.save(newShip);
        return new ResponseEntity<>("Ship added", HttpStatus.CREATED);

    }

    //Add a salvo with its locations to a gamePlayer
    @RequestMapping(path = "/games/players/{gamesId}/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<String> placeSalvos(@RequestParam String email, @PathVariable long gamePlayerId, @PathVariable long gamesId, @RequestParam List<String> locations) {
        GamePlayer myOpponent = getOppositeGamePlayer(gamesId, gamePlayerId);
        GamePlayer thePlayer = gamePlayerRepository.findById(gamesId).get();

        List listSalvosHit = new ArrayList();

        List<String> shipsSunk = new ArrayList<>();
        System.out.println(Math.sqrt(Math.pow(thePlayer.getOpponentTurn() - thePlayer.getTurnGame(),2) ));

        if (email.isEmpty()) {
            return new ResponseEntity<>("No name given", HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findById(gamePlayerId) == null) {
            return new ResponseEntity<>("This GamePlayer Id does not exist", HttpStatus.UNAUTHORIZED);
        } else if ((thePlayer.getOpponentShipsSunk().size() != 17 || thePlayer.getRemainingShips() != 0) && thePlayer.getOpponentTurn() >= thePlayer.getTurnGame()){


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

            //add the opponent sunk ship to an array in the class gamePlayer
            thePlayer.getOpponentShipsSunk().clear();

            myOpponent.setRemainingShips(5);

            myOpponent.getShips().forEach(ship->
            {if (ship.getSunk() == true) {
                myOpponent.shipSunk();
                ship.getLocation().forEach(location ->
                    { thePlayer.getOpponentShipsSunk().add(location);

                });}});

            //check if the Player won the game of tie and end the game

            if (thePlayer.getOpponentShipsSunk().size() == 17 && thePlayer.getTurnGame() == myOpponent.getTurnGame() || thePlayer.getRemainingShips() == 0 && thePlayer.getTurnGame() == myOpponent.getTurnGame()) {

                if(thePlayer.getOpponentShipsSunk().size() == 17 && thePlayer.getRemainingShips() == 0) {
                    thePlayer.tieGame();
                    myOpponent.tieGame();
                    gameRepository.getOne(gamesId).setGameOver(true);
                    Score tie = new Score(0.5);
                    Score tieOpp = new Score(0.5);
                    tieOpp.setPlayer(myOpponent.getPlayer());
                    tie.setPlayer(thePlayer.getPlayer());
                    scoreRepository.save(tie);
                    scoreRepository.save(tieOpp);
                    //check if the Player has no remaining ship and the game is over
                } else if(thePlayer.getRemainingShips() == 0 ) {
                    gameRepository.getOne(gamesId).setGameOver(true);
                } else if (thePlayer.getOpponentShipsSunk().size() == 17 ) {
                    thePlayer.wonGame();
                    gameRepository.getOne(gamesId).setGameOver(true);
                    Score won = new Score(1);
                    won.setPlayer(thePlayer.getPlayer());
                    scoreRepository.save(won);
                }
            }


          


            gameRepository.save(gameRepository.getOne(gamesId));
            salvoRepository.save(newSalvo);
            gamePlayerRepository.save(thePlayer);
            gamePlayerRepository.save(myOpponent);

            return new ResponseEntity<>("S" + newSalvo.getSalvoLocations(), HttpStatus.CREATED);
        }
        else {

            return new ResponseEntity<>("wait for the opponent to fire", HttpStatus.FORBIDDEN);
        }




    };

    //function to look for the opposite gamePlayer
    @RequestMapping("/gamePlayer/{gamesId}/{gamePlayerId}/opponent")
    public GamePlayer getOppositeGamePlayer(@PathVariable long gamesId, @PathVariable long gamePlayerId) {
        Optional<Game> myGame = gameRepository.findById(gamesId);
        List<GamePlayer> myOpponent = myGame.get().getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId() != gamePlayerId).collect(Collectors.toList());

        return myOpponent.get(0);
    }


   public Player loggedUser(){
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        Player myUser = currentUserName(principal);
        return myUser;
    }

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public Player currentUserName(Principal principal) {

        return playerRepository.findByEmail(principal.getName());
    }


}































