package com.ferit.tkalcec.digitalschedule.Adapters;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ferit.tkalcec.digitalschedule.Classes.Course;
import com.ferit.tkalcec.digitalschedule.R;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter <CourseAdapter.ViewHolder> {

    private static final String TAG = "CourseAdapter";

    private ArrayList<Course> courses = new ArrayList();

    private OnCourseListClickListener mOnCourseListClickListener;
    private OnCourseContextMenuListener mOnCourseContextMenuListener;

    public CourseAdapter(ArrayList<Course> courses, OnCourseListClickListener onCourseListClickListener, OnCourseContextMenuListener onCourseContextMenuListener) {
        this.courses = courses;
        this.mOnCourseListClickListener = onCourseListClickListener;
        this.mOnCourseContextMenuListener = onCourseContextMenuListener;
    } {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View courseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(courseView, mOnCourseListClickListener, mOnCourseContextMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Course course = this.courses.get(position);
            holder.itemCourseName.setText(course.getName());
            holder.itemHolderOfCourse.setText(course.getHolder());
        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder: Null pointer: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return this.courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView itemCourseName, itemHolderOfCourse;

        public OnCourseListClickListener mOnCourseListClickListener;
        public OnCourseContextMenuListener mOnCourseContextMenuListener;

        public ViewHolder(View itemView, OnCourseListClickListener onCourseListClickListener, OnCourseContextMenuListener onCourseContextMenuListener) {
            super(itemView);

            this.itemCourseName = (TextView) itemView.findViewById(R.id.tvItemCourseName);
            this.itemHolderOfCourse = (TextView) itemView.findViewById(R.id.tvItemHolderOfCourse);

            mOnCourseListClickListener = onCourseListClickListener;
            mOnCourseContextMenuListener = onCourseContextMenuListener;

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnCourseListClickListener.onCourseClickListener(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            mOnCourseContextMenuListener.onCourseContextMenuListener(getAdapterPosition(), contextMenu);
        }
    }

    public void removeCourse(int position) {
        if(courses.size() > position) {
            courses.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnCourseListClickListener {
        void onCourseClickListener(int position);
    }

    public interface OnCourseContextMenuListener {
        void onCourseContextMenuListener(int adapterPosition, ContextMenu contextMenu);
    }
}
