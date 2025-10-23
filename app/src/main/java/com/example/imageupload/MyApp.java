package com.example.imageupload;

import android.app.Application;
import io.objectbox.BoxStore;
import com.example.imageupload.model.MyObjectBox;

public class MyApp extends Application {

    private static BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize ObjectBox
        boxStore = MyObjectBox.builder()
                .androidContext(this)
                .build();

        System.out.println("ObjectBox initialized: " + boxStore.getNativeStore());
    }

    public static BoxStore getBoxStore() {
        return boxStore;
    }
}
