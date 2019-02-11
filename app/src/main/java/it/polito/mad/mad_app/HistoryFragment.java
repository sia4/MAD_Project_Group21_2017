package it.polito.mad.mad_app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

public class    HistoryFragment extends Fragment {

    private static final int VERTICAL_ITEM_SPACE = 48;

    static private List<ExpenseData> lex = new ArrayList<>();
    private Map<String, ExpenseData> m_lex = new TreeMap<>();
    private Context context;
    private ViewGroup viewgroup;
    private HistoryAdapter hAdapter;
    private String GroupName, myvalue,GName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        viewgroup=container;
        context = view.getContext();
        lex.clear();
        hAdapter = new HistoryAdapter(context,lex,viewgroup);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.expenses);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hAdapter);
        hAdapter.notifyDataSetChanged();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //recyclerView.addItemDecoration(new it.polito.mad.mad_app.DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                final ExpenseData expense = lex.get(position);
                Intent intent = new Intent().setClass(view.getContext(), ExpenseInfoActivity.class);

                String GroupId = getArguments().getString("GroupId");
                String GroupName = getArguments().getString("GroupName");
                System.out.println("groupName HistoryFragment: "+ GroupName);
                intent.putExtra("groupName", GroupName);
                intent.putExtra("groupId", GroupId);
                intent.putExtra("ExpenseId", expense.getIdEx());
                view.getContext().startActivity(intent);
                //getActivity().finish();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

        GroupName = this.getArguments().getString("GroupId");
        GName = this.getArguments().getString("GroupName");

        System.out.println("H: " + GroupName);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Expenses").child(GroupName);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hAdapter.notifyDataSetChanged();
                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                if(map2!=null) {

                    //TODO questa cosa a me ogni tanto crusha dicendo che Attempt to invoke virtual method 'android.view.View android.view.View.findViewById(int)' on a null object reference
                    if(getView() != null) {
                        TextView tv = (TextView) getView().findViewById(R.id.noExpenses);
                        tv.setVisibility(view.GONE);
                    }

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
                                    String missing = (String) map3.get("missing");
                                    if (missing == null || missing.equals("no")) {
                                        System.out.println("currency" + (String) map3.get("currency"));
                                        final ExpenseData e = new ExpenseData((String) map3.get("name"), (String) map3.get("description"), (String) map3.get("category"), (String) map3.get("currency"), map3.get("value").toString(), map3.get("myvalue").toString(), (String) map3.get("algorithm"), (String) map3.get("defaultcurrency"));
                                        e.setCreator((String) map3.get("creator"));
                                        if(map3.get("imagePath")!=null){
                                            e.setImagePath((String) map3.get("imagePath"));
                                        }
                                        e.setUsers((Map<String, String>) map3.get("users"));
                                        e.setIdEx(k);
                                        e.setDate((String) map3.get("date"));
                                        e.setContested((String) map3.get("contested"));
                                        e.setGroupId(GroupName);
                                        e.setGroupName(GName);
                                        //System.out.print("currency from e: " + e.getCurrencyRow());

                                        if ((String) map3.get("creatorId") != null)
                                            e.setCreatorId((String) map3.get("creatorId"));

                                        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef2 = database2.getReference("Expenses").child(GroupName).child(k);
                                        myRef2.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, String> usermapTemp;
                                                usermapTemp = (Map<String, String>) dataSnapshot.getValue();
                                                System.out.println("USERMAPPPPPP " + usermapTemp);
                                                if (usermapTemp != null) {
                                                    for (String h : usermapTemp.keySet()) {
                                                        if (h.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                            myvalue = usermapTemp.get(h);
                                                            e.setMyvalue(myvalue);
                                                            m_lex.put(k, e);
                                                            lex.clear();
                                                            lex.addAll(m_lex.values());
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

                                    // hAdapter.notifyDataSetChanged();
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

              //  hAdapter.notifyDataSetChanged();
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