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

import com.ferit.tkalcec.digitalschedule.Classes.Exam;
import com.ferit.tkalcec.digitalschedule.Classes.Lecture;
import com.ferit.tkalcec.digitalschedule.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExamsAdapter extends RecyclerView.Adapter <ExamsAdapter.ViewHolder> {
    private static final String TAG = "LectureAdapter";
    private static final SimpleDateFormat examDateFormat = new SimpleDateFormat("HH:mm'h'");
    private static final SimpleDateFormat headerDateFormat = new SimpleDateFormat("dd.MM.yyyy.");

    private ArrayList<Exam> exams = new ArrayList<>();
    private Context context;
    private Boolean examsHeaderVisible;

    private ExamsAdapter.OnExamsListClickListener mOnExamsListClickListener;
    private ExamsAdapter.OnExamsContextMenuListener mOnExamsContextMenuListener;

    public ExamsAdapter(ArrayList<Exam> exams, ExamsAdapter.OnExamsListClickListener onExamsListClickListener, ExamsAdapter.OnExamsContextMenuListener onExamsContextMenuListener, boolean examsHeaderVisible) {
        this.exams = exams;
        this.mOnExamsListClickListener = onExamsListClickListener;
        this.mOnExamsContextMenuListener = onExamsContextMenuListener;
        this.examsHeaderVisible = examsHeaderVisible;
    } {

    }

    @NonNull
    @Override
    public ExamsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View examsView = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ExamsAdapter.ViewHolder(examsView, mOnExamsListClickListener, mOnExamsContextMenuListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsAdapter.ViewHolder holder, int position) {
        try {
            Exam exam = this.exams.get(position);

            if(examsHeaderVisible) {
                holder.tvNameOfDay.setText(dayOfWeekInString(exam.getStartTime()) + " " + headerDateFormat.format(exam.getStartTime()));
                holder.rlScheduleHeader.setVisibility(View.VISIBLE);
            }

            holder.tvCourseName.setText(exam.getCourseName());
            holder.tvLecturesType.setText(exam.getType());
            holder.tvLengthOfLectures.setText(getStringFromDate(exam.getStartTime()));
            holder.tvLocationOfLectures.setText(exam.getLocation());
            holder.tvHallOfLectures.setText(exam.getHall());
            holder.llLectureListHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder: Null pointer: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return this.exams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public LinearLayout llLectureListHeader;
        public TextView tvCourseName, tvLecturesType, tvLengthOfLectures, tvLocationOfLectures, tvHallOfLectures;
        public RelativeLayout rlScheduleHeader;
        public TextView tvNameOfDay;

        public ExamsAdapter.OnExamsListClickListener mOnExamsListClickListener;
        public ExamsAdapter.OnExamsContextMenuListener mOnExamsContextMenuListener;

        public ViewHolder(View itemView, ExamsAdapter.OnExamsListClickListener onExamsListClickListener, ExamsAdapter.OnExamsContextMenuListener onExamsContextMenuListener) {
            super(itemView);

            this.llLectureListHeader = (LinearLayout) itemView.findViewById(R.id.lectureListHeaderLayout);
            this.tvCourseName = (TextView) itemView.findViewById(R.id.tvCourseName);
            this.tvLecturesType = (TextView) itemView.findViewById(R.id.tvLecturesType);
            this.tvLengthOfLectures = (TextView) itemView.findViewById(R.id.tvLengthOfLectures);
            this.tvLocationOfLectures = (TextView) itemView.findViewById(R.id.tvLocationOfLectures);
            this.tvHallOfLectures = (TextView) itemView.findViewById(R.id.tvHallOfLectures);
            this.rlScheduleHeader = (RelativeLayout) itemView.findViewById(R.id.rlScheduleHeader);
            this.tvNameOfDay = (TextView) itemView.findViewById(R.id.tvDayName);

            mOnExamsListClickListener = onExamsListClickListener;
            mOnExamsContextMenuListener = onExamsContextMenuListener;

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnExamsListClickListener.onExamsClickListener(getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            mOnExamsContextMenuListener.onExamsContextMenuListener(getAdapterPosition(), contextMenu);
        }
    }

    public interface OnExamsListClickListener {
        void onExamsClickListener(int position);
    }

    public interface OnExamsContextMenuListener {
        void onExamsContextMenuListener(int adapterPosition, ContextMenu contextMenu);
    }

    public String getStringFromDate(Date dateToConvert){
        String date = examDateFormat.format(dateToConvert);
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
