package com.example.voicerecorder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AudioRecordDao {
    @Query("SELECT * FROM audioRecords")
    List<AudioRecord> getAll();

    @Query("SELECT * FROM audioRecords WHERE fileName LIKE :query")
    List<AudioRecord> searchDatabase(String query);

    @Insert
    void insert(AudioRecord audioRecord);

    @Delete
    void delete(AudioRecord audioRecord);

    @Delete
    void delete(ArrayList<AudioRecord> audioRecords);

    @Update
    void update(AudioRecord audioRecord);
}
