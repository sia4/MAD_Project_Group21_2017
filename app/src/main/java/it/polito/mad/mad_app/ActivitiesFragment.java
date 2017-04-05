package it.polito.mad.mad_app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.ActivityData;
import it.polito.mad.mad_app.model.BalanceData;
import it.polito.mad.mad_app.model.MainData;

public class ActivitiesFragment extends Fragment {

    private List<ActivityData> activities;
    private Context context;
    /*
    class cred_deb {
        private String name;
        private String value;

        public cred_deb(String name,String value) {
            this.name = name;
            this.value=value;
        }
        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_activities, container, false);

        context = view.getContext();

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        /*
        users.add(new cred_deb("Edo", "25€"));
        users.add(new cred_deb("Luca", "-48€"));
        users.add(new cred_deb("Silvia", "27€"));
        users.add(new cred_deb("Lucia", "81€"));
        */

        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(),
                android.support.v7.widget.DividerItemDecoration.VERTICAL));
        activities = MainData.getInstance().getActivitiesList();
        ActivitiesAdapter aAdapter = new ActivitiesAdapter(activities);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(aAdapter);

        return view;
    }

}