package com.almasb.fxglgames.platformer;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FontFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class LevelEndScene extends SubScene {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 250;

    private Text textUserTime = getUIFactoryService().newText("", Color.WHITE, 24.0);

    private FontFactory levelFont = getAssetLoader().loadFont(getSettings().getFontMono());
    private BooleanProperty isAnimationDone = new SimpleBooleanProperty(true);

    public LevelEndScene() {
        var bg = new Rectangle(WIDTH, HEIGHT, Color.color(0, 0, 0, 0.85));
        bg.setStroke(Color.BLUE);
        bg.setStrokeWidth(1.75);
        bg.setEffect(new DropShadow(28, Color.color(0,0,0, 0.9)));


        var textContinue = getUIFactoryService().newText("Tap to continue", Color.WHITE, 11.0);

        var vbox = new VBox(15, textUserTime,  textContinue);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25));

        var root = new StackPane(
                bg, vbox
        );

        root.setTranslateX(1280 / 2 - WIDTH / 2);
        root.setTranslateY(720 / 2 - HEIGHT / 2);

        getContentRoot().getChildren().addAll(root);

        getInput().addAction(new UserAction("Close Level End Screen") {
            @Override
            protected void onActionBegin() {
                if (!isAnimationDone.getValue())
                    return;

                getSceneService().popSubScene();
            }
        }, MouseButton.PRIMARY);
    }

    public void onLevelFinish() {
        isAnimationDone.setValue(true);

//        Duration userTime = Duration.seconds(getd("levelTime"));
        //textUserTime.setText(String.format("Your time: %.2f sec!", userTime.toSeconds()));




        getSceneService().pushSubScene(this);
    }


}
