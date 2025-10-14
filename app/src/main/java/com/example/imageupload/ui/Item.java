package com.example.imageupload.ui;

// each checklist item along with boolean functionality
public class Item {
    private String text;
    private boolean isChecked;

    public Item(String text, boolean isChecked)
    {
        this.text = text;
        this.isChecked = isChecked;
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
}