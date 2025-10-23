package com.example.imageupload.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
// each checklist item along with boolean functionality

@Entity
public class Item {
    @Id
    public long id;
    public String text;
    private boolean isChecked;
    private int priority; // (1, critical...5, low)

    // no arg constructor for ObjectBox
    public Item(){}

    public Item(String text, boolean isChecked, int priority)
    {
        this.text = text;
        this.isChecked = isChecked;
        this.priority = priority;
    }

    public String getText()
    {
        return text;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}