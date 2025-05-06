package com.example.cookingina.objects.entity.equipment;

import com.example.cookingina.objects.entity.Equipment;

public class Fryer extends Equipment {
    private boolean isOccupied = false; // New field
    public boolean isUnlocked = true;
    public Fryer(String emptyResource, String usedResource, int type, int playend, double speedMultiplier,
                 double cost, int capacity, boolean isUnlocked, String description) {
        super(emptyResource, usedResource, type, speedMultiplier, cost, capacity, isUnlocked, description);
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void setUnlocked(boolean set){
        this.isUnlocked = set;
    }

    public boolean isUnlocked(){
        return isUnlocked;
    }

    @Override
    public int getLayoutX() {
        return super.getLayoutX();
    }

    @Override
    public void setLayoutX(int layoutX) {
        super.setLayoutX(layoutX);
    }

    @Override
    public int getLayoutY() {
        return super.getLayoutY();
    }

    @Override
    public void setLayoutY(int layoutY) {
        super.setLayoutY(layoutY);
    }

    public int getType(){
        return super.getType();
    }
}
