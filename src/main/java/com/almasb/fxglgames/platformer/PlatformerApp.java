package com.almasb.fxglgames.platformer;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.platformer.EntityType.*;

public class PlatformerApp extends GameApplication {

    private static final int MAX_LEVEL = 3;
    private static final int STARTING_LEVEL = 0;
    private Label tijdlabel;
    private String playerName;
    private int levelTime;
    private Image logo;
    private Text title;
    private Button startButton;
    private boolean isBegonnen = false;
    private Button exitButton;


    @Override
    protected void initUI(){
        if (!isBegonnen) {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(10);

            Label naamLabel = new Label("Wat is je naam?");
            naamLabel.setFont(new Font(30));
            naamLabel.setAlignment(Pos.CENTER);
            naamLabel.setTextFill(Color.WHITE);

            TextField naamField = new TextField();
            naamField.setFont(new Font(24));

            Button startButton = new Button("Start");
            startButton.setFont(new Font(24));
            startButton.setOnAction(e -> {
                playerName = naamField.getText();
                vbox.setVisible(false);
                isBegonnen = true;
                getGameWorld().addEntityFactory(new PlatformerFactory());

                player = null;
                trump = null;
                nextLevel();

                player = spawn("player", 900, 50);
                trump = spawn("trump", 0, 50);

                set("player", player);
                set("trump", trump);
                spawn("background");

                Viewport viewport = getGameScene().getViewport();
                viewport.setBounds(-1500, 0, 250 * 70, getAppHeight());
                viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);
                viewport.setLazy(true);


            });

            vbox.getChildren().addAll(naamLabel, naamField, startButton);

            getGameScene().addUINode(vbox);
        }

    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Miguel");
        settings.setVersion("1.0");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public LoadingScene newLoadingScene() {
                return new MainLoadingScene();
            }
        });
        settings.setApplicationMode(ApplicationMode.DEVELOPER);


    }

    private LazyValue<LevelEndScene> levelEndScene = new LazyValue<>(() -> new LevelEndScene());
    private Entity player;
    private Entity trump;


    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.A, VirtualButton.LEFT);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.D, VirtualButton.RIGHT);


        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PlayerComponent.class).jump();
            }
        }, KeyCode.W, VirtualButton.A);

        getInput().addAction(new UserAction("Use") {
            @Override
            protected void onActionBegin() {
                getGameWorld().getEntitiesByType(BUTTON)
                        .stream()
                        .filter(btn -> btn.hasComponent(CollidableComponent.class) && player.isColliding(btn))
                        .forEach(btn -> {
                            btn.removeComponent(CollidableComponent.class);

                            Entity keyEntity = btn.getObject("keyEntity");
                            keyEntity.setProperty("activated", true);

                            KeyView view = (KeyView) keyEntity.getViewComponent().getChildren().get(0);
                            view.setKeyColor(Color.RED);

                            makeExitDoor();
                        });
            }
        }, KeyCode.E, VirtualButton.B);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", STARTING_LEVEL);
        vars.put("levelTime", 0.0);
        vars.put("score", 0);
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.25);
        loopBGM("BGM_dash_runner.wav");
    }

    @Override
    protected void initGame() {
        if (isBegonnen){
            player = null;
            trump = null;
            PlatformerFactory platformerFactory = new PlatformerFactory();
            getGameWorld().addEntityFactory(platformerFactory);

            player = spawn("player", 900, 50);
            trump = spawn("trump", 0, 50);

            set("player", player);
            set("trump", trump);
            spawn("background");
        }

        tijdlabel = new Label("TIme: " + levelTime);


        run(() -> {
            levelTime++;
            tijdlabel.setText("Time: " + levelTime);
        }, Duration.seconds(1));
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.MUUR, EntityType.TRUMP) {
            @Override
            protected void onCollision(Entity muur, Entity trump) {
                muur.removeFromWorld();
                System.out.println("print");
            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.WATER, EntityType.PLAYER) {
            @Override
            protected void onCollision(Entity water, Entity player) {
                water.removeFromWorld();
                System.out.println("print");
            }
        });
        getPhysicsWorld().setGravity(0, 760);

        onCollisionOneTimeOnly(PLAYER, DOOR_BOT, (player, door) -> {
            levelEndScene.get().onLevelFinish();

            // the above runs in its own scene, so fade will wait until
            // the user exits that scene
            getGameScene().getViewport().fade(() -> {
                nextLevel();
            });
        });


        onCollisionOneTimeOnly(PLAYER, MESSAGE_PROMPT, (player, prompt) -> {
            prompt.setOpacity(1);

            despawnWithDelay(prompt, Duration.seconds(4.5));
        });



        onCollisionBegin(PLAYER, KEY_PROMPT, (player, prompt) -> {
            String key = prompt.getString("key");

            var entity = getGameWorld().create("keyCode", new SpawnData(prompt.getX(), prompt.getY()).put("key", key));
            spawnWithScale(entity, Duration.seconds(1), Interpolators.ELASTIC.EASE_OUT());

            runOnce(() -> {
                despawnWithScale(entity, Duration.seconds(1), Interpolators.ELASTIC.EASE_IN());
            }, Duration.seconds(2.5));
        });
    }

    private void makeExitDoor() {
        var doorTop = getGameWorld().getSingleton(DOOR_TOP);
        var doorBot = getGameWorld().getSingleton(DOOR_BOT);

        doorBot.getComponent(CollidableComponent.class).setValue(true);

        doorTop.setOpacity(1);
        doorBot.setOpacity(1);
    }

    private void nextLevel() {
        if (geti("level") == MAX_LEVEL){
            FXGL.getGameController().pauseEngine();
            showMessage("Gefeliciteerd je hebt gewonnen!");



            FXGL.getGameScene().setBackgroundColor(Color.LIGHTBLUE);

            FXGL.getGameScene().clearUINodes();



            VBox vbox = new VBox();
            vbox.setPrefWidth(1280);
            vbox.setPrefHeight(720);
            vbox.setAlignment(Pos.CENTER);
// vbox.setSpacing(8);

            Pane borderPane = new Pane();
            borderPane.setPrefWidth(1000);
            borderPane.setPrefHeight(720);
            borderPane.getChildren().add(vbox);
// borderPane.setTranslateX(getApp)Width() / 2 - 20);
// borderPane.setTranslateY(getAppHeight() / 2);

            FXGL.getGameScene().addUINode(borderPane);
            stopSpel();
            showScoreboard();
            return;
        }


        inc("level", +1);

        setLevel(geti("level"));
    }




    @Override
    protected void onUpdate(double tpf) {
        if (isBegonnen) {
            inc("levelTime", tpf);

            if (player.getY() > getAppHeight()) {
                onPlayerDied();
            }

            onCollisionBegin(PLAYER, TRUMP, (player, trump) -> {

                onPlayerDied();

            });
            trump.getComponent(Trump.class).right();
        }
    }

    public void onPlayerDied() {

        showMessage("ded", () ->{
            System.out.println("r");
            setLevel(geti("level"));
//            getPrimaryStage().close();
//            Platform.runLater(() -> new ReloadApp().start(new Stage()));
        });
    }

    private void setLevel(int levelNum) {
        if (player != null) {
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(900, 50));
            trump.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(0, -655));
            player.setZIndex(Integer.MAX_VALUE);
        }


        Level level = setLevelFromMap("tmx/level" + levelNum  + ".tmx");
