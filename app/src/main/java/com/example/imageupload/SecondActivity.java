package com.example.imageupload;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageupload.model.Item;
import com.example.imageupload.repository.ItemRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SecondActivity extends AppCompatActivity {

    private ItemRepo itemRepo; // creating an instance of the repo to load/save Item objects
    private RecyclerView recyclerViewToday; // reference to the RecyclerView in activity_second.xml  that shows due tasks
    private ItemAdapter adapter; // this adapter binds Item objects to RecyclerView rows
    private List<Item> allItems; // a list to hold every item fetched from the database
    private List<Item> filteredItems = new ArrayList<>(); // a list to hold the items that match the current date on the Calendar
    private TextView aiSummaryText; // a variable to assist the LLM in generating daily tasks

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
        // finds the AI generated text summary view in activity_second.xml
        aiSummaryText = findViewById(R.id.AITextView);
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

        // generating a string summary of what tasks need to be done today
        //String summary = generateSummary(filteredItems);
        //aiSummaryText.setText(summary);
        fetchAISummary(filteredItems);


        if (filteredItems.isEmpty()) {
            Toast.makeText(this, "No tasks due on this date", Toast.LENGTH_SHORT).show();
        }
    }

    // a method that generates a string summary of what tasks need to be done on a given day
    // eventually will integrate an LLM to do this
    private String generateSummary(List<Item> items) {
        if (items.isEmpty()) {
            return "You have no tasks due today.";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Good morning! Today your priorities are:\n");

        // Sort tasks by priority (0 = highest)
        items.sort(Comparator.comparingInt(Item::getPriority));

        for (Item item : items) {
            summary.append("• ").append(item.getText()).append("\n");
        }

        return summary.toString();
    }

    // a method that allows AndroidStudio to communicate with my local Flask server to generate
    // an AI summary of tasks to complete on a given day
    private void fetchAISummary(List<Item> items) {
        // extract task names from the passed in Item objects
        List<String> taskStrings = new ArrayList<>();
        for (Item item : items) {
            taskStrings.add(item.getText());
        }

        // building JSON body for Flask with tasks due on a given day
        String json = "{\"tasks\": [";
        for (int i = 0; i < taskStrings.size(); i++) {
            json += "\"" + taskStrings.get(i).replace("\"", "\\\"") + "\"";
            if (i < taskStrings.size() - 1) json += ",";
        }
        json += "]}";

        // HTTP engine that sends network requests
        OkHttpClient client = new OkHttpClient();

        // creates the POST request body -- sends the JSON string
        RequestBody body = RequestBody.create(
                json,
                okhttp3.MediaType.parse("application/json; charset=utf-8")
        );

        // builds the HTTP request
        // this uses my local IP address that pops up when I run the Flask server on my machine
        Request request = new Request.Builder()
                .url("http://192.168.1.71:5000/summary")
                .post(body)
                .build();

        // async network call, this sends the request
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> aiSummaryText.setText("Error contacting AI server."));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(responseBody);
                        String summary = obj.getString("summary");

                        runOnUiThread(() -> aiSummaryText.setText(summary));

                    } catch (Exception ex) {
                        runOnUiThread(() -> aiSummaryText.setText("Invalid AI response."));
                    }
                } else {
                    runOnUiThread(() -> aiSummaryText.setText("AI server error."));
                }
            }
        });
    }



}
