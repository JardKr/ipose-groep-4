package com.almasb.fxglgames.platformer;

public class User {
    private String name;
    private double highscore = Double.MAX_VALUE;
    private double currentScore = Double.MAX_VALUE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHighscore() {
        return highscore;
    }

    public void setHighscore(double highscore) {
        this.highscore = highscore;
    }

    public double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(double currentScore) {
        this.currentScore = currentScore;
    }
}
