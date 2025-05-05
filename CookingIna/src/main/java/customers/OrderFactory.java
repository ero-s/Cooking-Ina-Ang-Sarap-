package customers;

import java.util.Random;

public class OrderFactory {

    private static final String[] menuItems = {"Cake", "Muffin", "Cupcake", "Cookie", "Brownie"};

    public static Order createRandomOrder() {
        Random random = new Random();
        String item = menuItems[random.nextInt(menuItems.length)];
        int quantity = random.nextInt(5) + 1; // Random quantity between 1 and 5
        return new Order(item, quantity);
    }
}
