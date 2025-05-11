package customers;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import com.example.cookingina.CookingInaMain;
import com.example.cookingina.control.UIController;
import com.example.cookingina.database.DatabaseManager;
import com.example.cookingina.session.Session;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpeechBubbleComponent extends Component {

    private final Group bubbleRoot = new Group();
    private final Map<String, ImageView> icons = new HashMap<>();

    private Rectangle patienceBar;
    private double patience = 1.0; // from 1.0 (full) to 0.0 (empty)
    private static final double DURATION_SECONDS = DatabaseManager.getPatienceLevel(Session.getUsername());
    private long startTime;

    public SpeechBubbleComponent(List<Order> orders) {
        double height = 60 + orders.size() * 40;
        double width = 80;
        // background
        Rectangle bg = new Rectangle(width, height);
        bg.setArcWidth(12);
        bg.setArcHeight(12);
        bg.setFill(Color.WHITE);
        bg.setStroke(Color.BLACK);

        double pointerY = bg.getHeight();
        Polygon pointer = new Polygon(
                width / 2 - 5, pointerY,
                width / 2 + 5, pointerY,
                width / 2, pointerY + 15
        );
        pointer.setFill(Color.WHITE);
        pointer.setStroke(Color.BLACK);

        bubbleRoot.getChildren().addAll(bg, pointer);

        // Patience bar background
        Rectangle barBG = new Rectangle(width, 6);
        barBG.setFill(Color.GRAY);
        barBG.setTranslateY(-12);

        // Patience bar foreground
        patienceBar = new Rectangle(width, 6);
        patienceBar.setFill(Color.LIMEGREEN);
        patienceBar.setTranslateY(-12);

        bubbleRoot.getChildren().addAll(barBG, patienceBar);
        startTime = System.currentTimeMillis();

        // Icons for orders
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            String key = o.getItem() + "_" + i;
            Image img = FXGL.image(o.getItem().toLowerCase() + ".png");
            ImageView iv = new ImageView(img);
            iv.setFitWidth(56);
            iv.setFitHeight(56);
            iv.setLayoutX(10);
            iv.setLayoutY(10 + i * 50);
            icons.put(key, iv);
            bubbleRoot.getChildren().add(iv);
        }
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(bubbleRoot);
        double bubbleW = bubbleRoot.getBoundsInLocal().getWidth();
        bubbleRoot.setTranslateX(-bubbleW / 2 + 50);
        bubbleRoot.setTranslateY(entity.getHeight()-300);
    }

    @Override
    public void onUpdate(double tpf) {
        double elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0;
        double remaining  = Math.max(0, DURATION_SECONDS - elapsedSec);
        double progress   = remaining / DURATION_SECONDS;
        patience = progress;

        // Remove customer if patience has fully run out
        if (progress <= 0) {
            // Remove from spawner list
            UIController.components.remove(entity.getComponent(CustomerComponent.class));
            // Remove entity
            entity.removeFromWorld();
            return;
        }

        double width = bubbleRoot.getBoundsInLocal().getWidth();
        patienceBar.setWidth(width * progress);
        patienceBar.setFill(
                progress > 0.5 ? Color.LIMEGREEN :
                        progress > 0.2 ? Color.ORANGE :
                                Color.RED
        );
    }

    /** Removes the icon for a served item and checks if all orders are done */
    public void markServed(String baseItem) {
        // Remove one icon
        String matchKey = icons.keySet().stream()
                .filter(k -> k.startsWith(baseItem + "_"))
                .findFirst().orElse(null);

        if (matchKey != null) {
            ImageView iv = icons.remove(matchKey);
            bubbleRoot.getChildren().remove(iv);
        }

        // If all orders served, remove customer
        if (icons.isEmpty() && entity != null) {
            // Remove from spawner list
            // Optionally play a fade-out
            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), entity.getViewComponent().getParent());
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.play();
            UIController.components.remove(entity.getComponent(CustomerComponent.class));

        }
    }

    /** Shows a popup with earned price based on remaining patience */
    public void showPricePopup(int basePrice) {
        int adjustedPrice = (int) (basePrice * patience);
        FXGL.inc("income", adjustedPrice);

        Text popup = new Text("+" + adjustedPrice);
        popup.setFont(Font.font(32));
        popup.setFill(Color.YELLOW);
        popup.setTranslateX(bubbleRoot.getBoundsInLocal().getWidth() / 2 - 10);
        popup.setTranslateY(-40);
        bubbleRoot.getChildren().add(popup);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.8), popup);
        tt.setByY(-30);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.8), popup);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        tt.play();
        ft.play();
        ft.setOnFinished(e -> bubbleRoot.getChildren().remove(popup));
    }

    /** Shows a deduction popup when losing money */
    public void showDeductionPopup(int amount) {
        FXGL.inc("income", -amount);
        Text popup = new Text("-" + amount);
        popup.setFont(Font.font(32));
        popup.setFill(Color.RED);
        popup.setTranslateX(bubbleRoot.getBoundsInLocal().getWidth() / 2 - 10);
        popup.setTranslateY(-40);
        bubbleRoot.getChildren().add(popup);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.8), popup);
        tt.setByY(-30);
        FadeTransition ft = new FadeTransition(Duration.seconds(0.8), popup);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        tt.play();
        ft.play();
        ft.setOnFinished(e -> bubbleRoot.getChildren().remove(popup));
    }
}
