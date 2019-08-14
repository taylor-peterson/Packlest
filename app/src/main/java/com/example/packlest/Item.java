package com.example.packlest;

class Item extends AbstractBaseObject {
    ItemCategory itemCategory;

    Item() {
        super();
    }

    Item(Item item) {
        super(item);
        itemCategory = item.itemCategory;
    }
}
