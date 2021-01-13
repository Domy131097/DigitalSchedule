package com.ferit.tkalcec.digitalschedule.Adapters;


import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ferit.tkalcec.digitalschedule.Classes.Lecture;
import com.ferit.tkalcec.digitalschedule.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LectureAdapter extends RecyclerView.Adapter <LectureAdapter.ViewHolder> {
    private static final String TAG = "LectureAdapter";
    private static final SimpleDateFormat lectureDateFormat = new SimpleDateFormat("dd.MM.yyyy. - HH:mm'h'");

    private ArrayList<Lecture> lectures;

    private LectureAdapter.OnLectureListClickListener mOnLectureListClickListener;
    private LectureAdapter.OnLectureContextMenuListener mOnLectureContextMenuListener;

    public LectureAdapter(ArrayList<Lecture> lectures, LectureAdapter.OnLectureListClickListener onLectureListClickListener, LectureAdapter.OnLectureContextMenuListener onLectureContextMenuListener) {
        this.lectures = lectures;
        this.mOnLectureListClickListener = onLectureListClickListener;
        this.mOnLectureContextMenuListener = onLectureContextMenuListener;
    }

    @NonNull
    @Override
    public LectureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View lectureView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture, parent, false);
        return new LectureAdapter.ViewHolder(lectureView, mOnLectureListClickListener, mOnLectureContextMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureAdapter.ViewHolder holder, int position) {
        try {
            Lecture lecture = this.lectures.get(position);
            holder.itemCourseName.setText(lecture.getCourseName());
            holder.itemDateOfLecture.setText(getStringFromDate(lecture.getStartTime()));
        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder: Null pointer: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return this.lectures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView itemCourseName, itemDateOfLecture;

        public LectureAdapter.OnLectureListClickListener mOnLectureListClickListener;
        public LectureAdapter.OnLectureContextMenuListener mOnLectureContextMenuListener;

        public ViewHolder(View itemView, LectureAdapter.OnLectureListClickListener onLectureListClickListener, LectureAdapter.OnLectureContextMenuListener onLectureContextMenuListener) {
            super(itemView);

            this.itemCourseName = (TextView) itemView.findViewById(R.id.tvItemCourseName);
            this.itemDateOfLecture = (TextView) itemView.findViewById(R.id.tvItemDateOfLecture);

            mOnLectureListClickListener = onLectureListClickListener;
            mOnLectureContextMenuListener = onLectureContextMenuListener;

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnLectureListClickListener.onLectureClickListener(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            mOnLectureContextMenuListener.onLectureContextMenuListener(getAdapterPosition(), contextMenu);
        }
    }

    public void removeLecture(int position) {
        if(lectures.size() > position) {
            lectures.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnLectureListClickListener {
        void onLectureClickListener(int position);
    }

    public interface OnLectureContextMenuListener {
        void onLectureContextMenuListener(int adapterPosition, ContextMenu contextMenu);
    }

    public String getStringFromDate(Date dateToConvert){
        String date = lectureDateFormat.format(dateToConvert);
        return date;
    }
}
