module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
    opens assets.sounds;
    opens assets.textures;
}