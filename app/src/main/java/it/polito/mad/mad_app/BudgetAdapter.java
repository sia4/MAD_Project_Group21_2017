package it.polito.mad.mad_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.polito.mad.mad_app.model.Balance;
import it.polito.mad.mad_app.model.Currencies;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.MyViewHolder> {

    //private List<BalanceData> budgetData;
    private List<Balance> budgetData=new ArrayList<>();
    private String DefaultCurrency;
    private String gKey;
    //private List<BalanceData> budgetDataC; //contiene i record in valute diverse


    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_cred_deb, value_cred_deb, b;
        public LinearLayout buttonContainer;
        public Button button;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name_cred_deb = (TextView) view.findViewById(R.id.name_cred_deb);
            value_cred_deb = (TextView) view.findViewById(R.id.value_cred_deb);
            //b=(Button) view.findViewById(R.id.Par);
            buttonContainer = (LinearLayout) view.findViewById(R.id.buttonContainer);
            imageView = (ImageView) view.findViewById(R.id.imageBudget);
        }
    }


    public BudgetAdapter(List<Balance> budgetData, String DefaultCurrency, String gKey) {
        this.budgetData = budgetData;
        this.DefaultCurrency = DefaultCurrency;
        this.gKey = gKey;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Balance budget = budgetData.get(position);
        final float n=budget.getValue();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(budget.getKey());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map!=null) {
                    String name = (String) map.get("name");
                    String surname = (String) map.get("surname");
                    String p = (String) map.get("imagePath");

                    if(p==null)
                        holder.imageView.setImageResource(R.drawable.group_default);
                    else {
                        final Context context = holder.imageView.getContext();
                        Glide.with(context).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);

                                holder.imageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    }

                    if (n > 0) {
                        holder.name_cred_deb.setText(name + " " + surname +" owns you:");
                    } else {
                        holder.name_cred_deb.setText("You own to " + name + " " + surname + ":");
                    }

                    if (DefaultCurrency == null) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef2 = database.getReference("Groups").child(gKey).child("primaryCurrency");
                        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String pC = (String) dataSnapshot.getValue();
                                if (pC != null) {
                                    DefaultCurrency = pC;
                                    Currencies tmp = new Currencies();
                                    String symbol = tmp.getCurrencySymbol(DefaultCurrency);
                                    holder.value_cred_deb.setText(String.format(Locale.US, "%.2f", budget.getValue()) + symbol);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                    } else {

                        Currencies tmp = new Currencies();
                        String symbol = tmp.getCurrencySymbol(DefaultCurrency);
                        holder.value_cred_deb.setText(String.format(Locale.US, "%.2f", budget.getValue()) + symbol);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(n>0){
            //System.out.println(budget.getEmail() + "-->" + MainData.getInstance().findUserByMail(budget.getEmail()));
            //holder.name_cred_deb.setText(MainData.getInstance().findUserByMail(budget.getEmail()).getName() + " owns you:");

            //holder.name_cred_deb.setText(budget.getName()+ " owns you:");
            holder.value_cred_deb.setTextColor(Color.parseColor("#27B011"));
            if(holder.button==null) {
                holder.button = new Button(mContext);
                holder.button.setText("Balance");
                holder.buttonContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
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
                        Currencies tmp = new Currencies();
                        String symbol = tmp.getCurrencySymbol(DefaultCurrency);
                        intent.putExtra("defaultcurrency", symbol);
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

        }

    }

    @Override
    public int getItemCount() {
        return budgetData.size();
        //return 0;
    }
}