package it.polito.mad.mad_app;

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.Currencies;
import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.ImageMethod;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<ExpenseData> expenseData;
    private int mExpandedPosition=-1;
    private ViewGroup view_x;
    private ViewGroup viewgroup;
    private Map<String, Integer> catToId = new TreeMap<>();
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_ex, data_ex,  money_ex,creator_ex, yourslice,descrip_ex,algorithm_ex;
        public ImageView imCat;
        public ImageView iPhoto;
        public CardView card_view;
        public LinearLayout ll,first_ll;
        public RelativeLayout second_ll;
        public Button b,b_contested;
        public ImageView arrow_down,arrow_up;
        public boolean isExpanded;
        public Map<String, Map<String, Map<String, Object>>> balancemap;
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
            arrow_down=(ImageView) view.findViewById(R.id.arrow_down);
            arrow_up=(ImageView) view.findViewById(R.id.arrow_up);
            yourslice=(TextView) view.findViewById(R.id.your_slice);
            first_ll=(LinearLayout) view.findViewById(R.id.first_ll);
            second_ll=(RelativeLayout) view.findViewById(R.id.second_ll);
            isExpanded=false;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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
        holder.arrow_down.setImageResource(R.drawable.ic_action_name);
        holder.arrow_up.setImageResource(R.drawable.ic_action_up);
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
        String key=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String,String>u =expense.getUsers();
        holder.yourslice.setText(u.get(key));
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
            ImageMethod.square_image(holder.iPhoto.getContext(),holder.iPhoto,expense.getImagePath());
            //Glide.with(holder.iPhoto.getContext()).load(expense.getImagePath()).into(holder.iPhoto);
        }
        //final boolean isExpanded = position==mExpandedPosition;
        holder.ll.setVisibility(holder.isExpanded?View.VISIBLE:View.GONE);
        if(!holder.isExpanded){
            holder.arrow_up.setVisibility(View.INVISIBLE);
            holder.arrow_down.setVisibility(View.VISIBLE);
        }else {
            holder.arrow_down.setVisibility(View.INVISIBLE);
            holder.arrow_up.setVisibility(View.VISIBLE);
        }
        //holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(!holder.isExpanded) {
                    holder.arrow_up.setVisibility(View.VISIBLE);
                    holder.arrow_down.setVisibility(View.INVISIBLE);
                    holder.ll.setVisibility(View.VISIBLE);
                    holder.isExpanded=true;
                }else{
                    holder.arrow_down.setVisibility(View.VISIBLE);
                    holder.arrow_up.setVisibility(View.INVISIBLE);
                    holder.ll.setVisibility(View.GONE);
                    holder.isExpanded=false;
                }
            }
        });
        holder.b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Expenses").child(expense.getGroupId());
                myRef.child(expense.getIdEx()).child("contested").setValue("yes");
                DatabaseReference myRef2 = myRef.push();


                myRef2.setValue(new ExpenseData(expense.getName() + "(retrieve)", "expense retrieved", expense.getCategory(), expense.getCurrency(),expense.getValue(), "0.00", expense.getAlgorithm(), expense.getDefaultcurrency()));
                myRef2.child("creator").setValue(expense.getCreator());
                myRef2.child("missing").setValue("yes");
                //myRef2.child("value").setValue(value);
                myRef2.child("contested").setValue("no");
                myRef2.child("users").setValue(expense.getUsers());


                DatabaseReference getMyName = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getMyName.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map <String, Object> mapname = (Map<String, Object>) dataSnapshot.getValue();
                        System.out.println("mapnameeeeeeeeeeeeeeeeeeee"+mapname);
                        if(mapname!=null) {
                            String myname = (String)mapname.get("name");
                            String mysurname = (String)mapname.get("surname");
                            DatabaseReference ActRef = database.getReference("Activities");
                            for(String k : expense.getUsers().keySet()) {
                                if(!k.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    ActRef.child(k).push().setValue(new ActivityData(myname + " " + mysurname, myname + " " + mysurname + " contested expense " + expense.getName() + " in group " + expense.getGroupName(), Long.toString(System.currentTimeMillis()), "contest", expense.getIdEx(), expense.getGroupId()));
                            }
                        }
                        else{
                            Log.d("ExpenseInfo", "balancemapppppppppppppppp " +expense.getUsers());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                DatabaseReference myRef3 = database3.getReference("Balance").child(expense.getGroupId());

                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.balancemap = (Map<String, Map<String, Map<String, Object>>>) dataSnapshot.getValue();
                        if(holder.balancemap==null) {
                            //Toast.makeText(ExpenseInfoActivity.this, "no balance found!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            final FirebaseDatabase database5 = FirebaseDatabase.getInstance();
                            DatabaseReference myRef5 = database5.getReference("Balance").child(expense.getGroupId());
                            float value1, value2, value3, value4;
                            for( Map.Entry<String, String> e : expense.getUsers().entrySet()) {
                                if(!e.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                    value1 = Float.parseFloat((String)holder.balancemap.get(FirebaseAuth.getInstance().getCurrentUser().getUid()).get(e.getKey()).get("value"));
                                    value2 = Float.parseFloat((String)holder.balancemap.get(e.getKey()).get(FirebaseAuth.getInstance().getCurrentUser().getUid()).get("value"));
                                    //value3 = new Float (Float.parseFloat(usermap.get(k).toString()));
                                    System.out.println("buttonnnnnnn1 "+value1);
                                    System.out.println("buttonnnnnnn2 "+value2);
                                    //System.out.println("buttonnnnnnn3 "+value3);
                                    System.out.println("USERMAPPPPPPP" + e.getValue());
                                    value3 =Float.parseFloat(e.getValue());
                                    value1 = value1 - value3;
                                    value2 = value2 + Float.parseFloat(e.getValue());
                                    myRef5.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(e.getKey()).child("value").setValue(String.format(Locale.US, "%.2f",value1));
                                    myRef5.child(e.getKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("value").setValue(String.format(Locale.US, "%.2f",value2));


                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        }

    @Override
    public int getItemCount() {
        return expenseData.size();
    }
}