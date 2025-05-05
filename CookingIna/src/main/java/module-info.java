module com.example.cookingina {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires annotations;
    requires com.fasterxml.jackson.core;
    requires java.desktop;

    opens com.example.cookingina to javafx.fxml;
    opens assets.textures;
    exports com.example.cookingina;
    exports com.example.cookingina.control;
    opens com.example.cookingina.control to javafx.fxml;
    exports customers;
    opens customers to javafx.fxml;
}