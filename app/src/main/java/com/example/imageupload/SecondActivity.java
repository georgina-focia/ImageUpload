package com.example.imageupload;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageupload.model.Item;
import com.example.imageupload.repository.ItemRepo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    private ItemRepo itemRepo; // creating an instance of the repo to load/save Item objects
    private RecyclerView recyclerViewToday; // reference to the RecyclerView in activity_second.xml  that shows due tasks
    private ItemAdapter adapter; // this adapter binds Item objects to RecyclerView rows
    private List<Item> allItems; // a list to hold every item fetched from the database
    private List<Item> filteredItems = new ArrayList<>(); // a list to hold the items that match the current date on the Calendar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // attaches activity_second.xml as the UI attached to this activity
        setContentView(R.layout.activity_second);

        itemRepo = new ItemRepo();
        // retrieves all items from database and stores them in the allItems List
        allItems = itemRepo.getAllItems();
        // finds the recyclerView in activity_second.xml and assigns it to the variable
        recyclerViewToday = findViewById(R.id.recyclerViewToday);
        // connecting the adapter to the recyclerView
        adapter = new ItemAdapter(this, filteredItems, itemRepo);
        recyclerViewToday.setAdapter(adapter);
        recyclerViewToday.setLayoutManager(new LinearLayoutManager(this));

        // finds the calendarView in activity_second.xml and
        CalendarView calendarView = findViewById(R.id.calendarView);

        // When user selects a certain date, update the list to show items that are due on that date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            long selectedMillis = selectedDate.getTimeInMillis();
            filterItemsByDate(selectedMillis);
        });

        // initially show today's due tasks when the calendar first opens on today's date
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        // using unix timestamp to simplify conversion for calendarView
        filterItemsByDate(today.getTimeInMillis());

        // GEORGINA: creating the functionality for the back button to go back to MainActivity.java
        Button closeButton = findViewById(R.id.action_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // a method that filters items that are due on certain dates
    private void filterItemsByDate(long dateMillis) {
        filteredItems.clear();

        // calculates number of milliseconds in one day, this is the start of the current day
        long oneDay = 24 * 60 * 60 * 1000L;
        // this is the start of the NEXT day
        long endOfDay = dateMillis + oneDay;

        for (Item item : allItems) {
            Long dueObj = item.getDueAt();
            if (dueObj == null) continue;  // or just skip this item
            long due = dueObj;

            if (due >= dateMillis && due < endOfDay) {
                filteredItems.add(item);
            }
        }
        adapter.notifyDataSetChanged();

        if (filteredItems.isEmpty()) {
            Toast.makeText(this, "No tasks due on this date", Toast.LENGTH_SHORT).show();
        }
    }

}
