package customers;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.example.cookingina.control.UIController;
import javafx.util.Duration;

import java.util.List;

public class CustomerComponent extends Component {
    private boolean hasArrived = false;
    private final double targetX;
    private final String direction;
    private int customerNo;
    private final List<Order> myOrders;

    /**
     * Only store the targetX and direction here.
     * Do *not* call entity.setY() in the constructor!
     */
    public CustomerComponent(double targetX, double targetY, String direction) {
        this.targetX  = targetX;
        this.direction = direction;
        // NO entity.setY(...) here!
        this.myOrders = OrderFactory.createForNewCustomer();
    }

    /** Expose for overlap checking */
    public double getTargetX() {
        return targetX;
    }

    @Override
    public void onUpdate(double tpf) {
        if (hasArrived)
            return;

        double dx = 100 * tpf * (direction.equals("RIGHT") ? 1 : -1);
        entity.translateX(dx);

        if (Math.abs(entity.getX() - targetX) < 1) {
            entity.setX(targetX);
            hasArrived = true;

            FXGL.getGameTimer().runOnceAfter(this::onArrived, Duration.seconds(0));
        }
    }

    private void onArrived() {
        // instead of UIController.showOrderBubble(...)
        entity.addComponent(new SpeechBubbleComponent(myOrders));
    }
}
