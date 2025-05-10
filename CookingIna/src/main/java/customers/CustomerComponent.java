package customers;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.example.cookingina.control.UIController;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.List;

public class CustomerComponent extends Component {

    private volatile boolean hasArrived = false;
    private final double targetX;
    private final String direction;
    private int customerNo;
    private final List<Order> myOrders;

    private Thread movementThread;

    public CustomerComponent(double targetX, double targetY, String direction) {
        this.targetX = targetX;
        this.direction = direction;
        this.myOrders = OrderFactory.createForNewCustomer();
    }

    @Override
    public void onAdded() {
        // Start a background thread to handle movement
        movementThread = new Thread(() -> {
            try {
                while (!hasArrived) {
                    // Use FXGL tpf estimate (for simplicity assume 60 FPS = ~0.016s)
                    double tpf = 0.016;
                    double dx = 100 * tpf * (direction.equals("RIGHT") ? 1 : -1);

                    Platform.runLater(() -> {
                        if (!hasArrived && entity != null) {
                            entity.translateX(dx);

                            if (Math.abs(entity.getX() - targetX) < 1) {
                                entity.setX(targetX);
                                hasArrived = true;

                                FXGL.getGameTimer().runOnceAfter(this::onArrived, Duration.seconds(0));
                            }
                        }
                    });

                    Thread.sleep(16); // simulate frame delay ~60 FPS
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        movementThread.setDaemon(true); // allow app to close cleanly
        movementThread.start();
    }

    private void onArrived() {
        entity.addComponent(new SpeechBubbleComponent(myOrders));
    }

    public double getTargetX() {
        return targetX;
    }

    public List<Order> getOrders() {
        return myOrders;
    }
}
