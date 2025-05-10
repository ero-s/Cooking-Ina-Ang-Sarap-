
package customers;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
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

import static com.almasb.fxgl.dsl.FXGLForKtKt.runOnce;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class SpeechBubbleComponent extends Component {

    private final Group bubbleRoot = new Group();
    private final Map<String, ImageView> icons = new HashMap<>();

    private Rectangle patienceBar;
    private double patience = 1.0; // from 1.0 (full) to 0.0 (empty)
    private static final double DURATION_SECONDS = 20.0;
    private long startTime;
    private int basePrice = 100; // Default base price, can be adjusted per customer


    public SpeechBubbleComponent(List<Order> orders) {
        double height = 60 + orders.size() * 40;
        double width = 80;
        // background
        Rectangle bg = new Rectangle(width, height);
        bg.setArcWidth(12);
        bg.setArcHeight(12);
        bg.setFill(javafx.scene.paint.Color.WHITE);
        bg.setStroke(javafx.scene.paint.Color.BLACK);

        double pointerY = 20 + bg.getHeight(); // Bottom of the rectangle

        // Adjust the pointer's Y-coordinate
        Polygon pointer = new Polygon(
                width / 2 - 5, pointerY,   // Left corner
                width / 2 + 5, pointerY,   // Right corner
                width / 2, pointerY + 15   // Tip of the triangle (10px below the bottom)
        );

        pointer.setFill(javafx.scene.paint.Color.WHITE);
        pointer.setStroke(javafx.scene.paint.Color.BLACK);

        bubbleRoot.getChildren().addAll(bg, pointer);

        // Patience bar background (gray)
        Rectangle barBG = new Rectangle(width, 6);
        barBG.setFill(javafx.scene.paint.Color.GRAY);
        barBG.setTranslateY(-12);

// Patience bar foreground (green, dynamic width)
        patienceBar = new Rectangle(width, 6);
        patienceBar.setFill(javafx.scene.paint.Color.LIMEGREEN);
        patienceBar.setTranslateY(-12);

        bubbleRoot.getChildren().addAll(barBG, patienceBar);

// Start time
        startTime = System.currentTimeMillis();


        // icons
        // in SpeechBubbleComponent constructor:
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            String base = o.getItem();                    // e.g. "hotdog"
            String key  = base + "_" + i;                 // e.g. "hotdog_0", "hotdog_1"

            Image img  = FXGL.image(base.toLowerCase() + ".png");
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
        // Attach the bubble into the customer's view
        entity.getViewComponent().addChild(bubbleRoot);

        double bubbleW = bubbleRoot.getBoundsInLocal().getWidth();
        bubbleRoot.setTranslateX(-bubbleW / 2 + 50);
        bubbleRoot.setTranslateY(-entity.getHeight() + 10);
    }

    @Override
    public void onUpdate(double tpf) {
        double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
        double progress = Math.max(0, 1.0 - elapsed / DURATION_SECONDS);
        patience = progress;

        double width = bubbleRoot.getBoundsInLocal().getWidth();
        patienceBar.setWidth(width * progress);
        patienceBar.setFill(progress > 0.5 ? javafx.scene.paint.Color.LIMEGREEN :
                progress > 0.2 ? javafx.scene.paint.Color.ORANGE :
                        javafx.scene.paint.Color.RED);
    }

    /** Removes the icon for a served item */
    /** Removes one icon for a served base item (e.g. "hotdog") */
    public void markServed(String baseItem) {
        // Find a key that starts with "baseItem_"
        String matchKey = icons.keySet().stream()
                .filter(k -> k.startsWith(baseItem + "_"))
                .findFirst()
                .orElse(null);

        if (matchKey != null) {
            ImageView iv = icons.remove(matchKey);
            bubbleRoot.getChildren().remove(iv);
        }
    }

    public void showPricePopup(int basePrice) {
        int adjustedPrice = (int)(basePrice * patience);

        // Update income world property
        int currentIncome = FXGL.geti("income");
        int newIncome = currentIncome + adjustedPrice;
        FXGL.set("income", newIncome);

        // Print total income to console
        System.out.println("Total Income: " + newIncome);

        String text = "+" + adjustedPrice;
        Text popup = new Text(text);
        popup.setFont(Font.font(32));
        popup.setFill(javafx.scene.paint.Color.YELLOW);
        popup.setTranslateX(bubbleRoot.getBoundsInLocal().getWidth() / 2 - 10);
        popup.setTranslateY(-40); // above the patience bar

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

    public void showDeductionPopup(int amount) {
        // amount should be positive (e.g. 5), we’ll internally prepend "-"
        int currentIncome = FXGL.geti("income");
        int newIncome = currentIncome - amount;
        FXGL.set("income", newIncome);

        // popup text is "-5"
        String text = "-" + amount;
        Text popup = new Text(text);
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
