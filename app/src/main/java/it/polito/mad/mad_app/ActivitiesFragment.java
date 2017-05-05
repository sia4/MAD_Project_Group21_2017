package it.polito.mad.mad_app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.BalanceData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.UserData;

public class ActivitiesFragment extends Fragment {

    private List<ActivityData> activities = new ArrayList<>();
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_activities, container, false);

        context = view.getContext();
        activities.clear();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(),
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        final ActivitiesAdapter aAdapter = new ActivitiesAdapter(activities);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(aAdapter);


        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uid).child("Groups");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activities.clear();
                Map<String, Object> map2 = (Map<String, Object>) dataSnapshot.getValue();
                System.out.println("group list: "+ map2);
                if(map2!=null) {
                    for (final String k : map2.keySet()){
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Activities").child(k);
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Map<String, String>> map3 = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                                if(map3!=null) {
                                    System.out.println("activitiesssssssssslist " + map3);
                                    String tmpc, tmpt, tmpd, tmpty;
                                    for(String j : map3.keySet()){
                                        tmpc = map3.get(j).get("creator");
                                        tmpt = map3.get(j).get("text");
                                        tmpd = map3.get(j).get("date");
                                        tmpty = map3.get(j).get("type");

                                        activities.add(new ActivityData(tmpc, tmpt, tmpd, tmpty));
                                    }
                                    aAdapter.notifyDataSetChanged();
                                }
                                else{
                                    System.out.println("no activities list found");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                   aAdapter.notifyDataSetChanged();
                }
                else
                {
                    System.out.println("no group list  found");
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