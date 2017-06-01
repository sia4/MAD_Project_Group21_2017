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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class GroupsFragment extends Fragment {
    static private EditText search_text;

    private Map<String, Map<String, Object>> balancemap = new TreeMap<>();
    public class GroupModel implements Comparable<GroupModel> {
        String groupId;
        String groupName;
        String groupUrl;
        String lastOperation;
        String dateLastOperation;
        String favourite;
        String archive;
        public GroupModel(String groupId, String groupName, String groupUrl, String lastOperation, String dateLastOperation, String favourite, String archive) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.groupUrl = groupUrl;
            this.lastOperation = lastOperation;
            this.dateLastOperation = dateLastOperation;
            this.favourite = favourite;
            this.archive = archive;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupUrl() { return groupUrl; }
        public String getFavourite(){return favourite;}
        public String getArchive(){return archive;}
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

        public String getRelativeData(){
            if(!dateLastOperation.equals("")) {
                PrettyTime prettyTime = new PrettyTime(Locale.US);
                String ago = prettyTime.format(new Date(Long.parseLong(dateLastOperation)));
                return ago;
            }
            else
            {
                return dateLastOperation;
            }


        }
        @Override
        public int compareTo(@NonNull GroupModel o) {

            long weight = 100;
            String date = o.getDateLastOperation();
            String fav = o.getFavourite();
            long o1, o2;
            if(date.equals(""))
                o1 = 0;
            else
                o1 = Long.valueOf(date);

            if(dateLastOperation.equals("")|| (fav!=null && fav.equals("yes")))
                o2 = 0;
            else
                o2 = Long.valueOf(dateLastOperation);

            System.out.println("normal time: "+(o1-o2));
            return (int) (o1 - o2);
        }
    }

    private List<GroupModel> groups = new ArrayList<>();
    private List<GroupModel> fav_groups = new ArrayList<>();
    private List<GroupModel> no_fav_groups = new ArrayList<>();
    private Map<String, String> groupsId = new TreeMap<>();
    String uKey = null;

    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context;
        final View view = inflater.inflate(R.layout.fragment_groups, container, false);
        final View viewMain = inflater.inflate(R.layout.app_bar_user_info, container);
        search_text = (EditText) viewMain.findViewById(R.id.search_text);
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
        final DatabaseReference myRef = database.getReference("Users").child(uKey).child("Groups");

        System.out.println("search_text: "+ MainData.getInstance().getGroupsFragmentData());

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
                    no_fav_groups.clear();
                    fav_groups.clear();
                    groupsId.clear();
                    MainData.getInstance().clearBalanceByGroup();

                    Log.d("Groups Fragment", "Svuoto vettore gruppi");
                    for (Map.Entry<String, Object> k : map.entrySet()) {

                        String name = (String) ((Map<String, Object>) k.getValue()).get("name");

                        String imagePath = (String) ((Map<String, Object>) k.getValue()).get("imagePath");
                        String lastOperation = (String) ((Map<String, Object>) k.getValue()).get("lastOperation");
                        String dateLastOperation = (String) ((Map<String, Object>) k.getValue()).get("dateLastOperation");
                        String missing = (String) ((Map<String, Object>) k.getValue()).get("missing");
                        String favourite = (String) ((Map<String, Object>) k.getValue()).get("favourite");
                        String archive = (String) ((Map<String, Object>) k.getValue()).get("archive");
                        if (lastOperation == null)
                            lastOperation = "";
                        if (dateLastOperation == null)
                            dateLastOperation = "";
                        if (missing != null && missing.equals("no")) {
                            System.out.println("missing:  "+missing);
                            final String tmpkey = k.getKey();
                            MainData.getInstance().clearBalanceByGroupByKey(tmpkey);
                            final FirebaseDatabase database3 = FirebaseDatabase.getInstance();
                            DatabaseReference myRef3 = database3.getReference("Balance").child(tmpkey).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            System.out.println("Balance" + "> " + tmpkey + " > " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                            groupsId.put(k.getKey(), name);
                            myRef3.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    balancemap = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                                    if (balancemap != null) {
                                        System.out.println("GroupsFragment L198 +" + balancemap);
                                        for (String u : balancemap.keySet()) {

                                            if (balancemap.get(u).get("value") != null) {
                                                float tttt = Float.parseFloat(balancemap.get(u).get("value").toString());
                                                MainData.getInstance().addToBalanceByGroup(tttt, tmpkey);
                                            }

                                        }

                                    } else {
                                        MainData.getInstance().addToBalanceByGroup(0, tmpkey);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        System.out.println("mappa per statistiche:   " + balancemap);

                        Log.d("Groups Fragment", "dati: " + name + " " + lastOperation + " " + dateLastOperation);
                        if((archive == null &&MainData.getInstance().getGroupFragmentArchive().equals("yes"))|| (archive!=null && !archive.equals(MainData.getInstance().getGroupFragmentArchive())))
                        {
                            if (name.contains(MainData.getInstance().getGroupsFragmentData())) {
                                if (missing == null) {
                                    if (favourite != null && favourite.equals("yes"))
                                        fav_groups.add(new GroupModel(k.getKey(), name, imagePath, lastOperation, dateLastOperation, favourite, archive));

                                    else
                                        no_fav_groups.add(new GroupModel(k.getKey(), name, imagePath, lastOperation, dateLastOperation, favourite, archive));
                                } else {
                                    if ((missing != null && missing.equals("no"))) {
                                        if (favourite != null && favourite.equals("yes"))
                                            fav_groups.add(new GroupModel(k.getKey(), name, imagePath, lastOperation, dateLastOperation, favourite, archive));
                                        else
                                            no_fav_groups.add(new GroupModel(k.getKey(), name, imagePath, lastOperation, dateLastOperation, favourite, archive));

                                    }
                                }
                            }

                        }

                        Collections.sort(no_fav_groups);
                        Collections.sort(fav_groups);
                        groups.clear();
                        groups.addAll(fav_groups);
                        groups.addAll(no_fav_groups);
                        MainData.getInstance().setMyGroupsId(groupsId);
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
                final String archive = groups.get(position).getArchive();
                Intent intent = new Intent().setClass(view.getContext(), GroupActivity.class);

                intent.putExtra("groupId", k);
                intent.putExtra("groupName", n);
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("archive", archive);
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                /*
                Button archive = (Button) viewMain.findViewById(R.id.archive_button);
                archive.setVisibility(viewMain.VISIBLE);
                archive.setVisibility(View.VISIBLE);
                archive.setVisibility(view.VISIBLE);
                */
            }
        }));

        return view;
    }
}