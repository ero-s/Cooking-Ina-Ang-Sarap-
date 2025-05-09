package com.example.cookingina.objects.entity;

public class Container {
    private final StoreItem item;
    private final String containerResource;
    private int layoutX;
    private int layoutY;

    public Container(StoreItem item, String containerResource) {
        this.item = item;
        this.containerResource = containerResource;
        this.layoutX = 0;
        this.layoutY = 0;
    }

    public StoreItem getItem() {
        return item;
    }

    public String getContainerResource(){
        return containerResource;
    }

    public void setLayoutX(int layoutX) {
        this.layoutX = layoutX;
    }

    public void setLayoutY(int layoutY) {
        this.layoutY = layoutY;
    }
}