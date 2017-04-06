package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import it.polito.mad.mad_app.model.*;

import static android.R.attr.data;


public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    private List<GroupData> GData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView impact_pos;
        public TextView impact_neg;
        public ImageView im;

        public MyViewHolder(View view) {
            super(view);
            name= (TextView) view.findViewById(R.id.name_tv);
            impact_pos= (TextView) view.findViewById(R.id.impact_pos_ex);
            impact_neg= (TextView) view.findViewById(R.id.impact_neg_ex);
            im=(ImageView) view.findViewById(R.id.im);
        }
    }


    public GroupsAdapter(List<GroupData> expensiveData) {
        this.GData = expensiveData;
    }

    @Override
    public GroupsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);

        return new GroupsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupsAdapter.MyViewHolder holder, int position) {
        final GroupData g = GData.get(position);
        holder.name.setText(g.getName());
        holder.im.setImageResource(R.drawable.group_default);
        //holder.im.setImageResource(R.drawable.casa);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent().setClass(v.getContext(), GroupActivity.class);
                String groupName = g.getName();
                intent.putExtra("name",groupName);
                v.getContext().startActivity(intent);
            }
        });
        holder.impact_pos.setText("You owe:"+String.format("%.2f", g.getNegExpenses()));//TODO insert the correct value
        holder.impact_neg.setText("They owe you:"+String.format("%.2f",g.getPosExpenses()));//TODO insert the correct value
        holder.impact_neg.setTextColor(Color.parseColor("#27B011"));
        holder.impact_pos.setTextColor(Color.parseColor("#D51111"));
    }

    @Override
    public int getItemCount() {
        return GData.size();
    }
}