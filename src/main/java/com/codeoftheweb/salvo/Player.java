package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.internal.net.http.common.Utils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long Id;
    private String email;
    private String password;


    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> score;





    public Player() {
    }

    public Player(String mail) {
        this.email = mail;
        this.password = "not defined";



    }

    public Player(String mail, String password) {
        this.email = mail;
        this.password = passwordEncoder().encode(password) ;


    }

    private PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//@Jsonignogre
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }



    @JsonIgnore
    public List<Game> getGames() {

        return gamePlayers.stream().map(sub -> sub.getGame()).collect(Collectors.toList());
    }

    public Set<Score> getScore() {
        return score;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}



