package com.example.imageupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.imageupload.repository.ItemRepo;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.imageupload.model.Item;

import io.objectbox.BoxStore;

public class MainActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
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
        // only add starter items if database == empty
        if (itemRepo.getAllItems().isEmpty()) {
            itemRepo.addItem(new Item("Headset", false, 0));
            itemRepo.addItem(new Item("PTT", false, 0));
            itemRepo.addItem(new Item("TAK server running", false, 0));
            itemRepo.addItem(new Item("USB-C adapter", false, 0));
            itemRepo.addItem(new Item("Bump helmet", false, 0));
            itemRepo.addItem(new Item("Ice Cream", false, 0));
            itemRepo.addItem(new Item("Morty", false, 0));
            itemRepo.addItem(new Item("Morty Joystick", false, 0));
            itemRepo.addItem(new Item("Orb", false, 0));
            itemRepo.addItem(new Item("ASN", false, 0));
        }

        // now safely load them
        List<Item> items = itemRepo.getAllItems();
        // recycler view
        recyclerView = findViewById(R.id.recyclerViewChecklist);
        ItemAdapter adapter = new ItemAdapter(this, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // exit button logic
        Button closeButton = findViewById(R.id.action_close);
        closeButton.setOnClickListener(v -> {
            System.exit(0);
            finish();
            Toast.makeText(this, "Exiting!", Toast.LENGTH_SHORT).show();
        });

        // refresh list button logic
        Button refreshButton = findViewById(R.id.action_refresh);
        refreshButton.setOnClickListener(v -> {
            itemRepo.getAllItems();
            Toast.makeText(this, "List synced", Toast.LENGTH_SHORT).show();
        });

        // delete button logic
        // deleted item removes from the list and the database?? double check
        Button deleteButton = findViewById(R.id.action_delete);
        deleteButton.setOnClickListener(v -> {
            // remove all checked items
            for (int i = items.size() - 1; i >= 0; i--) { // iterate backwards
                if (items.get(i).isChecked()) {
                    // delete from database
                    itemRepo.deleteItem(items.get(i).id);
                    // remove from list
                    items.remove(i);
                    adapter.notifyItemRemoved(i);
                    Toast.makeText(this, "Item Deleted!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // add button logic
        Button addButton = findViewById(R.id.action_add);
        addButton.setOnClickListener(v -> {
            // save the text placed in the edit text field
            // once done button is pressed
            // make sure it saves even when exit is pressed
            // create a new item with default text
            Item newItem = new Item("", false, 0);
            // edit text needs to go into this

            // save to database (returns assigned ID)
            long newId = itemRepo.addItem(newItem);
            newItem.id = newId;

            // add to list and refresh UI
            items.add(newItem);
            adapter.notifyItemInserted(items.size() - 1);
            recyclerView.scrollToPosition(items.size() - 1);
            Toast.makeText(this, "Item Added!", Toast.LENGTH_SHORT).show();
        });

        // sanity check
        Log.d("PCC", "Loaded " + items.size() + " items from DB");
    }
}
