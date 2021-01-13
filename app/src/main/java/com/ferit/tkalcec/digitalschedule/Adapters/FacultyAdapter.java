package com.ferit.tkalcec.digitalschedule.Adapters;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ferit.tkalcec.digitalschedule.Classes.Faculty;
import com.ferit.tkalcec.digitalschedule.R;

import java.util.ArrayList;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.ViewHolder> {
    private static final String TAG = "FacultyAdapter";

    ArrayList<Faculty> mFaculties;

    private OnFacultiesListListener mOnFacultiesListListener;
    private OnFacultyContextMenuListener mOnFacultyContextMenuListener;

    public FacultyAdapter(ArrayList<Faculty> faculties, OnFacultiesListListener onFacultiesListListener, OnFacultyContextMenuListener onFacultyContextMenuListener) {
        this.mFaculties = faculties;
        this.mOnFacultiesListListener = onFacultiesListListener;
        this.mOnFacultyContextMenuListener = onFacultyContextMenuListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View facultyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faculty, parent, false);
        return new ViewHolder(facultyView, mOnFacultiesListListener, mOnFacultyContextMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Faculty faculty = this.mFaculties.get(position);
            holder.itemFacultyName.setText(faculty.getName());
            holder.studyOfFaculty.setText(faculty.getStudy());
        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder: Null pointer: " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return this.mFaculties.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView itemFacultyName, studyOfFaculty;

        public OnFacultiesListListener mOnFacultiesListListener;
        public OnFacultyContextMenuListener mOnFacultyContextMenuListener;

        public ViewHolder(View itemView, OnFacultiesListListener onFacultiesListListener, OnFacultyContextMenuListener onFacultyContextMenuListener) {
            super(itemView);
            this.itemFacultyName = (TextView) itemView.findViewById(R.id.tvItemFacultyName);
            this.studyOfFaculty = (TextView) itemView.findViewById(R.id.tvItemStudyOfFaculty);

            mOnFacultiesListListener = onFacultiesListListener;
            mOnFacultyContextMenuListener = onFacultyContextMenuListener;

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnFacultiesListListener.onFacultiesListClick(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            mOnFacultyContextMenuListener.onFacultyContextMenuListener(getAdapterPosition(), contextMenu);
        }
    }

    public void removeFaculty(int position) {
        if(mFaculties.size() > position) {
            mFaculties.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnFacultiesListListener {
        void onFacultiesListClick(int position);
    }

    public interface OnFacultyContextMenuListener {
        void onFacultyContextMenuListener(int position, ContextMenu menu);
    }

}
