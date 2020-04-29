package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name="native", strategy="native")
    private long Id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoLocations")
    private List<String> salvoLocations = new ArrayList<>();



    private int turnNumber;



    public Salvo() {

    this.turnNumber = 0;

    }

    public Salvo(List<String> location, int turn) {

        this.turnNumber = turn;
        this.salvoLocations = location;

    }

    @JsonIgnore
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }





    public void fireSalvo (String alphaNumLocation) {
        List<String> grid = listGrid();
        int location = grid.indexOf(alphaNumLocation);
        this.salvoLocations.add(grid.get(location));
        this.turnNumber++;

    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    //Create an Array with the different grid locations
    public List<String> listGrid() {
        List<String> gridLocation = new ArrayList<>();
        String[] alpha = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        int[] num = {1,2,3,4,5,6,7,8,9,10};
        for (int i=0; i< alpha.length; i++ ) {
            for (int j=0; j<num.length; j++ )
                gridLocation.add(alpha[i] + num[j]);
        }
        return gridLocation;
    }
}
