package com.example.packlest;

class Item extends AbstractBaseObject {
    private ItemCategory itemCategory;

    Item() {
        super();
    }

    Item(Item item) {
        super(item);
        itemCategory = item.itemCategory; // TODO can abstract this into the relationship data object
    }
}
