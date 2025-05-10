package com.example.cookingina;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RegisterScene extends SubScene {

    public RegisterScene() {
        super();

        Text title = new Text("Register New Account");

        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> {
            getSceneService().pushSubScene(new LoginScene());
        });

        VBox layout = new VBox(20, title, backButton);
        layout.setTranslateX(300);
        layout.setTranslateY(200);

        getRoot().getChildren().add(layout);
    }
}
