package it.polito.mad.mad_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.BalanceData;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.MyViewHolder> {

    private List<ActivityData> activityData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, text;
        public Button accept;
        public MyViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date_act);
            text = (TextView) view.findViewById(R.id.text_act);
            accept = (Button) view.findViewById(R.id.AcceptActivity);
        }
    }


    public ActivitiesAdapter(List<ActivityData> activityData) {
        this.activityData = activityData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ActivityData activity = activityData.get(position);


        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(new Long(activity.getDate()));

        holder.date.setText(sdf.format(resultdate));
        holder.text.setText(activity.getText().toString());
        if(activity.getType().equals("deletegroup")||activity.getType().equals("leavegroup")){
            holder.accept.setVisibility(View.VISIBLE);
        }
        else{
            holder.accept.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return activityData.size();
    }
}