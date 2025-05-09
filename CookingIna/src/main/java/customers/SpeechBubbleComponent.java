
package customers;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.runOnce;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class SpeechBubbleComponent extends Component {

    private final Group bubbleRoot = new Group();
    private final Map<String, ImageView> icons = new HashMap<>();

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

        // icons
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            Image img = FXGL.image(o.getItem().toLowerCase() + ".png");
            ImageView iv = new ImageView(img);
            iv.setFitWidth(56);
            iv.setFitHeight(56);
            iv.setLayoutX(10);
            iv.setLayoutY(10 + i * 50);
            icons.put(o.getItem(), iv);
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

    /** Removes the icon for a served item */
    public void markServed(String itemName) {
        ImageView iv = icons.remove(itemName);
        if (iv != null) {
            bubbleRoot.getChildren().remove(iv);
        }
    }
}
