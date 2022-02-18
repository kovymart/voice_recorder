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
import java.util.ArrayList;
import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private TimeAgo timeAgo;
    private ArrayList<AudioRecord> records;
    private OnItemClickListener onItemClickListener;
    private boolean editMode= false;


    public AudioListAdapter(ArrayList<AudioRecord> records, OnItemClickListener onItemClickListener) {
        this.records = records;
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean mode) {
        if(editMode != mode) {
            this.editMode = mode;
            notifyDataSetChanged();
        }
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private CheckBox checkbox;
        private TextView tvFileName;
        private TextView tvMeta;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClickListener(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemLongClickListener(position);
            }
            return true;
        }
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
        if (position != RecyclerView.NO_POSITION) {
            AudioRecord record = records.get(position);
            holder.tvFileName.setText(record.fileName);
            String strTime = timeAgo.getTimeAgo(record.timestamp);
            holder.tvMeta.setText(String.format("%s %s", record.duration, strTime));
            if (editMode) {
                holder.checkbox.setVisibility(View.VISIBLE);
                holder.checkbox.setChecked(record.isChecked);
            } else {
                holder.checkbox.setVisibility(View.GONE);
                holder.checkbox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }
}
