package com.codeoftheweb.salvo;



import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvos;


    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Ship> ships;






    public GamePlayer () {

    }

    public GamePlayer(Player player, Game game) {

        this.time = new Date();
        this.game = game;
        this.player = player;


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


    @JsonIgnore
    public List<Player> getOpponent() {

        List<Player> myPlayerList = game.getPlayers();

        return game.getPlayers();
    }
    @JsonIgnore
    public Set<GamePlayer> getGBOpponent() {

        return game.getGamePlayers();
    }

    public long getGameId () {
        return game.getId();
    }





    public void setGame(Game game) {
        this.game = game;
    }


      public Player getPlayer() {
         return player;
       }


    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }
}






