package com.example.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements OnItemClickListener {

    private ArrayList<AudioRecord> records;
    private AudioListAdapter audioListAdapter;
    private AppDatabase appDatabase;
    private RecyclerView recyclerView;

    private TextInputEditText searchInput;
    private MaterialToolbar toolBarList;

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

        searchInput = (TextInputEditText) findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString();
                searchDatabase(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        toolBarList = (MaterialToolbar) findViewById(R.id.toolBarList);
        setSupportActionBar(toolBarList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolBarList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void searchDatabase(String query) {
        records.clear();
        List<AudioRecord> queryResult = appDatabase.audioRecordDao().searchDatabase("%" + query + "%");
        records.addAll(queryResult);
        runOnUiThread(
                () -> audioListAdapter.notifyDataSetChanged()
        );

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

        if (audioListAdapter.isEditMode()) {
            records.get(position).isChecked = !records.get(position).isChecked;
            audioListAdapter.notifyItemChanged(position);
        } else {
            Intent intent = new Intent(this, AudioPlayerActivity.class);
            intent.putExtra("filePath", record.filePath);
            intent.putExtra("fileName", record.fileName);
            startActivity(intent);
        }

    }

    @Override
    public void onItemLongClickListener(int position) {
        audioListAdapter.setEditMode(true);
        records.get(position).isChecked = !records.get(position).isChecked;
        audioListAdapter.notifyItemChanged(position);

    }
}