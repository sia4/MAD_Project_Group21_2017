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
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.TreeMap;

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.RecyclerTouchListener;


public class GroupsFragment extends Fragment {

    public class GroupModel {
        String groupId;
        String groupName;

        public GroupModel(String groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }
    }
    private Context context;
    private List<GroupModel> groups = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String uKey = null;

    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_groups, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_groups);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentFUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(currentFUser != null)
            uKey = currentFUser.getUid();
        System.out.println("utente "+ uKey.toString());

        context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.Groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        final GroupsAdapter gAdapter = new GroupsAdapter(groups);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uKey).child("Groups");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                //System.out.println("lista "+dList.toString());
                //dList = new ArrayList<>();
                //System.out.println("lista "+dList.toString());
                //dKey = new ArrayList<>();
                if(map != null) {
                    for (Map.Entry<String, Object> k : map.entrySet()) {

                        System.out.println("Value is: " + k);

                        groups.add(new GroupModel(k.getKey(), (k.getValue().toString())));
                        gAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(view.INVISIBLE);
                    }
                } else {
                    progressBar.setVisibility(view.INVISIBLE);
                    Toast.makeText(getActivity(), "There are no groups!", Toast.LENGTH_LONG).show();
                }
                //Log.d(TAG, "Value is: " + value);
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
                Intent intent = new Intent().setClass(view.getContext(), GroupActivity.class);

                intent.putExtra("groupId",k);
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return view;
    }

}
