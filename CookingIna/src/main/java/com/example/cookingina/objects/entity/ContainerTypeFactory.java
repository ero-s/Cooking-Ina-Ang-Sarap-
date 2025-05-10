package com.example.cookingina.objects.entity;

import com.example.cookingina.objects.entity.equipment.BeverageDispenser;

public class ContainerTypeFactory {

    public enum TYPE {
        HOTDOG, QUEKQUEK, TEMPURA, CUCUMBER, GUSO, SPICY_SAUCE, SWEET_SAUCE, MANGO, BAGOONG, ORANGE_JUICE, CALAMANSI_JUICE, BUKO_JUICE, SALT, HALO_HALO
    }

    public static ContainerType create(TYPE type) {
        switch (type) {
            case HOTDOG: //DONE
                return new Food(new StoreItem(
                        "hotdog",
                        "raw_hotdog.png",
                        "cooked_hotdog.png",
                        "overcooked_hotdog.png",
                        15.0,
                        15.00,
                        3.0,
                        1,
                        80,
                        80) ,
                        "rawHotdog_container.png");
            case QUEKQUEK: //DONE
                return new Food(new StoreItem(
                        "quekquek",
                        "raw_quekquek.png",
                        "cooked_quekquek.png",
                        "overcooked_quekquek.png",
                        15.0,
                        12.99,
                        2.0,
                        1,
                        80,
                        80) ,
                        "rawQuekquek_container.png");
            case TEMPURA:
                return new Food(new StoreItem(
                        "tempura",
                        "raw_tempura.png",
                        "cooked_tempura.png",
                        "overcooked_tempura.png",
                        10.0,
                        5.0,
                        3.0,
                        1 ,
                        80,
                        80),
                        "rawTempura_container.png");
            case CUCUMBER:
                return new Food(new StoreItem("cucumber","rawQuekquek_container.png", "rawQuekquek.png","overcooked_quekquek.png",15.0,12.99, 2.0, 1 ,80, 80),"cucumberGarnish_container.png");
            case GUSO:
                return new Food(new StoreItem("guso","rawQuekquek_container.png", "rawQuekquek.png","overcooked_quekquek",15.0,12.99, 2.0, 1 ,80, 80),"gusoGarnish_container.png");
            case SPICY_SAUCE:
                return new Food(new StoreItem("spicy_sauce","rawQuekquek_container.png", "rawQuekquek.png", "overcooked_quekquek",15.0,12.99, 2.0, 1 ,80, 80),"spicy_sauce.png");
            case SWEET_SAUCE:
                return new Food(new StoreItem("sweet_sauce","rawQuekquek_container.png", "rawQuekquek.png","overcooked_quekquek",12.99, 2.0, 2.0, 1, 80, 80),"sweet_sauce.png");
            case MANGO: // DONE
                return new Food(new StoreItem(
                        "mango",
                        "raw_mango.png",
                        "mango_ready.png",
                        "mango_ready.png",
                        5.0,
                        30.0,
                        20.0,
                        1,
                        120,
                        120) , "manga_basket.png");
            case HALO_HALO: // DONE
                return new Food(new StoreItem(
                        "halo-halo",
                        "glass_with_ice.png",
                        "halo_halo.png",
                        "halo_halo.png",
                        5.0,
                        30.0,
                        20.0,
                        1,
                        120,
                        120) , "ice_crusher.png");
            case BAGOONG:
                return new Food(new StoreItem("bagoong","rawQuekquek_container.png", "rawQuekquek.png", "overcooked_quekquek",15.0,12.99, 2.0, 1 ,80, 80),"hipon_bottle.png");
            case SALT:
                return new Food(new StoreItem("salt","rawQuekquek_container.png", "rawQuekquek.png","overcooked_quekquek",15.0,12.99, 2.0, 1 ,80, 80),"salt_bottle.png");
            case ORANGE_JUICE:
                return new BeverageDispenser(new StoreItem(
                        "orange_juice",
                        "orangeJuice_finishedProduct.png",
                        "orangeJuice_finishedProduct.png",
                        "orangeJuice_finishedProduct.png",
                        15.0,
                        12.99,
                        2.0,
                        1,
                        110,
                        60),
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
                        "calamansiJuice_finishedProduct.png",
                        15.0,
                        12.99,
                        2.0,
                        1,
                        110,
                        60),
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
                        "bukoJuice_finishedProduct.png",
                        15.0,
                        12.99,
                        2.0,
                        1,
                        110,
                        60),
                        "bukoJuice_dispenser.png",
                        "mangojuice_done.png",
                        1,
                        0,
                        1.5,
                        500.0,
                        4,
                        false,
                        "buko juice");
            default:
                throw new IllegalArgumentException("Unknown container type: " + type);
        }
    }
}
