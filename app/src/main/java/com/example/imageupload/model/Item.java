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
    private int priority;

    // GEORGINA: adding a due date field for me to process in the Calendar
    // using a Long so that I can enter due dates as a timestamp in milliseconds
    public Long dueAt;

    // no arg constructor for ObjectBox
    public Item(){}

    public Item(String text, boolean isChecked, int priority)
    {
        this.text = text;
        this.isChecked = isChecked;
        this.priority = priority;
        // GEORGINA: adding a default value so that old entries without this field still work
        this.dueAt = null;
    }

    // GEORGINA: adding a second constructor that takes in a due date dueAt
    public Item(String text, boolean isChecked, int priority, Long dueAt){
        this.text = text;
        this.isChecked = isChecked;
        this.priority = priority;
        this.dueAt = dueAt;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    // GEORGINA: adding getter and setter methods for dueAt field
    public Long getDueAt(){
        return dueAt;
    }
    public void setDueAt(Long dueAt){
        this.dueAt = dueAt;
    }
}