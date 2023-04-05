package com.almasb.fxglgames.platformer;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class Trump extends Component {
    private PhysicsComponent physics;

    private AnimatedTexture texture;

    private AnimationChannel animIdle, animWalk;

    private int jumps = 1;

    public Trump() {

        Image image = image("trump.png");

        animIdle = new AnimationChannel(image, 1, 96, 154, Duration.seconds(0), 0, 0);
        animWalk = new AnimationChannel(image, 1, 96, 154, Duration.seconds(0), 0, 0);

        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);

        physics.onGroundProperty().addListener((obs, old, isOnGround) -> {
            if (isOnGround) {
                jumps = 1;
            }
        });
    }


    @Override
    public void onUpdate(double tpf) {
        if (physics.isMovingX()) {
            if (texture.getAnimationChannel() != animWalk) {
                texture.loopAnimationChannel(animWalk);
            }
        } else {
            if (texture.getAnimationChannel() != animIdle) {
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    public void right() {
        getEntity().setScaleX(1);
        physics.setVelocityX(150);
    }

    public void jump() {
        if (jumps == 0)
            return;

        physics.setVelocityY(-700);

        jumps--;
    }


    public void stop() {
        physics.setVelocityX(0);
    }


}
