package customers;

import java.util.Objects;

/**
 * Represents a customer's order of an item and quantity.
 */
public class Order {
    private String item;
    private int quantity;

    public Order(String item, int quantity) {
        this.item = Objects.requireNonNull(item, "item cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        this.quantity = quantity;
    }

    /**
     * Name of the ordered item.
     */
    public String getItem() {
        return item;
    }

    /**
     * Quantity ordered.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * For UI calls that expect getName().
     */
    public String getName() {
        return item;
    }

    @Override
    public String toString() {
        return item + " ×" + quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return quantity == order.quantity && item.equals(order.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity);
    }

    public void decrement() {
        if (quantity > 0)
            quantity--;
    }
}