package com.example.cookingina.objects.entity;

import com.example.cookingina.objects.entity.equipment.BeverageDispenser;

public class ContainerTypeFactory {

    public enum TYPE {
        HOTDOG, QUEKQUEK, TEMPURA, MANGO, ORANGE_JUICE, CALAMANSI_JUICE, BUKO_JUICE, SALT, CUCUMBER, GUSO, SPICY_SAUCE, SWEET_SAUCE, BAGOONG,
    }

    public static ContainerType create(TYPE type) {
        switch (type) {
            case HOTDOG: //DONE
                return new Food(new StoreItem(
                        "hotdog",
                        "rawHotdog.png",
                        "rawHotdog.png",
                        15.0,
                        15.00,
                        3.0,
                        80,
                        80,
                        1) ,
                        "rawHotdog_container.png");
            case QUEKQUEK: //DONE
                return new Food(new StoreItem(
                        "quekquek",
                        "rawQuekquek.png",
                        "cooked_quek-quek.png",
                        15.0,
                        12.99,
                        2.0,
                        80,
                        80,
                        4) ,
                        "rawQuekquek_container.png");
            case TEMPURA:
                return new Food(new StoreItem(
                        "tempura",
                        "raw_tempura.png",
                        "cooked_tempura.png",
                        10.0,
                        5.0,
                        3.0,
                        80,
                        80,
                        6),
                        "rawTempura_container.png");
            case ORANGE_JUICE:
                return new BeverageDispenser(new StoreItem(
                        "orange_juice",
                        "orangeJuice_finishedProduct.png",
                        "orangeJuice_finishedProduct.png",
                        15.0,
                        12.99,
                        2.0,
                        110,
                        60,
                        3),
                        "orangeJuice_dispenser.png",
                        "nestea_juice_done.png",
                        1,
                        0,
                        1.5,
                        500.0,
                        4,
                        false,
                        "orange juice");
            case CALAMANSI_JUICE:
                return new BeverageDispenser(new StoreItem(
                        "calamansi_juice",
                        "calamansiJuice_finishedProduct.png",
                        "calamansiJuice_finishedProduct.png",
                        15.0,
                        12.99,
                        2.0,
                        110,
                        60,
                        7),
                        "calamansiJuice_dispenser.png",
                        "dragonfruit_juice_done.png",
                        1,
                        0,
                        1.5,
                        500.0,
                        4,
                        false,
                        "calamansi juice");
            case BUKO_JUICE:
                return new BeverageDispenser(new StoreItem(
                        "buko_juice",
                        "bukoJuice_finishedProduct.png",
                        "bukoJuice_finishedProduct.png",
                        15.0,
                        12.99,
                        2.0,
                        110,
                        60,
                        5),
                        "bukoJuice_dispenser.png",
                        "mangojuice_done.png",
                        1,
                        0,
                        1.5,
                        500.0,
                        4,
                        false,
                        "buko juice");
            case MANGO: // DONE
                return new Food(new StoreItem(
                        "mango",
                        "raw_mango.png",
                        "mango_ready.png",
                        5.0,
                        30.0,
                        20.0,
                        120,
                        120,
                        8) , "manga_basket.png");
            case CUCUMBER:
                return new Food(new StoreItem("cucumber","", "",15.0,12.99, 2.0,80, 80,1),"cucumberGarnish_container.png");
            case GUSO:
                return new Food(new StoreItem("guso","", "",15.0,12.99, 2.0,80, 80,1),"gusoGarnish_container.png");
            case SPICY_SAUCE:
                return new Food(new StoreItem("spicy_sauce","", "", 15.0,12.99, 2.0,80, 80,1),"spicy_sauce.png");
            case SWEET_SAUCE:
                return new Food(new StoreItem("sweet_sauce","", "",12.99, 2.0, 2.0, 80, 80,1),"sweet_sauce.png");
            case BAGOONG:
                return new Food(new StoreItem("bagoong","", "", 15.0,12.99, 2.0, 80, 80,8),"hipon_bottle.png");
            case SALT:
                return new Food(new StoreItem("salt","", "",15.0,12.99, 2.0, 80, 80,8),"salt_bottle.png");
            default:
                throw new IllegalArgumentException("Unknown container type: " + type);
        }
    }
}
