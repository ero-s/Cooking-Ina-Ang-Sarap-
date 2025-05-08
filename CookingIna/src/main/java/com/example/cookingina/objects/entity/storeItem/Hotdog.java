//package com.example.cookingina.objects.entity.storeItem;
//
//import com.example.cookingina.objects.entity.StoreItem;
//
//public class Hotdog extends StoreItem {
//    public Hotdog(String container, String rawResource, String cookedResource, String description, Double preparationTime, Double sellingPrice, Double discardCost, int status) {
//        super(container, rawResource, cookedResource, description, preparationTime, sellingPrice, discardCost, status);
//    }
//}


package com.example.cookingina.objects.entity.storeItem;

import com.example.cookingina.objects.entity.StoreItem;

public class Hotdog extends StoreItem {
    public Hotdog(String container, String rawResource, String cookedResource, String description, Double preparationTime, Double sellingPrice, Double discardCost, int status, int height, int width) {
        super(container, rawResource, cookedResource, description, preparationTime, sellingPrice, discardCost, status, height, width);
    }
}
