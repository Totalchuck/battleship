package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;



@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvos;






    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Ship> ships;

    @ElementCollection
    @Column(name="salvoHitLocations")
    private List<String> salvoHitLocations = new ArrayList<>();

    @ElementCollection
    @Column(name="opponentShipsSunk")
    private List<String> opponentShipsSunk = new ArrayList<>();

    private boolean gameEnd;

    private boolean gameWon;

    private int turnGame;

    private int remainingShips;

    private String opponentName;




    public GamePlayer() {
        this.turnGame = 0;
        this.remainingShips = 5;
    }

    public GamePlayer(Player player, Game game) {

        this.time = new Date();
        this.game = game;
        this.player = player;

        this.turnGame = 0;
        this.gameEnd = false;
        this.gameWon = false;
        this.remainingShips = 5;
        this.opponentName = null;




    }

    public long getId() {
        return id;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @JsonIgnore
    public Game getGame() {

        return game;
    }


    public long getGameId() {
        return game.getId();
    }



    public String getPlayerMail() {
        return player.getEmail();
    }

    public String getOpponentName() {
        return game.getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId() != getId()).collect(Collectors.toList()).get(0).player.getEmail();
    }

    public Set<Ship> getShips() {
        return this.ships;
    }




    public void setGame(Game game) {
        this.game = game;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }


    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void shipSunk () {
        this.remainingShips --;
    }

    public void setRemainingShips(int remainingShips){
        this.remainingShips = remainingShips;
    }

    public int getTurnGame() {
        return this.turnGame;
    }

    public void incrementTurnGame() {
        this.turnGame++;
    }

    public void setTurnGame(int turnGame) {
        this.turnGame = turnGame;
    }

    public void addSalvoHit (String location) {
        salvoHitLocations.add(location);
    }

    public List<String> getSalvoHitLocations() {
        return salvoHitLocations;
    }

    public List<String> getOpponentShipsSunk() {
        return opponentShipsSunk;
    }

    public boolean getGameEnd() {
        return gameEnd;
    }

    public boolean getGameWon() {
        return gameWon;
    }

    public int getOpponentShipsSunkSize() {
        return opponentShipsSunk.size();
    }

    public void endGame() {
        this.gameEnd = true;

    }

    public void wonGame() {
        this.gameWon=true;
    }

    public int getRemainingShips() {
        return this.remainingShips;
    }





}






