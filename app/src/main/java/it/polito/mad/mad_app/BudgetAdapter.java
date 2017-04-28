package it.polito.mad.mad_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.Balance;
import it.polito.mad.mad_app.model.BalanceData;
import it.polito.mad.mad_app.model.MainData;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.MyViewHolder> {

    //private List<BalanceData> budgetData;
    private List<Balance> budgetData=new ArrayList<>();
    //private List<BalanceData> budgetDataC; //contiene i record in valute diverse


    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_cred_deb, value_cred_deb, b;
        public LinearLayout buttonContainer;
        public Button button;


        public MyViewHolder(View view) {
            super(view);
            name_cred_deb = (TextView) view.findViewById(R.id.name_cred_deb);
            value_cred_deb = (TextView) view.findViewById(R.id.value_cred_deb);
            //b=(Button) view.findViewById(R.id.Par);
            buttonContainer = (LinearLayout) view.findViewById(R.id.buttonContainer);

        }
    }


    public BudgetAdapter(List<Balance> budgetData/*, List<BalanceData> budgetCData*/) {
        this.budgetData = budgetData;
        //this.budgetDataC = budgetCData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cred_deb_item, parent, false);

        mContext = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
         final Balance budget = budgetData.get(position);
        float n=budget.getValue();
        holder.value_cred_deb.setText(String.format("%.2f", budget.getValue()) + " " /* budget.getCurrency()*/);
        if(n>0){
            //System.out.println(budget.getEmail() + "-->" + MainData.getInstance().findUserByMail(budget.getEmail()));
            //holder.name_cred_deb.setText(MainData.getInstance().findUserByMail(budget.getEmail()).getName() + " owns you:");
            holder.name_cred_deb.setText(budget.getName()+ " owns you:");
            holder.value_cred_deb.setTextColor(Color.parseColor("#27B011"));
            if(holder.button==null) {
                holder.button = new Button(mContext);
                holder.button.setText("Balance");
                holder.buttonContainer.addView(holder.button);
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Balance budget = budgetData.get(position);
                        Intent intent = new Intent().setClass(v.getContext(), BalanceActivity.class);
                        String uname = budget.getName()+ " owns you:";
                        //String umail = budget.getEmail();
                        String gname = budget.getName();
                        //String bValue = Float.toString(budget.getValue());
                        //String currency = budget.getCurrency();
                        intent.putExtra("gname", budget.getgID());
                        intent.putExtra("uKey", budget.getKey());
                        intent.putExtra("uname", budget.getName());
                        intent.putExtra("value", Float.toString(budget.getValue()));
                        //intent.putExtra("currency", currency);
                        v.getContext().startActivity(intent);
                    }
                });
            }
            /*holder.b.setVisibility(View.VISIBLE);
            holder.b.setText("Balance");
            holder.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent().setClass(v.getContext(), BalanceActivity.class);
                    String uname = budget.getName();
                    String gname = budget.getGName();
                    String bValue = Float.toString(budget.getValue());
                    String currency = budget.getCurrency();
                    intent.putExtra("gname",gname);
                    intent.putExtra("uname",uname);
                    intent.putExtra("value", bValue);
                    intent.putExtra("currency", currency);
                    v.getContext().startActivity(intent);
                }
            });*/
        }
        else{
            if(holder.button!=null){
                holder.buttonContainer.removeView(holder.button);
            }
            holder.value_cred_deb.setTextColor(Color.parseColor("#D51111"));
            holder.name_cred_deb.setText("You own to "+budget.getName()+":");
        }

    }

    @Override
    public int getItemCount() {
        return budgetData.size();
        //return 0;
    }
}