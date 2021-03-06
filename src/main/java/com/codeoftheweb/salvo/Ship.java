package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long Id;
    private String shipType;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="Location")
    private List<String> location = new ArrayList<>();

    private boolean sunk;

    private boolean horizontal;

    private int remainingLife;

    public Ship() {
    }

    public Ship(String shipType) {
        this.shipType = shipType;
    }

    public Ship(String shipType, List<String> location, Boolean horizontal)  {
        this.shipType = shipType;
        this.location = location;
        this.sunk=false;
        this.remainingLife = location.size();
        this.horizontal= horizontal;
    }


    public long getId() {
        return Id;
    }


    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public List<String> getLocation () {
        return location;
    }
@JsonIgnore
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public boolean isTheShipSunk(List<String> salvoLocations) {
       remainingLife = location.size();
        this.location.forEach(location -> salvoLocations.forEach(salvoLocation ->
        {
            if (location.equals(salvoLocation)) {
              remainingLife--;

            }}));

        if (remainingLife == 0) {
            sunk = true;
        }
        return sunk;
    }

    public boolean getHorizontal() {
        return this.horizontal;
    }

    public int getRemainingLife() {
        return remainingLife;
    }

    public boolean getSunk(){
        return sunk;
    }
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




