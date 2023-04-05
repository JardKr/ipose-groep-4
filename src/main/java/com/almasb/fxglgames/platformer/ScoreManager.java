package com.almasb.fxglgames.platformer;

import java.util.HashMap;
import java.util.Map;

public class ScoreManager {
    private User user;
    private Map<String, Double> allScores = new HashMap<String, Double>();

    public ScoreManager() {
        this.loadScores();
    }

    public void setScore(double timeScore) {
        this.user.setCurrentScore(timeScore);
    }

    public double getScore() {
        return this.user.getCurrentScore();
    }

    public boolean saveScore() {
        // Return true if new score is highscore
        double currentScore = this.user.getCurrentScore();
        this.allScores.put(this.user.getName(), currentScore);

        if (this.user.getHighscore() > currentScore) {
            this.user.setHighscore(currentScore);

            return true;
        }

        return false;
    }

    public void loadScores() {

    }


    public Map<String, Double> getAllScores() {
        return this.allScores;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
