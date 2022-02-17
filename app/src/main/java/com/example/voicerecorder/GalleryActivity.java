package com.example.voicerecorder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends Activity implements OnItemClickListener {

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

        audioListAdapter = new AudioListAdapter(records, this);
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

    @Override
    public void onItemClickListener(int position) {
        AudioRecord record = records.get(position);
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("filePath", record.filePath);
        intent.putExtra("fileName", record.fileName);
        startActivity(intent);
    }

    @Override
    public void onItemLongClickListener(int position) {
        Toast.makeText(this, "long", Toast.LENGTH_SHORT).show();

    }
}