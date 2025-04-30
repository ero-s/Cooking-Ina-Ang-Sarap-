package com.example.cookingina.objects.entity;

public class PaperTray  {

    private final String emptyResource;
    private final String openResource;
    private int x;
    private int y;


    public PaperTray(String emptyResource, String openResource) {
        this.emptyResource = emptyResource;
        this.openResource = openResource;
        x = 0;
        y = 0;
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

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}