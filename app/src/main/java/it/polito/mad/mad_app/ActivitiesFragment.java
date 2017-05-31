package it.polito.mad.mad_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private Map<String, ActivityData> m_activities = new TreeMap<>();
    private String groupName;
    private boolean read=false;
    private Map<String,Map<String,Object>> act_read=new TreeMap<>();
    private Map<String, Map<String, Object>> userGroups = new TreeMap<>();
    private  Map<String, Map<String, String>> activitiesMap;
    private Map<String, Integer> actToId = new TreeMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_activities, container, false);


        actToId.put("addgroup", R.drawable.ic_notifica_groupadd); // aggiunto membro al gruppo
        actToId.put("contest", R.drawable.ic_notifica_expensecontested); // spesa contestata
        actToId.put("deletegroup", R.drawable.ic_notifica_groupdelete); //proposta
        actToId.put("leavegroup", R.drawable.ic_notifica_groupleave); //proposta
        actToId.put("expense", R.drawable.ic_notifica_expensenew);
        actToId.put("acceptleavegroup", R.drawable.ic_notifica_groupleave);
        actToId.put("leavedgroup", R.drawable.ic_notifica_groupleave);
        actToId.put("acceptdeletegroup", R.drawable.ic_notifica_groupdeleted);
        actToId.put("deletedgroup", R.drawable.ic_notifica_groupdelete);
        actToId.put("welcome", R.mipmap.logo);


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

                        final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                        DatabaseReference myRef3 = database3.getReference("Activities").child(uId);
                        myRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                activitiesMap = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                                Log.d("Activities Fragment", "Dentro il listener - groupName: "+ groupName);

                                if(activitiesMap != null) {

                                    if(getView() != null) {
                                        Log.d("Activities Fragment", "Tolgo scritta no Activities");
                                        TextView tv = (TextView) getView().findViewById(R.id.noActivities);
                                        tv.setVisibility(GONE);
                                    }

                                    Log.d("Activities Fragment", "Dentro il listener - activities: "+ activitiesMap);
                                    FirebaseDatabase db_read=FirebaseDatabase.getInstance();
                                    DatabaseReference read_r=db_read.getReference().child("ActivitiesRead").child(uId);
                                    read_r.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            act_read=(Map<String,Map<String,Object>>) dataSnapshot.getValue();
                                            if(act_read!=null){

                                                String tmpc, tmpt, tmpd, tmpty, tmpid, tmpgid, value;
                                                int imgId=0;
                                                int toRead = 0;
                                                for(final String j : activitiesMap.keySet()){
                                                    tmpc = activitiesMap.get(j).get("creator");
                                                    tmpt = activitiesMap.get(j).get("text");
                                                    tmpd = activitiesMap.get(j).get("date");
                                                    tmpty = activitiesMap.get(j).get("type");
                                                    tmpid = activitiesMap.get(j).get("itemId");
                                                    tmpgid = activitiesMap.get(j).get("groupId");
                                                    value = activitiesMap.get(j).get("itemValue");
                                                    System.out.println("tipo notifica: "+tmpty);
                                                    if(tmpty!=null)
                                                        imgId = actToId.get(tmpty);
                                                    final ActivityData a = new ActivityData(tmpc, tmpt, tmpd, tmpty, tmpid, tmpgid, imgId, value);
                                                    Log.d("ActivitiesFragment","cerco di capire"+act_read.toString());
                                                    if(act_read.get(tmpgid)!=null){
                                                        if(act_read.get(tmpgid).get(j)!=null&&(boolean)act_read.get(tmpgid).get(j)==true){
                                                            a.setRead(true);
                                                        }else
                                                        {
                                                            a.setRead(false);
                                                        }
                                                    }
                                                    a.setGroupName(groupName);
                                                    a.setActivityId(j);
                                                    m_activities.put(j, a);
                                                }
                                                String ss = String.format("activities to read: %d", toRead);
                                                System.out.println(ss);
                                                activities.clear();
                                                act_read.clear();
                                                activities.addAll(m_activities.values());
                                                Collections.sort(activities);
                                                aAdapter.notifyDataSetChanged();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
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


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FirebaseDatabase.getInstance().getReference("ActivitiesRead").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(activities.get(position).getGroupId()).child(activities.get(position).getActivityId()).setValue(true);

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