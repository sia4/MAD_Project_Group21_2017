package it.polito.mad.mad_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.polito.mad.mad_app.model.ExpensiveData;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<ExpensiveData> expensiveData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_ex, currency_ex, description_ex,  money_ex, category_ex, algorithm_ex;

        public MyViewHolder(View view) {
            super(view);
            name_ex = (TextView) view.findViewById(R.id.name_ex);
            currency_ex = (TextView) view.findViewById(R.id.currency_ex);
            description_ex = (TextView) view.findViewById(R.id.description_ex);
            money_ex = (TextView) view.findViewById(R.id.money_ex);
            category_ex = (TextView) view.findViewById(R.id.category_ex);
            algorithm_ex = (TextView) view.findViewById(R.id.algorithm_ex);
        }
    }


    public HistoryAdapter(List<ExpensiveData> expensiveData) {
        this.expensiveData = expensiveData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ex_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ExpensiveData expense = expensiveData.get(position);
        holder.name_ex.setText(expense.getName());
        holder.currency_ex.setText(expense.getCurrency());
        holder.description_ex.setText(expense.getDescription());
        holder.money_ex.setText(String.valueOf(expense.getValue()));
        holder.category_ex.setText(expense.getCategory());
        holder.algorithm_ex.setText(expense.getAlgorithm());
    }

    @Override
    public int getItemCount() {
        return expensiveData.size();
    }
}