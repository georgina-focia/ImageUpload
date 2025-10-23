package com.example.imageupload.repository;

import com.example.imageupload.MyApp;
import com.example.imageupload.model.Item;

import java.util.List;

import io.objectbox.Box;

public class ItemRepo {

    private final Box<Item> itemBox;

    public ItemRepo() {
        itemBox = MyApp.getBoxStore().boxFor(Item.class);
    }

    // push / insert item
    // prevent duplicates
    public long addItem(Item item) {
        return itemBox.put(item);
    }

    // pull / get all items
    public List<Item> getAllItems() {
        return itemBox.getAll();
    }

    // update item
    public void updateItem(Item item) {
        itemBox.put(item);
    }

    // delete item
    public void deleteItem(long id) {
        itemBox.remove(id);
    }

    // clear all items
    public void clearAll() {
        itemBox.removeAll();
    }

    // UPDATE ALL ITEMS
}