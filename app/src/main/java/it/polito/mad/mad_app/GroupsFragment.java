package it.polito.mad.mad_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.RecyclerTouchListener;


public class GroupsFragment extends Fragment {

    public class GroupModel implements Comparable<GroupModel> {
        String groupId;
        String groupName;
        String groupUrl;
        String lastOperation;
        String dateLastOperation;

        public GroupModel(String groupId, String groupName, String groupUrl, String lastOperation, String dateLastOperation) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.groupUrl = groupUrl;
            this.lastOperation = lastOperation;
            this.dateLastOperation = dateLastOperation;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupUrl() { return groupUrl; }

        public String getLastOperation() {return lastOperation; }

        public String getDateLastOperationWellFormed() {
            if(dateLastOperation != "") {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultdate = new Date(new Long(dateLastOperation));
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
            long o1, o2;
            if(date.equals(""))
                o1 = 0;
            else
                o1 = Long.valueOf(date);

            if(dateLastOperation.equals(""))
                o2 = 0;
            else
                o2 = Long.valueOf(dateLastOperation);

            return (int)(o1 - o2);
        }
    }
    private Context context;
    private List<GroupModel> groups = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String uKey = null;
    boolean emailVerified;

    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_groups, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_groups);

        auth = FirebaseAuth.getInstance();
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

                //if(currentFUser != null) {
                    //if (currentFUser.isEmailVerified()) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Users").child(uKey).child("Groups");

                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                if (map != null) {
                                    groups.clear();
                                    for (Map.Entry<String, Object> k : map.entrySet()) {
                                        String name = (String) ((Map<String, Object>) k.getValue()).get("name");
                                        String imagePath = (String) ((Map<String, Object>) k.getValue()).get("imagePath");
                                        String lastOperation = (String) ((Map<String, Object>) k.getValue()).get("lastOperation");
                                        String dateLastOperation = (String) ((Map<String, Object>) k.getValue()).get("dateLastOperation");
                                        if(lastOperation == null)
                                            lastOperation = "";
                                        if(dateLastOperation == null)
                                            dateLastOperation = "";
                                        Log.d("GROUPSFRAGMENT", "dati: "+name+ " "+lastOperation+" "+dateLastOperation);
                                        groups.add(new GroupModel(k.getKey(), name, imagePath, lastOperation, dateLastOperation));
                                        Collections.sort(groups);
                                        gAdapter.notifyDataSetChanged();
                                        progressBar.setVisibility(view.INVISIBLE);
                                    }
                                } else {
                                    progressBar.setVisibility(view.INVISIBLE);

                                    TextView tv = (TextView) getView().findViewById(R.id.noGroups);
                                    tv.setVisibility(view.VISIBLE);

                                    //Toast.makeText(getActivity(), "There are no groups!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                //log.w(TAG, "Failed to read value.", error.toException());
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
                    /*} else {

                        progressBar.setVisibility(view.INVISIBLE);
                        new AlertDialog.Builder(context)
                                .setTitle("Please verify your email!")
                                .setMessage("You have not verified your email yet. Please check your email or go to setting to send again confirmation email.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Button button = (Button) view.findViewById(R.id.relaod);
                                        button.setVisibility(view.VISIBLE);
                                        button.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                getFragmentManager().beginTransaction().detach(GroupsFragment.this).attach(GroupsFragment.this).commit();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Button button = (Button) view.findViewById(R.id.relaod);
                                        button.setVisibility(view.VISIBLE);
                                        button.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                getFragmentManager().beginTransaction().detach(GroupsFragment.this).attach(GroupsFragment.this).commit();
                                            }
                                        });
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }*/
//                }

            //}
        //};

        return view;
    }
}
