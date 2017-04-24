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

import it.polito.mad.mad_app.model.Group;
import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

import static android.content.ContentValues.TAG;

//import it.polito.mad.mad_app.model.RecyclerItemClickListener;


public class GroupsFragment extends Fragment {


    private Context context;
    private List<Group> d = new ArrayList<>();

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
        uKey = currentFUser.getUid();

        context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.Groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        //d = MainData.getInstance().getGroupList();

        final GroupsAdapter gAdapter = new GroupsAdapter(d);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gAdapter);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(uKey).child("Groups");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                for(String k : map.keySet()) {
                    System.out.println("Value is: " + k);
                    FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                    DatabaseReference myRef2 = database2.getReference("Groups").child(k);
                    myRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            System.out.println("Value is: " + map.toString());
                            d = new ArrayList<>();
                            //Log.d(TAG, "Value is: " + value);
                            Group g = new Group((String)map.get("name"),(String) map.get("surname"), (String)map.get("defaultCurrency"));
                            d.add(g);
                            //if(progressBar.isActivated())
                                progressBar.setVisibility(View.INVISIBLE);
                            gAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            //log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

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
                final Group g = d.get(position);
                Intent intent = new Intent().setClass(view.getContext(), GroupActivity.class);
                String groupName = g.getName();
                intent.putExtra("name",groupName);
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return view;
    }

}