//          Level level = setLevelFromMap("tmx/level3.tmx");
    }


    public ArrayList<User> leesTakenUitBestand(){
        ArrayList<User> taken = new ArrayList<>();
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(new File("C:\\Users\\koenm\\Documents\\GitHub\\ipose-groep-4\\src\\main\\java\\com\\almasb\\fxglgames\\platformer\\highscores.txt")));
            String regel = bufReader.readLine();
            while (regel != null) {
                String [] taakRegel = regel.split(",");
                User taak = new User(taakRegel[0], taakRegel[1]);
                taken.add(taak);

                regel = bufReader.readLine();
            }
            bufReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found! Error:");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException occured! Error:");
            e.printStackTrace();
        }

        return taken;
    }





    private void showScoreboard() {
// Create scoreboard scene
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(20);

        Label headerLabel = new Label("Game finished!");
        headerLabel.setFont(new Font(36));
        container.getChildren().add(headerLabel);

        Label timeLabel = new Label(playerName + ": " + levelTime + "s");
        timeLabel.setFont(new Font(24));
        for (int i = 0; i < leesTakenUitBestand().size(); i++) {
            System.out.println("hoi");
            ArrayList<User> user = leesTakenUitBestand();
            String stringtijd = user.get(i).getTijd();
            String stringnaam = user.get(i).getNaam();
            Label tijdlabel = new Label(stringtijd);
            Label naamlabel = new Label(stringnaam);
            Label alllabel = new Label(stringnaam, tijdlabel);
            container.getChildren().add(alllabel);
        }

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> {
            FXGL.getGameController().exit();
        });
        container.getChildren().add(exitButton);

        getGameScene().addUINode(container);
    }

    protected void stopSpel() {
        System.out.println("print");
        try (FileWriter writer = new FileWriter("C:\\Users\\koenm\\Documents\\GitHub\\ipose-groep-4\\src\\main\\java\\com\\almasb\\fxglgames\\platformer\\highscores.txt", true)) {
            writer.append(playerName).append(",").append(String.valueOf(levelTime)).append("\n");
        } catch (IOException e) {
            System.err.println("Kon het scorebestand niet schrijven: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
