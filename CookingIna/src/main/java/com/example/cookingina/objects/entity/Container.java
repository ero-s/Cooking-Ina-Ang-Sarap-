package com.example.cookingina.objects.entity;

public class Container {
    private String rawResource;
    private String foodItemResource;
    private String description;
    private int layoutX;
    private int layoutY;

    public Container(String rawResource, String foodItemResource, String description) {
        this.rawResource = rawResource;
        this.foodItemResource = foodItemResource;
        this.description = description;
        this.layoutX = 0;
        this.layoutY = 0;
    }

    public String getRawResource() {
        return rawResource;
    }

    public String getFoodItemResource() {
        return foodItemResource;
    }

    public String getDescription() {
        return description;
    }

    public int getLayoutX() {
        return layoutX;
    }

    public int getLayoutY() {
        return layoutY;
    }

    public void setRawResource(String rawResource) {
        this.rawResource = rawResource;
    }

    public void setFoodItemResource(String foodItemResource) {
        this.foodItemResource = foodItemResource;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLayoutX(int layoutX) {
        this.layoutX = layoutX;
    }

    public void setLayoutY(int layoutY) {
        this.layoutY = layoutY;
    }
}