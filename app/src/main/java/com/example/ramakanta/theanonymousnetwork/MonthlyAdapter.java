package com.example.ramakanta.theanonymousnetwork;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Created by daduc on 27-03-2018.
 */

public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.MonthlyViewHolder> {
    List<MonthlyAttendance> l;
    public MonthlyAdapter(List<MonthlyAttendance> l) {
        this.l=l;
    }
    public static class MonthlyViewHolder extends RecyclerView.ViewHolder{
        TextView subText,attText,totalText;
        public MonthlyViewHolder(View itemView) {
            super(itemView);
            subText=itemView.findViewById(R.id.month_attendance_subject);
            attText=itemView.findViewById(R.id.month_attendance_class_attended);
            totalText=itemView.findViewById(R.id.month_attendance_total_class);
        }
        public void setAllData(String sub,long att,long total){
            subText.setText(sub);
            attText.setText(att+"");
            totalText.setText(total+"");
        }
    }

    @Override
    public MonthlyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.month_attendance_layout, parent, false);

        return new MonthlyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MonthlyViewHolder holder, int position) {
        MonthlyAttendance m=l.get(position);
        holder.setAllData(m.getSubject(),m.getAtt(),m.getTotal());
    }

    @Override
    public int getItemCount() {
        return l.size();
    }
}
