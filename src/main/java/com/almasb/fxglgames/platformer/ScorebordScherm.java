package com.almasb.fxglgames.platformer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollToEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

public class ScorebordScherm extends StackPane {
    ScoreManager scoreManager;

    public ScorebordScherm(ScoreManager scoreManeger){
        this.scoreManager = scoreManager;

        Text title = new Text("Scoreboard");
        title.setFill(Color.BLACK);
        title.setStyle("-fx-font-size: 40px; -fx-text-aligment: center;");
        setPrefSize(800,800);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        for (Map.Entry<String, Double> entry: scoreManeger.getAllScores().entrySet()){
            String key = entry.getKey();
            Double value = entry.getValue();

            vBox.getChildren().add(new Label(key + " - " + value + "s"));
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(600);
        scrollPane.setMaxWidth(400);
        scrollPane.setContent(vBox);

        this.setStyle("-fx-background-color: linear-gradient(#0052d4,#4364f7,#6fb1fc);" +
                "-fx-background-radius: 5;");

        getChildren().addAll(title, scrollPane);
    }
}
