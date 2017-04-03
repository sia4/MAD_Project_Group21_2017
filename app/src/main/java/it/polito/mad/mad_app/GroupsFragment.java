package it.polito.mad.mad_app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.*;


public class GroupsFragment extends Fragment {

    private Context context;
    private List<GroupData> d = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_groups, container, false);

        // Set the adapter
        //if (view instanceof RecyclerView) {
        context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(),
                android.support.v7.widget.DividerItemDecoration.VERTICAL));

        //lv = (ListView) view.findViewById(R.id.lv_ex);

        MainData.addGroup("Coinquilini", "Gruppo di via Tolmino 7");
        MainData.addGroup("Regalo di laurea", "Regalo per Martina");
        MainData.addGroup("Colleghi", "Spese dell'ufficio");

        d = MainData.getGroupList();
        GroupsAdapter gAdapter = new GroupsAdapter(d);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(gAdapter);
        return view;
    }
}