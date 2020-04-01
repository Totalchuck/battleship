package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.stream.Location;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

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


    // @Embeddable
    //    public class Location {
    //        public Location() {
    //            List<String> Location = getLocation();
    //        }
    //        public List<String>;
    //    }



    public Ship() {
    }

    public Ship(String shipType, String alphaNumLocation, boolean horizontal ) {
        this.shipType = shipType;
        List<String> grid = listGrid();
        int location = grid.indexOf(alphaNumLocation);
        this.location.add(grid.get(location));
        if (horizontal && location > 10 && location < 90) {
            this.location.add(grid.get(location-10));
            this.location.add(grid.get(location+10));
        } else {
            this.location.add(grid.get(location-1));
            this.location.add(grid.get(location+1));
        }


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




