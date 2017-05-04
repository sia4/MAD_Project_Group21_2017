package it.polito.mad.mad_app;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.MainData;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<ExpenseData> expenseData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_ex, data_ex,  money_ex, impact_ex,creator_ex, contested_ex;

        public MyViewHolder(View view) {
            super(view);
            name_ex = (TextView) view.findViewById(R.id.name_ex);
            data_ex = (TextView) view.findViewById(R.id.data_ex);
            money_ex = (TextView) view.findViewById(R.id.money_ex);
            impact_ex = (TextView) view.findViewById(R.id.impact_ex);
            creator_ex = (TextView) view.findViewById(R.id.creator_ex);
            contested_ex = (TextView) view.findViewById(R.id.contested_ex);
        }
    }


    public HistoryAdapter(List<ExpenseData> expenseData) {
        this.expenseData = expenseData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ex_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ExpenseData expense = expenseData.get(position);
        holder.name_ex.setText(expense.getName());

        holder.data_ex.setText(expense.getDate());
        holder.money_ex.setText(String.format("%.2f", expense.getMyvalue()));
        holder.creator_ex.setText(expense.getCreator());
        holder.impact_ex.setTextColor(Color.parseColor("#27B011"));
        holder.impact_ex.setText("They owe you:");
        holder.money_ex.setTextColor(Color.parseColor("#27B011"));
        if(expense.getContested().equals("yes"))
            holder.contested_ex.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return expenseData.size();
    }
}