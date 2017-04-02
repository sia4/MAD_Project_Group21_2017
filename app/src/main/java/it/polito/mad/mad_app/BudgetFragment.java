package it.polito.mad.mad_app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BudgetFragment extends Fragment {

    private ArrayList<cred_deb> users = new ArrayList<>();
    private Context context;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_budget, container, false);

        context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        users.add(new cred_deb("Luca", "25€"));
        users.add(new cred_deb("Luca", "-48€"));
        users.add(new cred_deb("Silvia", "27€"));
        users.add(new cred_deb("Lucia", "81€"));

        BudgetAdapter bAdapter = new BudgetAdapter(users);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bAdapter);

        return view;
    }

}