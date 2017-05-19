package it.polito.mad.mad_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.polito.mad.mad_app.model.RecyclerTouchListener;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class GroupsFragment extends Fragment {

    public class GroupModel implements Comparable<GroupModel> {
        String groupId;
        String groupName;
        String groupUrl;
        String lastOperation;
        String dateLastOperation;
        String favourite;

        public GroupModel(String groupId, String groupName, String groupUrl, String lastOperation, String dateLastOperation, String favourite) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.groupUrl = groupUrl;
            this.lastOperation = lastOperation;
            this.dateLastOperation = dateLastOperation;
            this.favourite = favourite;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupUrl() { return groupUrl; }
        public String getFavourite(){return favourite;}
        public String getLastOperation() {return lastOperation; }

        public String getDateLastOperationWellFormed() {
            if(!dateLastOperation.equals("")) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.ITALY);
                Date resultdate = new Date(Long.valueOf(dateLastOperation));
                return sdf.format(resultdate);
            } else {
                return dateLastOperation;
            }
        }

        public String getDateLastOperation() {
            return dateLastOperation;
        }

        @Override
        public int compareTo(@NonNull GroupModel o) {

            String date = o.getDateLastOperation();
            String fav = o.getFavourite();
            long o1, o2;
            if(date.equals("") || (fav!=null && fav.equals("yes")))
                o1 = 0;
            else
                o1 = Long.valueOf(date);

            if(dateLastOperation.equals("")|| (fav!=null && fav.equals("yes")))
                o2 = 0;
            else
                o2 = Long.valueOf(dateLastOperation);

            return (int)(o1 - o2);
        }
    }

    private List<GroupModel> groups = new ArrayList<>();

    String uKey = null;

    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context;
        final View view = inflater.inflate(R.layout.fragment_groups, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_groups);

        final FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFUser != null)
            uKey = currentFUser.getUid();

        context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.Groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        final GroupsAdapter gAdapter = new GroupsAdapter(getActivity(), groups);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uKey).child("Groups");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                if (map != null) {

                    if(getView() != null) {
                        Log.d("Groups Fragment", "Tolgo scritta no Groups");
                        TextView tv = (TextView) getView().findViewById(R.id.noGroups);
                        tv.setVisibility(GONE);
                    }

                    groups.clear();

                    Log.d("Groups Fragment", "Svuoto vettore gruppi");
                    for (Map.Entry<String, Object> k : map.entrySet()) {
                        String name = (String) ((Map<String, Object>) k.getValue()).get("name");
                        String imagePath = (String) ((Map<String, Object>) k.getValue()).get("imagePath");
                        String lastOperation = (String) ((Map<String, Object>) k.getValue()).get("lastOperation");
                        String dateLastOperation = (String) ((Map<String, Object>) k.getValue()).get("dateLastOperation");
                        String missing = (String) ((Map<String, Object>) k.getValue()).get("missing");
                        String favourite = (String) ((Map<String, Object>) k.getValue()).get("favourite");
                        if(lastOperation == null)
                            lastOperation = "";
                        if(dateLastOperation == null)
                            dateLastOperation = "";

                            Log.d("Groups Fragment", "dati: " + name + " " + lastOperation + " " + dateLastOperation);
                            if (missing == null) {
                                    groups.add(new GroupModel(k.getKey(), name, imagePath, lastOperation, dateLastOperation, favourite));
                            }
                            else {
                                if (missing.equals("no")){
                                        groups.add(new GroupModel(k.getKey(), name, imagePath, lastOperation, dateLastOperation, favourite));

                                }
                          }
                        Collections.sort(groups);


                        Log.d("Groups Fragment", "Notify Data Set Changed sull'adapter");
                        gAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(INVISIBLE);
                    }
                } else {
                    progressBar.setVisibility(INVISIBLE);

                    if(getView() != null) {
                        TextView tv = (TextView) getView().findViewById(R.id.noGroups);
                        tv.setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Groups Fragment", "Failed to read value.", error.toException());
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final String k = groups.get(position).getGroupId();
                final String n = groups.get(position).getGroupName();
                final String imagePath=groups.get(position).getGroupUrl();
                Intent intent = new Intent().setClass(view.getContext(), GroupActivity.class);

                intent.putExtra("groupId", k);
                intent.putExtra("groupName", n);
                intent.putExtra("imagePath", imagePath);
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }
}