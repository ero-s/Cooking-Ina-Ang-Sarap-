package com.example.cookingina.objects.entity;

import com.example.cookingina.objects.entity.equipment.BeverageDispenser;

public class BeverageFactory{

    public enum TYPE {
        ORANGE_JUICE, CALAMANSI_JUICE, BUKO_JUICE
    }

    public static BeverageDispenser create(TYPE type) {
        switch (type) {
            case ORANGE_JUICE:
                return new BeverageDispenser(new StoreItem(
                        "orange_juice",
                        "orangeJuice_finishedProduct.png",
                        "orangeJuice_finishedProduct.png",
                        true,
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
                        true,
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
                        true,
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