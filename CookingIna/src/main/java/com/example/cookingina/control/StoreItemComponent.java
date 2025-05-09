package com.example.cookingina.control;

import com.almasb.fxgl.entity.component.Component;
import com.example.cookingina.objects.entity.StoreItem;

public class StoreItemComponent extends Component {
    private StoreItem storeItem;

    public StoreItemComponent(StoreItem storeItem) {
        this.storeItem = storeItem;
    }

    public StoreItem getStoreItem() {
        return storeItem;
    }
}
