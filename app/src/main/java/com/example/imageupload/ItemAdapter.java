package com.example.imageupload;

import android.content.Context;
import android.text.Editable;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageupload.model.Item;
import com.example.imageupload.repository.ItemRepo;

import java.util.List;

import io.objectbox.Box;

// adapter acts like a bridge between data and the UI
// in terms of using recycler view, it holds data (checklist of items), creates and binds the biew, tells the RV how many items exist, updates the UI when data changes
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;
    private final Context context;

    
    // Spinner options
    private final String[] priorities = {"In-Progress", "Completed", "Blocked"};

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
        // sync checkbox state
        //holder.checkBox.setChecked(item.isChecked());

        // listen for checkbox changes
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
        });


        // CREATE AN EDIT TEXT ADAPTER
        // textWatcher is for watching any changes in editText
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // this function is called before text is edited
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // this function is called after text is edited
            }
        };

        holder.editText.addTextChangedListener(textWatcher);
        // // set the TextChange Listener for
        //        // the edit text field
        //
        // make entire item clickable
//        holder.editText.setOnClickListener(v -> {
//            // toggle the checkbox
//            boolean newState = !item.isChecked();
//            item.setChecked(newState);
//            holder.checkBox.setChecked(newState);
//            itemRepo.updateItem(item);
//        });
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
}