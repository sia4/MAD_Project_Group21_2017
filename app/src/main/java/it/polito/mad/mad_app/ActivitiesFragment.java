package it.polito.mad.mad_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.util.Log;
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
import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ActivitiesFragment extends Fragment {

    private List<ActivityData> activities = new ArrayList<>();
    private String groupName;
    private Map<String, Map<String, Object>> userGroups = new TreeMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_activities, container, false);

        Context context;
        context = view.getContext();
        activities.clear();

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.Activities);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        final ActivitiesAdapter aAdapter = new ActivitiesAdapter(activities);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(aAdapter);

        final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uId).child("Groups");

        //Retrive activities
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("Activities Fragment", "Dentro il listener - leggo le activities");
                Log.d("Activities Fragment", "Dentro il listener - Pulisco array");
                activities.clear();
                userGroups = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                Log.d("Activities Fragment", "Dentro il listener - Lista dei gruppi: "+ userGroups);

                if(userGroups != null) {

                    for (final String k : userGroups.keySet()){

                        Log.d("Activities Fragment", "Dentro il listener - Lista dei gruppi: "+ userGroups);
                        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Activities").child(k);
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Map<String, String>> activitiesMap = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                                groupName = (String)userGroups.get(k).get("name");

                                Log.d("Activities Fragment", "Dentro il listener - groupName: "+ groupName);

                                if(activitiesMap != null) {

                                    if(getView() != null) {
                                        Log.d("Activities Fragment", "Tolgo scritta no Activities");
                                        TextView tv = (TextView) getView().findViewById(R.id.noActivities);
                                        tv.setVisibility(GONE);
                                    }

                                    Log.d("Activities Fragment", "Dentro il listener - activities: "+ activitiesMap);

                                    String tmpc, tmpt, tmpd, tmpty, tmpid, tmpgid;
                                    for(String j : activitiesMap.keySet()){
                                        tmpc = activitiesMap.get(j).get("creator");
                                        tmpt = activitiesMap.get(j).get("text");
                                        tmpd = activitiesMap.get(j).get("date");
                                        tmpty = activitiesMap.get(j).get("type");
                                        tmpid = activitiesMap.get(j).get("itemId");
                                        tmpgid = activitiesMap.get(j).get("groupId");

                                        ActivityData a = new ActivityData(tmpc, tmpt, tmpd, tmpty, tmpid, tmpgid);
                                        a.setGroupName(groupName);
                                        activities.add(a);
                                    }
                                    Collections.sort(activities);
                                    aAdapter.notifyDataSetChanged();
                                } else{

                                    if(getView() != null) {
                                        TextView tv = (TextView) getView().findViewById(R.id.noActivities);
                                        tv.setVisibility(VISIBLE);
                                    }

                                    Log.d("Activities Fragment", "No activities list found");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                   aAdapter.notifyDataSetChanged();
                } else {

                    if(getView() != null) {
                        TextView tv = (TextView) getView().findViewById(R.id.noActivities);
                        tv.setVisibility(VISIBLE);
                    }

                    Log.d("Activities Fragment", "No group list found");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Activities Fragment", "Failed to read value.", error.toException());
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String type = activities.get(position).getType();
                if(type.equals("expense")||type.equals("contest")) {
                    Intent intent = new Intent().setClass(view.getContext(), ExpenseInfoActivity.class);
                    intent.putExtra("ExpenseId", activities.get(position).getItemId());
                    intent.putExtra("groupId", activities.get(position).getGroupId());
                    intent.putExtra("groupName", activities.get(position).getGroupName());
                    view.getContext().startActivity(intent);
                }
                if(type.equals("deletegroup")||type.equals("leavegroup")) {
                    Intent intent = new Intent().setClass(view.getContext(), PolActivity.class);
                    intent.putExtra("polId", activities.get(position).getItemId());
                    intent.putExtra("groupId", activities.get(position).getGroupId());
                    intent.putExtra("groupName", activities.get(position).getGroupName());
                    intent.putExtra("text", activities.get(position).getText());
                    intent.putExtra("type", activities.get(position).getType());
                    view.getContext().startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

}