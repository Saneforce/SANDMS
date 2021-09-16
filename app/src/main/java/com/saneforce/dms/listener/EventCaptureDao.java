package com.saneforce.dms.listener;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.saneforce.dms.model.EventCaptureModel;

import java.util.List;

@Dao
public interface EventCaptureDao {

    @Query("SELECT * FROM eventCaptureModel")
    List<EventCaptureModel> getAll();

    @Insert
    void insert(EventCaptureModel task);

    @Delete
    void delete(EventCaptureModel task);


    @Update
    void update(EventCaptureModel task);

}
