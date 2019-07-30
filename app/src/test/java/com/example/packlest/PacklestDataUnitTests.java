package com.example.packlest;

import org.junit.Test;

import java.util.HashSet;

public class PacklestDataUnitTests {
    @Test
    public void packlestDataItemDelete() {
        PacklestData packlestData = new PacklestData();
        PackingList packingList = new PackingList();
        Item item = new Item();
        packlestData.addOrUpdateItem(item, new HashSet<>());
        packlestData.addOrUpdatePackingList(packingList, new HashSet<>());
        packlestData.addItemToPackingList(item.uuid, packingList.uuid);
        assert(packingList.itemInstances.size() == 1);
        packlestData.deleteItem(item.uuid);
        assert(packingList.itemInstances.size() == 0);
    }
}