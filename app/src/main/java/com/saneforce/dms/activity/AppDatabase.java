
package com.saneforce.dms.activity;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.saneforce.dms.listener.EventCaptureDao;
import com.saneforce.dms.model.EventCaptureModel;

@Database(entities = {EventCaptureModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventCaptureDao taskDao();
}
