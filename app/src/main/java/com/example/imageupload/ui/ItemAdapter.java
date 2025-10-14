package com.example.imageupload.ui;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.imageupload.ui.Item;
import com.example.imageupload.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> checklist;

    public ItemAdapter(List<Item> checklist)
    {
        this.checklist = checklist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Item item = checklist.get(position);
        holder.textView.setText(item.getText());
        holder.checkBox.setChecked(item.isChecked());

        // update data when checkbox toggled
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            item.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount()
    {
        return checklist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox checkBox;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById((R.id.checkBox));
            textView = itemView.findViewById(R.id.item_text);
        }
    }
}