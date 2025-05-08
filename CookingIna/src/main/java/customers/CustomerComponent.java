package customers;

import com.almasb.fxgl.entity.component.Component;
import com.example.cookingina.control.UIController;

public class CustomerComponent extends Component {
    private boolean hasArrived = false;
    private final double targetX;
    private final String direction;
    private int customerNo;

    /**
     * Only store the targetX and direction here.
     * Do *not* call entity.setY() in the constructor!
     */
    public CustomerComponent(double targetX, double targetY, String direction) {
        this.targetX  = targetX;
        this.direction = direction;
        // NO entity.setY(...) here!
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
        }
    }
}
