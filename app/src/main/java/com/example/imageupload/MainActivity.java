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
import android.widget.Toast;

import com.example.imageupload.model.Item;

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

        // delete button logic
        Button deleteButton = findViewById(R.id.action_delete);
        deleteButton.setOnClickListener(v -> {
            // Remove all checked items
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
            // create a new item with default text
            Item newItem = new Item("New Item", false, 0);

            // save to database (returns assigned ID)
            long newId = itemRepo.addItem(newItem);
            newItem.id = newId;

            // add to list and refresh UI
            items.add(newItem);
            adapter.notifyItemInserted(items.size() - 1);
            recyclerView.scrollToPosition(items.size() - 1);
            Toast.makeText(this, "Item Added!", Toast.LENGTH_SHORT).show();
        });

        Button refreshButton = findViewById(R.id.action_refresh);
        refreshButton.setOnClickListener(v -> {
            itemRepo.getAllItems();
            Toast.makeText(this, "List refreshed!", Toast.LENGTH_SHORT).show();
        });

        Button closeButton = findViewById(R.id.action_close);
        closeButton.setOnClickListener(v -> {
                finish();
                System.exit(0);
            Toast.makeText(this, "Exiting!", Toast.LENGTH_SHORT).show();
        });





        // sanity check
        Log.d("PCC", "Loaded " + items.size() + " items from DB");
    }
}
