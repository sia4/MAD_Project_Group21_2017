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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.GroupData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

import static android.content.ContentValues.TAG;

//import it.polito.mad.mad_app.model.RecyclerItemClickListener;


public class GroupsFragment extends Fragment {


    private Context context;
    private List<GroupData> d = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_groups, container, false);

        context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        /*recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(),
                android.support.v7.widget.DividerItemDecoration.VERTICAL));*/
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        //d = MainData.getInstance().getGroupList();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("groups");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                d = MainData.getInstance().getGroupList();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        GroupsAdapter gAdapter = new GroupsAdapter(d);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final GroupData g = d.get(position);
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
