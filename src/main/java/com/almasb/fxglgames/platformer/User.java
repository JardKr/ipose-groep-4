package com.almasb.fxglgames.platformer;

public class User {
    private String naam;
    private String tijd;

    public User(String naam, String tijd) {
        this.naam = naam;
        this.tijd = tijd;
    }

    public String getNaam() {
        return naam;
    }

    public String getTijd() {
        return tijd;
    }
}
