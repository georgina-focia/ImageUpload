package com.example.imageupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.imageupload.repository.ItemRepo;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.imageupload.model.Item;

// User flow
// 1. user opens app
// 2. user clicks "select image"
// 3. gallery opens
// 4. users picks an image
// 5. image shows up on screen



public class MainActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    Button deleteButton;
    Button addButton;
    ItemAdapter adapter;
    List<Item> checklist;
    private ItemRepo itemRepo;

    // override - when the activity starts, run the code to set up the screen
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // super (oop) (inherit from parent class)
        // run OnCreate() version from AppCompatActivity before running new code
        super.onCreate(savedInstanceState);
        // object box
        // ObjectBox.init(this);
        // links Java to your XML layout
        setContentView(R.layout.activity_main);

        itemRepo = new ItemRepo();
        Item item1 = new Item("Headset", false, 0);
        itemRepo.addItem(item1);
        Item item2 = new Item("PTT", false, 0);
        itemRepo.addItem(item2);
        Item item3 = new Item("TAK server running", false, 0);
        itemRepo.addItem(item3);
        Item item4 = new Item("USB-C adapter", false, 0);
        itemRepo.addItem(item4);
        Item item5 = new Item("Bump helmet", false, 0);
        itemRepo.addItem(item5);
        Item item6 = new Item("Ice Cream", false,0);
        itemRepo.addItem(item6);
        Item item7 = new Item("Morty", false,0);
        itemRepo.addItem(item7);
        Item item8 = new Item("Morty Joystick", false, 0);
        itemRepo.addItem(item8);
        Item item9 = new Item("Orb", false, 0);
        itemRepo.addItem(item9);
        Item item10 = new Item("ASN", false, 0);
        itemRepo.addItem(item10);

        // TO DO: FIX THE LOGIC FOR REMOVING AND ADDING ITEMS
        // pull / retrieve
        List<Item> items = itemRepo.getAllItems();
        for (Item i : items) {
            Log.d("PCC", i.text + " is Checked? " + i.isChecked());
        }

        // update
        if (!items.isEmpty()) {
            Item first = items.get(0);
            first.setChecked(false);
            itemRepo.updateItem(first);
        }

        // delete
        if (!items.isEmpty()) {
            itemRepo.deleteItem(items.get(0).id);
        }

        // connect the variables to the views in XML
//        imageView = findViewById(R.id.imageView);
//        selectImageButton = findViewById(R.id.selectImageButton);
        // text view goes here

        // recycler view
        recyclerView = findViewById(R.id.recyclerViewChecklist);
        ItemAdapter adapter = new ItemAdapter(this, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRepo.clearAll();


        // dynamic list of items with assigned text fields
//        checklist = new ArrayList<>();
//
//        checklist.add(new Item("Headset", false));
//        checklist.add(new Item("Push to talk dongle (PTT)", false));
//        checklist.add(new Item("TAK server running", false));
//        checklist.add(new Item("USB-C adapter", false));
//        checklist.add(new Item("Bump helmet", false));
//        checklist.add(new Item("Ice Cream", false));
//        checklist.add(new Item("Morty", false));
//        checklist.add(new Item("Morty joystick", false));
//        checklist.add(new Item("Orb", false));
//        checklist.add(new Item("ASN", false));
//        checklist.add(new Item("Parrot", false));
//        checklist.add(new Item("Headset", false));
//        checklist.add(new Item("Push to talk dongle (PTT)", false));
//        checklist.add(new Item("TAK server running", false));
//        checklist.add(new Item("USB-C adapter", false));
//        checklist.add(new Item("Bump helmet", false));
//        checklist.add(new Item("Ice Cream", false));
//        checklist.add(new Item("Morty", false));
//        checklist.add(new Item("Morty joystick", false));
//        checklist.add(new Item("Orb", false));
//        checklist.add(new Item("ASN", false));
//        checklist.add(new Item("Parrot", false));

        // adapter being set to recycler view
        //adapter = new ItemAdapter(items);
        //recyclerView.setAdapter(adapter);
        //itemRepo.clearAll();

        // delete button included in activity layout
        Button deleteButton = findViewById(R.id.action_delete);
        deleteButton.setOnClickListener(v -> {
            // Remove all checked items
            for (int i = items.size() - 1; i >= 0; i--) { // iterate backwards
                if (items.get(i).isChecked()) {
                    items.remove(i);
                    adapter.notifyItemRemoved(i);
                }
            }
        });

        // add button included in activity layout
        Button addButton = findViewById(R.id.action_add);
        addButton.setOnClickListener(v -> {
            // Create a new item with default text
            Item newItem = new Item("New Item", false, 0);

            // Add it to the checklist
            items.add(newItem);

            // Notify adapter
            adapter.notifyItemInserted(items.size() - 1);

            // Scroll to the new item
            recyclerView.scrollToPosition(items.size() - 1);
        });
    }
}
