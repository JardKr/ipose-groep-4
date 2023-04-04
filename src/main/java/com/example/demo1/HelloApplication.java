package com.example.demo1;



import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import com.almasb.fxgl.core.math.Vec2;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class HelloApplication extends GameApplication{
    private Entity player;
    private boolean loggedIn = false;

    @Override
    protected void initSettings(GameSettings settings){
        settings.setWidth(800);
        settings.setHeight(800);
        settings.setTitle("hello world");
        settings.setVersion("1.0");
    }


    @Override
    protected void initGame(){
        if (!loggedIn) {
            // Show login screen
            Entity loginText = new Entity();
            loginText.addComponent(new Text("Please log in."));
            loginText.setPosition(new Vec2(getWidth() / 2, getHeight() / 2));
            getGameWorld().addEntity(loginText);
        } else {
            player = FXGL.entityBuilder()
                .at(400,400)
                .viewWithBBox( "brick.png")
                .with(new CollidableComponent(true))
                .type(EntityTypes.PLAYER)
                .buildAndAttach();

            FXGL.getGameTimer().runAtInterval(() ->{
                int randomPos1 = ThreadLocalRandom.current().nextInt(80,FXGL.getGameScene().getAppWidth() - 80);
                int randomPos2 = ThreadLocalRandom.current().nextInt(80,FXGL.getGameScene().getAppWidth() - 80);
                FXGL.entityBuilder()
                        .at(randomPos1,randomPos2)
                        .viewWithBBox(new Circle(5,Color.WHITE))
                        .with(new CollidableComponent(true))
                        .type(EntityTypes.STAR)
                        .buildAndAttach();
            }, Duration.millis(2000));
        }
        
    }

    @Override
    protected void initInput(){

    Input input = getInput();

    // Define a user action to handle login button press
    input.addAction(new UserAction("login") {
        @Override
        protected void onActionBegin() {
            if (!loggedIn) {
                // Set loggedIn to true and reload the game
                loggedIn = true;
                getGameController().restartGame();
            }
        }
    }, KeyCode.ENTER); // Bind to enter key for simplicity
        FXGL.onKey(KeyCode.D, () -> {
            player.translateX(5);
        });

        FXGL.onKey(KeyCode.A, () -> {
            player.translateX(-5);
        });

        FXGL.onKey(KeyCode.W, () -> {
            player.translateY(-5);
        });

        FXGL.onKey(KeyCode.S, () -> {
            player.translateY(5);
        });

        FXGL.onKeyDown(KeyCode.F, () ->{
            FXGL.play("pipe.wav");

            FXGL.inc("kills", +1);
        });


    }

    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.STAR) {
            @Override
            protected void onCollision(Entity player, Entity star) {
                star.removeFromWorld();
                FXGL.play("fxgl-samples_src_main_resources_assets_sounds_drop.wav");
                FXGL.inc("kills", +1);

            }

        });
    }

    @Override
    protected void initUI() {
        Label myText = new Label("hello there");
        myText.setStyle("-fx-text-fill: yellow");
        myText.setTranslateX(50);
        myText.setTranslateY(50);
        myText.textProperty().bind(FXGL.getWorldProperties().intProperty("kills").asString());

        FXGL.getGameScene().setBackgroundColor(Color.BLACK);
        FXGL.getGameScene().addUINodes(myText);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("kills",0);
    }



    public static void main(String[] args) {
        launch(args);
    }
}
