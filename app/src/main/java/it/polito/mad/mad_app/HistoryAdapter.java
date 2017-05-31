package it.polito.mad.mad_app;

import android.graphics.Color;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.ImageMethod;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<ExpenseData> expenseData;
    private int mExpandedPosition=-1;
    private ViewGroup view_x;
    private ViewGroup viewgroup;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_ex, data_ex,  money_ex,creator_ex, contested_ex,descrip_ex,algorithm_ex;
        public ImageView imCat;
        public ImageView iPhoto;
        public CardView card_view;
        public LinearLayout ll;
        public Button b,b_contested;

        public MyViewHolder(View view) {
            super(view);
            view_x=(ViewGroup)view;
            name_ex = (TextView) view.findViewById(R.id.name_ex);
            data_ex = (TextView) view.findViewById(R.id.data_ex);
            money_ex = (TextView) view.findViewById(R.id.money_ex);
            imCat=(ImageView) view.findViewById(R.id.imCat);
            ll=(LinearLayout) view.findViewById(R.id.info);
            creator_ex = (TextView) view.findViewById(R.id.creator_ex);
            descrip_ex=(TextView) view.findViewById(R.id.descrip_ex);
            algorithm_ex=(TextView) view.findViewById(R.id.algorithm_ex);
            card_view=(CardView) view.findViewById(R.id.card_view);
            b=(Button) view.findViewById(R.id.exDeny);
            b_contested=(Button) view.findViewById(R.id.exContested);
            iPhoto=(ImageView) view.findViewById(R.id.iPhoto);
            //contested_ex = (TextView) view.findViewById(R.id.contested_ex);
        }
    }


    public HistoryAdapter(List<ExpenseData> expenseData,ViewGroup viewGroup) {
        this.expenseData = expenseData;
        this.viewgroup=viewGroup;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ex_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Map<String, Integer> catToId = new TreeMap<>();
        catToId.put("Entertainment", R.drawable.entertainment);
        catToId.put("Food and Drinks", R.drawable.food);
        catToId.put("House and Utilities", R.drawable.house);
        catToId.put("Clothing", R.drawable.clothing);
        catToId.put("Present", R.drawable.present);
        catToId.put("Medical Expenses", R.drawable.medical);
        catToId.put("Transport", R.drawable.transportation);
        catToId.put("Hotel", R.drawable.hotel);
        catToId.put("Cleaning", R.drawable.cleaning);
        catToId.put("General", R.drawable.general);
        catToId.put("Other", R.drawable.other);
        final ExpenseData expense = expenseData.get(position);
        System.out.println("HistoryAdapter L49 - " + expense.toString());
        holder.name_ex.setText(expense.getName());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date resultdate = new Date(new Long(expense.getDate()));
        PrettyTime prettyTime = new PrettyTime(Locale.US);
        String ago = prettyTime.format(resultdate);
        holder.data_ex.setText(ago);

        Currencies c_tmp = new Currencies();
        String symbol = c_tmp.getCurrencySymbol(expense.getCurrency());

        if (symbol != null) {
            holder.money_ex.setText(expense.getValue() + " " + symbol);
        } else {
            holder.money_ex.setText(expense.getValue());
        }
        holder.algorithm_ex.setText(expense.getAlgorithm());
        if(!expense.getDescription().equals("")){
            holder.descrip_ex.setText(expense.getDescription());
        }
        else{
            holder.descrip_ex.setVisibility(View.GONE);
        }
        holder.creator_ex.setText(expense.getCreator());
        int id=catToId.get(expense.getCategory());
        holder.imCat.setImageDrawable(ResourcesCompat.getDrawable(view_x.getResources(),id,null));
        if(expense.getContested() != null && expense.getContested().equals("yes")) {
            holder.b_contested.setVisibility(View.VISIBLE);
            holder.card_view.setBackgroundColor(Color.parseColor("#F2E2E2"));
            holder.b.setVisibility(View.GONE);
        } else {
        }
        if(expense.getImagePath()!=null){
            Log.d("HistoryAdapter","La foto la vede"+expense.getImagePath());
            Glide.with(holder.iPhoto.getContext()).load(expense.getImagePath()).into(holder.iPhoto);
        }
        final boolean isExpanded = position==mExpandedPosition;
        holder.ll.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition(viewgroup);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenseData.size();
    }
}