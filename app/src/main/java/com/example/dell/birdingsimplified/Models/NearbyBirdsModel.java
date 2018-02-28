package com.example.dell.birdingsimplified.Models;

/**
 * Created by Dell on 28-02-2018.
 */

public class NearbyBirdsModel {

    String Location, Bird, Color;

    public NearbyBirdsModel(String state, String name, String color) {
        this.Location = state;
        this.Bird = name;
        this.Color = color;
    }

    public String getState() {
        return this.Location;
    }

    public String getName() {
        return this.Bird;
    }

    public String getColor() {
        return this.Color;
    }

}


