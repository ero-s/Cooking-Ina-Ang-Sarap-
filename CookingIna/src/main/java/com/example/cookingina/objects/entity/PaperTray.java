package com.example.cookingina.objects.entity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.example.cookingina.CookingInaMain;

public class PaperTray extends Entity {

    private final String emptyResource;
    private final String openResource;
    private boolean isOccupied;
    private StoreItem storeItem;
    private final int width;
    private final int height;
    private int x;
    private int y;


    public PaperTray(String emptyResource, String openResource, int width, int height) {
        this.emptyResource = emptyResource;
        this.openResource = openResource;
        this.width = width;
        this.height = height;
        x = 0;
        y = 0;
        isOccupied = false;
    }

    public String getEmptyResource() {
        return openResource;
    }

    public void setLayoutX(int x) {
        this.x = x;
    }

    public void setLayoutY(int y) {
        this.y = y;
    }

    public void addStoreItem(StoreItem storeItem, int x, int y) {
        FXGL.entityBuilder()
                .type(CookingInaMain.EntityType.PLATE)
                .at(x, y)
                .viewWithBBox(FXGL.texture(storeItem.getCookedResource(), storeItem.getWidth(), storeItem.getHeight()))
                .buildAndAttach();
        this.storeItem = storeItem;
    }

    public boolean isOccupied(){
        return isOccupied;
    }
    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }
    public StoreItem getStoreItem() {
        return storeItem;
    }
}