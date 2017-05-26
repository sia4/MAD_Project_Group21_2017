package it.polito.mad.mad_app;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.ExpenseData;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<ExpenseData> expenseData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_ex, data_ex,  money_ex,creator_ex, contested_ex;

        public MyViewHolder(View view) {
            super(view);
            name_ex = (TextView) view.findViewById(R.id.name_ex);
            data_ex = (TextView) view.findViewById(R.id.data_ex);
            money_ex = (TextView) view.findViewById(R.id.money_ex);
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
        System.out.println("HistoryAdapter L49 - " + expense.toString());
        holder.name_ex.setText(expense.getName());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date resultdate = new Date(new Long(expense.getDate()));

        holder.data_ex.setText(sdf.format(resultdate));

        Currencies c_tmp = new Currencies();
        String symbol = c_tmp.getCurrencySymbol(expense.getCurrency());

        if (symbol != null) {
            holder.money_ex.setText(expense.getValue() + " " + symbol);
        } else {
            holder.money_ex.setText(expense.getValue());
        }
        //if (expense.getCreator().equals())
        holder.creator_ex.setText(expense.getCreator());

        if(expense.getContested() != null && expense.getContested().equals("yes")) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E8D1D1"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return expenseData.size();
    }
}