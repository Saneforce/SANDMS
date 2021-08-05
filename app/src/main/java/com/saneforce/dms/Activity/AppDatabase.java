
package com.saneforce.dms.Activity;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.saneforce.dms.Interface.EventCaptureDao;
import com.saneforce.dms.Model.EventCaptureModel;

@Database(entities = {EventCaptureModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventCaptureDao taskDao();
}
