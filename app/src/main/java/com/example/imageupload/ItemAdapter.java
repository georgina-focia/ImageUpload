package com.example.imageupload;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView; // GEORGINA: added for dueDate

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageupload.model.Item;
import com.example.imageupload.repository.ItemRepo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.objectbox.Box;

// adapter acts like a bridge between data and the UI
// in terms of using recycler view, it holds data (checklist of items), creates and binds the biew, tells the RV how many items exist, updates the UI when data changes
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;
    private final Context context;
    public Box<Item> itemRepo;

    // spinner options
    private final String[] priorities = {"In-Progress", "Completed", "Blocked"};

    public ItemAdapter(Context context, List<Item> items, ItemRepo itemRepo)
    {
        this.context = context;
        this.items = items;
        // GEORGINA: modified because itemrepo being null was causing an error
        this.itemRepo = MyApp.getBoxStore().boxFor(Item.class);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        CheckBox checkBox;
        Spinner priority_spinner;
        //GEORGINA: adding a field for dueDate
        TextView itemDueDate;

        public ViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
            checkBox = itemView.findViewById(R.id.checkBox);
            priority_spinner = itemView.findViewById(R.id.priority_spinner);
            // GEORGINA: getting the due date from the id that I defined in items.xml
            itemDueDate = itemView.findViewById(R.id.item_due_date);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new ViewHolder(view);
    }

    // give the user the ability to add/remove something from the list (put + and - icons in header)
    // when checkbox is selected, give user the option to delete
    // always give the user the ability to add new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Item item = items.get(position);

        // remove any previous TextWatcher to prevent multiple triggers
        if (holder.editText.getTag() instanceof TextWatcher) {
            holder.editText.removeTextChangedListener((TextWatcher) holder.editText.getTag());
        }
        holder.editText.setText(item.getText());
        // implement the TextWatcher callback listener
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int asfter) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // get the content of both the edit text
                item.setText(s.toString());
                System.out.println("On Text Changed: " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        holder.editText.addTextChangedListener(textWatcher);
        // store the watcher in the tag so we can remove it later
        holder.editText.setTag(textWatcher);
        // listen for checkbox changes
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
        });

        // make entire item clickable
        holder.editText.setOnClickListener(v -> {
            // toggle the checkbox
            boolean newState = !item.isChecked();
            item.setChecked(newState);
            holder.checkBox.setChecked(newState);
            // GEORGINA: added this, not sure if it works?
            //item.setDueAt(item.getDueAt());
            itemRepo.put(item);
        });

        // GEORGINA: dueDate logic
        Long dueAtObj = item.getDueAt();   // this may be null
        long dueAtMillis = (dueAtObj == null) ? 0 : dueAtObj;

        String humanReadableDate;

        if (dueAtMillis == 0) {
            humanReadableDate = "No due date";
        } else {
            humanReadableDate = formatDateFromMillis(dueAtMillis);
        }

        //holder.itemDueDate.setText(humanReadableDate);


        // GEORINGA: debugging
        Log.d("DueDateDebug", "Position: " + position + ", formatted date: " + humanReadableDate);
        holder.itemDueDate.setText(humanReadableDate); // THIS LINE SHOULD DISPLAY THE TEXT

        // GEORGINA: adding logic to be able to select a due date after you add an item on UI
        holder.itemDueDate.setOnClickListener(v -> {

            // open a date picker
            final Calendar calendar = Calendar.getInstance();

            // If item has a stored date, start picker at that date
            if (item.getDueAt() != null && item.getDueAt() > 0) {
                calendar.setTimeInMillis(item.getDueAt());
            }

            // Create the dialog
            DatePickerDialog dialog = new DatePickerDialog(
                    holder.itemView.getContext(),
                    (view, year, month, dayOfMonth) -> {

                        // convert picked date to millis
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth, 0, 0, 0);

                        long newDueAt = chosen.getTimeInMillis();

                        // save to objectbox
                        item.setDueAt(newDueAt);
                        itemRepo.put(item);

                        // update UI
                        holder.itemDueDate.setText(formatDateFromMillis(newDueAt));
                        notifyItemChanged(position);

                        Toast.makeText(context, "Due date updated!", Toast.LENGTH_SHORT).show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });


        // spinner logic
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            context,
            R.layout.spinner_layout,
            priorities
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
        holder.priority_spinner.setAdapter(spinnerAdapter);
        // set current priority
        holder.priority_spinner.setSelection(item.getPriority());
        // handle Spinner changes
        holder.priority_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                item.setPriority(pos); // save new priority to item
                // update ObjectBox
                Box<Item> box = MyApp.getBoxStore().boxFor(Item.class);
                box.put(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    //GEORGINA: helper method to display milliseconds date in human readable format
    private String formatDateFromMillis(long milliseconds) {
        if (milliseconds <= 0) {
            return "No date set"; // Return a meaningful default string
        }
        // Format the date as dd/MM/yyyy (e.g., 20/11/2025)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(milliseconds);
        return sdf.format(date);
    }
}