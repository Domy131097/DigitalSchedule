package com.ferit.tkalcec.digitalschedule.Adapters;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ferit.tkalcec.digitalschedule.Classes.Exam;
import com.ferit.tkalcec.digitalschedule.Classes.Lecture;
import com.ferit.tkalcec.digitalschedule.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExamAdapter extends RecyclerView.Adapter <ExamAdapter.ViewHolder> {
    private static final String TAG = "ExamAdapter";
    private static final SimpleDateFormat examDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private ArrayList<Exam> exams;

    private ExamAdapter.OnExamListClickListener mOnExamListClickListener;
    private ExamAdapter.OnExamContextMenuListener mOnExamContextMenuListener;

    public ExamAdapter(ArrayList<Exam> exams, OnExamListClickListener onExamListClickListener, OnExamContextMenuListener onExamContextMenuListener) {
        this.exams = exams;
        this.mOnExamListClickListener = onExamListClickListener;
        this.mOnExamContextMenuListener = onExamContextMenuListener;
    }

    @NonNull
    @Override
    public ExamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View examView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam, parent, false);
        return new ExamAdapter.ViewHolder(examView, mOnExamListClickListener, mOnExamContextMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamAdapter.ViewHolder holder, int position) {
        try {
            Exam exam = this.exams.get(position);
            holder.itemCourseName.setText(exam.getCourseName());
            holder.itemDateOfExam.setText(exam.getType() + " - " + getStringFromDate(exam.getStartTime()));
        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder: Null pointer: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return this.exams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView itemCourseName, itemDateOfExam;

        public ExamAdapter.OnExamListClickListener mOnExamListClickListener;
        public ExamAdapter.OnExamContextMenuListener mOnExamContextMenuListener;

        public ViewHolder(View itemView, ExamAdapter.OnExamListClickListener onExamListClickListener, ExamAdapter.OnExamContextMenuListener onExamContextMenuListener) {
            super(itemView);

            this.itemCourseName = (TextView) itemView.findViewById(R.id.tvItemCourseName);
            this.itemDateOfExam = (TextView) itemView.findViewById(R.id.tvItemDateOfExam);

            mOnExamListClickListener = onExamListClickListener;
            mOnExamContextMenuListener = onExamContextMenuListener;

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnExamListClickListener.onExamClickListener(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            mOnExamContextMenuListener.onExamContextMenuListener(getAdapterPosition(), contextMenu);
        }
    }

    public void removeExam(int position) {
        if(exams.size() > position) {
            exams.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnExamListClickListener {
        void onExamClickListener(int position);
    }

    public interface OnExamContextMenuListener {
        void onExamContextMenuListener(int adapterPosition, ContextMenu contextMenu);
    }

    public String getStringFromDate(Date dateToConvert){
        String date = examDateFormat.format(dateToConvert);
        return date;
    }
}
