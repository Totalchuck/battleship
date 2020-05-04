package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date time;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> score;

    public boolean gameOver;


    public Game() {
        this.gameOver = false;
        this.time = new Date();
    }

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }


    @JsonIgnore
     public Set<GamePlayer> getGamePlayers() {
          return gamePlayers;
     }


    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);

    }



@JsonIgnore
     public List <Player> getPlayers() {
         return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(Collectors.toList());
      }

    @JsonIgnore
    public List <String> getPlayersName() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(Collectors.toList()).stream().map(sub2 -> sub2.getEmail()).collect(Collectors.toList());
    }

    public Set<Score> getScore() {
        return score;
    }

    public boolean getGameOver () {
        return this.gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}

