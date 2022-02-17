package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Adapter;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends Activity {

    private ArrayList<AudioRecord> records;
    private AudioListAdapter audioListAdapter;
    private AppDatabase appDatabase;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        records = new ArrayList<>();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "audioRecords").allowMainThreadQueries().build();

        audioListAdapter = new AudioListAdapter(records);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(audioListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchAll();
    }

    private void fetchAll() {
        records.clear();
        List<AudioRecord> queryResult = appDatabase.audioRecordDao().getAll();
        records.addAll(queryResult);
        audioListAdapter.notifyDataSetChanged();
    }
}