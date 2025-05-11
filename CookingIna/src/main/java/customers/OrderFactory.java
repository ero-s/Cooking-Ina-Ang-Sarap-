
package customers;

import com.example.cookingina.CookingInaMain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Factory for generating customer orders.
 */
public class OrderFactory {

    private static final String[] MENU_ITEMS = {"cooked_hotdog", "cooked_quekquek", "cooked_tempura", "mango_ready", "calamansiJuice_finishedProduct","bukoJuice_finishedProduct","orangeJuice_finishedProduct", "halo_halo"};
    private static final int MAX_QUANTITY = 3;
    private static final int MAX_ORDERS_PER_CUSTOMER = 3;
    private static final Random RANDOM = new Random();

    /**
     * Create a single random Order.
     */

    public static Order createRandomOrder() {
        CookingInaMain game = new CookingInaMain();
        int safeUpperBound = Math.min(game.getPlayerLevel(), MENU_ITEMS.length);
        String item = MENU_ITEMS[RANDOM.nextInt(safeUpperBound)];
        int quantity = RANDOM.nextInt(MAX_QUANTITY) + 1;
        return new Order(item, quantity);
    }

    /**
     * Create a list of orders for a new customer.
     * Each customer gets between 1 and MAX_ORDERS_PER_CUSTOMER orders.
     */

    public static List<Order> createForNewCustomer() {
        int count = RANDOM.nextInt(MAX_ORDERS_PER_CUSTOMER) + 1;
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            orders.add(createRandomOrder());
        }
        return orders;
    }
}
