package com.example.voicerecorder;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AudioRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AudioRecordDao audioRecordDao();
}
