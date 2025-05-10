package com.example.cookingina.objects.entity;

import com.example.cookingina.objects.entity.equipment.BeverageDispenser;

public class ContainerFactory {

    public enum TYPE {
        HOTDOG, QUEKQUEK, TEMPURA, CUCUMBER, GUSO, SPICY_SAUCE, SWEET_SAUCE, MANGO, BAGOONG, SALT
    }

    public static Container create(TYPE type) {
        switch (type) {
            case HOTDOG: //DONE
                return new Container (new StoreItem(
                        "hotdog",
                        "rawHotdog.png",
                        "rawHotdog.png",
                        false,
                        15.0,
                        15.00,
                        3.0,
                        1,
                        80,
                        80) ,
                        "rawHotdog_container.png");
            case QUEKQUEK: //DONE
                return new Container (new StoreItem(
                        "quekquek",
                        "rawQuekquek.png",
                        "cooked_quek-quek.png",
                        false,
                        15.0,
                        12.99,
                        2.0,
                        1,
                        80,
                        80) ,
                        "rawQuekquek_container.png");
            case TEMPURA:
                return new Container (new StoreItem(
                        "tempura",
                        "raw_tempura.png",
                        "cooked_tempura.png",
                        false,
                        10.0,
                        5.0,
                        3.0,
                        1 ,
                        80,
                        80),
                        "rawTempura_container.png");
            case CUCUMBER:
                return new Container (new StoreItem("cucumber","rawQuekquek_container.png", "rawQuekquek.png", false,15.0,12.99, 2.0, 1 ,80, 80),"cucumberGarnish_container.png");
            case GUSO:
                return new Container (new StoreItem("guso","rawQuekquek_container.png", "rawQuekquek.png", false,15.0,12.99, 2.0, 1 ,80, 80),"gusoGarnish_container.png");
            case SPICY_SAUCE:
                return new Container (new StoreItem("spicy_sauce","rawQuekquek_container.png", "rawQuekquek.png", false,15.0,12.99, 2.0, 1 ,80, 80),"spicy_sauce.png");
            case SWEET_SAUCE:
                return new Container (new StoreItem("sweet_sauce","rawQuekquek_container.png", "rawQuekquek.png", false,15.0,12.99, 2.0, 1 ,80, 80),"sweet_sauce.png");
            case MANGO: // DONE
                return new Container (new StoreItem(
                        "mango",
                        "raw_mango.png",
                        "mango_ready.png",
                        false,
                        5.0,
                        30.0,
                        20.0,
                        1,
                        120,
                        120) , "manga_basket.png");
            case BAGOONG:
                return new Container (new StoreItem("bagoong","rawQuekquek_container.png", "rawQuekquek.png", false,15.0,12.99, 2.0, 1 ,80, 80),"hipon_bottle.png");
            case SALT:
                return new Container (new StoreItem("salt","rawQuekquek_container.png", "rawQuekquek.png",false,15.0,12.99, 2.0, 1 ,80, 80),"salt_bottle.png");
            default:
                throw new IllegalArgumentException("Unknown container type: " + type);
        }
    }
}
