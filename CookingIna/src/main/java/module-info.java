module com.example.cookingina {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.cookingina to javafx.fxml;
    exports com.example.cookingina;
}