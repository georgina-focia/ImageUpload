package com.example.imageupload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageupload.model.Item;

import java.util.List;

import io.objectbox.Box;

// adapter acts like a bridge between data and the UI
// in terms of using recycler view, it holds data (checklist of items), creates and binds the biew, tells the RV how many items exist, updates the UI when data changes
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;
    private final Context context;
    
    // Spinner options
    private final String[] priorities = {"In-Progess", "Completed", "Blocked"};


    public ItemAdapter(Context context, List<Item> items)
    {
        this.context = context;
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        CheckBox checkBox;
        Spinner priority_spinner;

        public ViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
            checkBox = itemView.findViewById(R.id.checkBox);
            priority_spinner = itemView.findViewById(R.id.priority_spinner);
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
        holder.editText.setText(item.getText());
        // listener
        holder.checkBox.setOnCheckedChangeListener(null);
        // sync checkbox state
        holder.checkBox.setChecked(item.isChecked());
        // listen for checkbox changes
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
        });

        // make entire item clickable
        holder.editText.setOnClickListener(v -> {
            // Toggle the checkbox
            boolean newState = !item.isChecked();
            item.setChecked(newState);
            holder.checkBox.setChecked(newState);
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            context,
            R.layout.spinner_layout,
            priorities
    );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
        holder.priority_spinner.setAdapter(spinnerAdapter);
        // Set current priority
        holder.priority_spinner.setSelection(item.getPriority());

        // Handle Spinner changes
        holder.priority_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                item.setPriority(pos); // save new priority to item
                // Optionally update ObjectBox
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
}