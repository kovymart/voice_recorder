package com.example.voicerecorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private File[] allFiles;
    private TimeAgo timeAgo;
    private List<AudioRecord> records;


    public AudioListAdapter(List<AudioRecord> records) {
        this.records = records;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkbox;
        private TextView tvFileName;
        private TextView tvMeta;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvMeta = itemView.findViewById(R.id.tvMeta);
        }

//        @Override
//        public void onClick(View v) {
//            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition());
//        }
    }


    @NonNull
    @Override
    public AudioListAdapter.AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioListAdapter.AudioViewHolder holder, int position) {
//        holder.list_title.setText(allFiles[position].getName());
//        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }


//    public interface onItemListClick {
//        void onClickListener(File file, int position);
//    }
}
