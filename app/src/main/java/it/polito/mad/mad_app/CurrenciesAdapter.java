package it.polito.mad.mad_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CurrenciesAdapter extends RecyclerView.Adapter<CurrenciesAdapter.MyViewHolder> {

    private List<String> Names = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name_c);
        }
    }


    public CurrenciesAdapter(List<String> currencies) {
        //this.currencies = currencies;
        Names.addAll(currencies);
    }

    @Override
    public CurrenciesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_item, parent, false);

        return new CurrenciesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CurrenciesAdapter.MyViewHolder holder, int position) {
        String c = Names.get(position);
        //String change = currencies.get(c).toString();
        holder.name.setText(c);
    }

    @Override
    public int getItemCount() {
        return Names.size();
    }
}
