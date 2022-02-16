package com.example.voicerecorder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "audioRecords")
public class AudioRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo
    public String fileName;
    @ColumnInfo
    public String filePath;
    @ColumnInfo
    public Long timestamp;
    @ColumnInfo
    public String duration;
    @ColumnInfo
    public String ampsPath;

    @Ignore
    public boolean isChecked = false;

    public AudioRecord() {

    }

    public AudioRecord(String newFileName, String filePath, long timestamp, String duration, String ampsPath) {
        this.fileName = newFileName;
        this.filePath = filePath;
        this.timestamp = timestamp;
        this.duration = duration;
        this.ampsPath = ampsPath;
    }

}
