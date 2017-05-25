package it.polito.mad.mad_app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.mad.mad_app.model.Balance;
import it.polito.mad.mad_app.model.BalanceData;

public class BudgetFragment extends Fragment {

    private List<Balance> users=new ArrayList<>();
    private List<BalanceData> other_currencies;
    private Context context;
    private String uKey;
    BudgetAdapter bAdapter;
    /*
    class cred_deb {
        private String name;
        private String value;

        public cred_deb(String name,String value) {
            this.name = name;
            this.value=value;
        }
        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_budget, container, false);

        context = view.getContext();
        //String GroupName = getArguments().getString("GroupName");
        final String groupId = getArguments().getString("GroupId");
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(),
                android.support.v7.widget.DividerItemDecoration.VERTICAL));
        FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(currentFUser != null) {
            uKey = currentFUser.getUid();
        }

        //users = MainData.getInstance().getGroup(GroupName).getExpensesList();
        //other_currencies = MainData.getInstance().getGroup(GroupName).getExpensesListC();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/Balance/"+groupId+"/"+uKey);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map != null) {
                    users.clear();
                    for (Map.Entry<String, Object> k : map.entrySet()) {
                        String name = (String) ((Map<String, Object>) k.getValue()).get("name");
                        float value;
                        if (((Map<String, Object>) k.getValue()).get("value") instanceof Double) {

                            Double v = (Double) ((Map<String, Object>) k.getValue()).get("value");
                            value = v.floatValue();
                            users.add(new Balance(k.getKey(), name, value, groupId));
                            bAdapter.notifyDataSetChanged();

                        } else {

                            String v = (String) ((Map<String, Object>) k.getValue()).get("value");
                            if (v != null) {

                                value = Float.parseFloat(v);
                                users.add(new Balance(k.getKey(), name, value, groupId));
                                bAdapter.notifyDataSetChanged();

                            } else {
                                System.out.println("ERROR Budget Fragment: " + "Line 95 - " + k.toString()); //TODO ATTENZIONE
                            }

                        }

                        //progressBar.setVisibility(view.INVISIBLE);
                        //System.out.println("Value is: " + map.get("u1"));
                        //Log.d(TAG, "Value is: " + value);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        bAdapter = new BudgetAdapter(users/*, other_currencies*/);//TODO
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*String GroupName = getArguments().getString("GroupName");

        users = MainData.getInstance().getGroup(GroupName).getExpensesList();*/
        //bAdapter.notifyDataSetChanged();

    }
}