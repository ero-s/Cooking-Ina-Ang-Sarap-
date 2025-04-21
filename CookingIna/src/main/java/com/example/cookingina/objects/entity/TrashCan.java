package com.example.cookingina.objects.entity;

public class TrashCan {
    private final String closeResource;
    private final String openResource;

    public TrashCan(String closeResource, String openResource) {
        this.closeResource = closeResource;
        this.openResource = openResource;
    }

    public String getCloseResource() {
        return closeResource;
    }

    public String getOpenResource() {
        return openResource;
    }
}
