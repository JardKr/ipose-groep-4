module com.miguel.iposegame {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.miguel.iposegame to javafx.fxml;
    exports com.miguel.iposegame;
    opens assets.textures;
    opens assets.sounds;

}
