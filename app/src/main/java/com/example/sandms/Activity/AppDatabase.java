package com.example.sandms.Activity;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.sandms.Interface.EventCaptureDao;
import com.example.sandms.Model.EventCaptureModel;

@Database(entities = {EventCaptureModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventCaptureDao taskDao();
}
