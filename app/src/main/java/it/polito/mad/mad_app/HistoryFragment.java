package it.polito.mad.mad_app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

public class    HistoryFragment extends Fragment {

    private static final int VERTICAL_ITEM_SPACE = 48;

    static private List<ExpenseData> lex = new ArrayList<>();
    private Context context;
    private String GroupName, myvalue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        context = view.getContext();
        lex.clear();

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.expenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
         //       DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new it.polito.mad.mad_app.DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                final ExpenseData expense = lex.get(position);
                Intent intent = new Intent().setClass(view.getContext(), ExpenseInfoActivity.class);

                String GroupId = getArguments().getString("GroupId");
                String GroupName = getArguments().getString("GroupName");
                intent.putExtra("groupName", GroupName);
                intent.putExtra("groupId", GroupId);
                intent.putExtra("ExpenseId", expense.getIdEx());
                view.getContext().startActivity(intent);
                getActivity().finish();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));




        GroupName = this.getArguments().getString("GroupId");

        System.out.println("H: " + GroupName);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Expenses").child(GroupName);


        final HistoryAdapter hAdapter = new HistoryAdapter(lex);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hAdapter);




        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lex.clear();
                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                if(map2!=null) {

                    //TODO questa cosa a me ogni tanto crusha dicendo che Attempt to invoke virtual method 'android.view.View android.view.View.findViewById(int)' on a null object reference
                    //TextView tv = (TextView) getView().findViewById(R.id.noExpenses);
                    //tv.setVisibility(view.GONE);
                    //lex = new ArrayList<ExpenseData>();
                    for (final String k : map2.keySet()){
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Expenses").child(GroupName).child(k);
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> map3 = (Map<String, Object>) dataSnapshot.getValue();
                                if(map3!=null) {
                                    //Float tmp = new Float(map3.get("value").toString());
                                    //Float tmp1 = new Float(map3.get("myvalue").toString());
                                    int flag = 0;
                                    for(ExpenseData ex : lex){
                                        if(ex.getIdEx().equals(k))
                                            flag =1;
                                    }
                                    if(flag!=1) {
                                        final ExpenseData e = new ExpenseData((String) map3.get("name"), (String) map3.get("description"), (String) map3.get("category"), (String) map3.get("currency"), map3.get("value").toString(), map3.get("myvalue").toString(), (String) map3.get("algorithm"));
                                        e.setCreator((String) map3.get("creator"));
                                        e.setIdEx(k);
                                        e.setDate((String) map3.get("date"));
                                        e.setContested((String) map3.get("contested"));
                                        if((String)map3.get("creatorId")!=null)
                                            e.setCreatorId((String)map3.get("creatorId"));

                                        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef2 = database2.getReference("Expenses").child(GroupName).child(k);
                                        myRef2.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> usermapTemp;
                                                usermapTemp = (Map<String, String>) dataSnapshot.getValue();
                                                System.out.println("USERMAPPPPPP "+usermapTemp);
                                                if(usermapTemp !=null) {
                                                    for(String h : usermapTemp.keySet()) {
                                                        if(h.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                            myvalue = usermapTemp.get(h);
                                                            e.setMyvalue(myvalue);
                                                            lex.add(e);
                                                            Collections.sort(lex);
                                                            hAdapter.notifyDataSetChanged();
                                                        }
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                                        System.out.println("valueeeeeeeeeeeeeeeeeeeeeeasdf" + map3.get("value"));

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }else {

                    TextView tv = (TextView) getView().findViewById(R.id.noExpenses);
                    tv.setVisibility(view.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return view;
    }


}