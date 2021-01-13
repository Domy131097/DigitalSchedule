package com.ferit.tkalcec.digitalschedule.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ferit.tkalcec.digitalschedule.Classes.Lecture;
import com.ferit.tkalcec.digitalschedule.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleAdapter extends RecyclerView.Adapter <ScheduleAdapter.ViewHolder> {
    private static final String TAG = "LectureAdapter";
    private static final SimpleDateFormat lectureDateFormat = new SimpleDateFormat("HH:mm'h'");
    private static final SimpleDateFormat headerDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private ArrayList<Lecture> lectures = new ArrayList<>();
    private Context context;
    private Boolean scheduleHeaderVisible = false;

    private ScheduleAdapter.OnScheduleListClickListener mOnScheduleListClickListener;
    private ScheduleAdapter.OnScheduleContextMenuListener mOnScheduleContextMenuListener;

    public ScheduleAdapter(ArrayList<Lecture> lectures, ScheduleAdapter.OnScheduleListClickListener onScheduleListClickListener, ScheduleAdapter.OnScheduleContextMenuListener onScheduleContextMenuListener, boolean scheduleHeaderVisible) {
        this.lectures = lectures;
        this.mOnScheduleListClickListener = onScheduleListClickListener;
        this.mOnScheduleContextMenuListener = onScheduleContextMenuListener;
        this.scheduleHeaderVisible = scheduleHeaderVisible;
    } {

    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View scheduleView = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleAdapter.ViewHolder(scheduleView, mOnScheduleListClickListener, mOnScheduleContextMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        try {
            Lecture lecture = this.lectures.get(position);

            if(scheduleHeaderVisible) {
                holder.tvNameOfDay.setText(dayOfWeekInString(lecture.getStartTime()) + " " + headerDateFormat.format(lecture.getStartTime()));
                holder.rlScheduleHeader.setVisibility(View.VISIBLE);
            }

            holder.tvCourseName.setText(lecture.getCourseName());
            holder.tvLecturesType.setText(lecture.getTypeOfLecture());
            holder.tvLengthOfLectures.setText(getStringFromDate(lecture.getStartTime()) + " - " + getStringFromDate(lecture.getEndTime()));
            holder.tvLocationOfLectures.setText(lecture.getLocation());
            holder.tvHallOfLectures.setText(lecture.getHall());
            if (lecture.getTypeOfLecture().equalsIgnoreCase("Predavanje"))
                holder.llLectureListHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLecture));
            else if (lecture.getTypeOfLecture().equalsIgnoreCase("Laboratorijske vježbe"))
                holder.llLectureListHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorExercise));
            else if (lecture.getTypeOfLecture().equalsIgnoreCase("Auditorne vježbe"))
                holder.llLectureListHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAuditory));
            else if (lecture.getTypeOfLecture().equalsIgnoreCase("Konstrukcijske vježbe"))
                holder.llLectureListHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDesign));
            else if (lecture.getTypeOfLecture().equalsIgnoreCase("Kolokvij"))
                holder.llLectureListHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorExam));

        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder: Null pointer: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return this.lectures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public LinearLayout llLectureListHeader;
        public TextView tvCourseName, tvLecturesType, tvLengthOfLectures, tvLocationOfLectures, tvHallOfLectures;
        public RelativeLayout rlScheduleHeader;
        public TextView tvNameOfDay;

        public ScheduleAdapter.OnScheduleListClickListener mOnScheduleListClickListener;
        public ScheduleAdapter.OnScheduleContextMenuListener mOnScheduleContextMenuListener;

        public ViewHolder(View itemView, ScheduleAdapter.OnScheduleListClickListener onScheduleListClickListener, ScheduleAdapter.OnScheduleContextMenuListener onScheduleContextMenuListener) {
            super(itemView);

            this.llLectureListHeader = (LinearLayout) itemView.findViewById(R.id.lectureListHeaderLayout);
            this.tvCourseName = (TextView) itemView.findViewById(R.id.tvCourseName);
            this.tvLecturesType = (TextView) itemView.findViewById(R.id.tvLecturesType);
            this.tvLengthOfLectures = (TextView) itemView.findViewById(R.id.tvLengthOfLectures);
            this.tvLocationOfLectures = (TextView) itemView.findViewById(R.id.tvLocationOfLectures);
            this.tvHallOfLectures = (TextView) itemView.findViewById(R.id.tvHallOfLectures);
            this.rlScheduleHeader = (RelativeLayout) itemView.findViewById(R.id.rlScheduleHeader);
            this.tvNameOfDay = (TextView) itemView.findViewById(R.id.tvDayName);

            mOnScheduleListClickListener = onScheduleListClickListener;
            mOnScheduleContextMenuListener = onScheduleContextMenuListener;

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnScheduleListClickListener.onScheduleClickListener(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            mOnScheduleContextMenuListener.onScheduleContextMenuListener(getAdapterPosition(), contextMenu);
        }
    }

    public interface OnScheduleListClickListener {
        void onScheduleClickListener(int position);
    }

    public interface OnScheduleContextMenuListener {
        void onScheduleContextMenuListener(int adapterPosition, ContextMenu contextMenu);
    }

    public String getStringFromDate(Date dateToConvert){
        String date = lectureDateFormat.format(dateToConvert);
        return date;
    }

    private String dayOfWeekInString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String weekDay = null;

        if (Calendar.MONDAY == dayOfWeek) weekDay = "Ponedjeljak";
        else if (Calendar.TUESDAY == dayOfWeek) weekDay = "Utorak";
        else if (Calendar.WEDNESDAY == dayOfWeek) weekDay = "Srijeda";
        else if (Calendar.THURSDAY == dayOfWeek) weekDay = "Četvrtak";
        else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Petak";
        else if (Calendar.SATURDAY == dayOfWeek) weekDay = "Subota";
        else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Nedjelja";

        return weekDay;
    }
}
