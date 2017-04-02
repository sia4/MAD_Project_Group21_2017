package it.polito.mad.mad_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mad.mad_app.model.*;

public class HistoryFragment extends Fragment {

    private static final int VERTICAL_ITEM_SPACE = 48;

    private List<ExpensiveData> data = new ArrayList<>();
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Set the adapter
        //if (view instanceof RecyclerView) {
        context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //lv = (ListView) view.findViewById(R.id.lv_ex);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        //add ItemDecoration
        //recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        //or
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        //or
        //recyclerView.addItemDecoration(
        //        new DividerItemDecoration(getActivity(), R.drawable.divider));

        data = GroupData.getExpensies();
        HistoryAdapter hAdapter = new HistoryAdapter(data);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hAdapter);
        /*BaseAdapter a=new BaseAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null){
                    convertView=getActivity().getLayoutInflater().inflate(R.layout.ex_item,parent,false);

                }
                TextView name=(TextView)convertView.findViewById(R.id.name_ex);
                TextView money=(TextView)convertView.findViewById(R.id.money_ex);
                TextView category=(TextView)convertView.findViewById(R.id.category_ex);
                TextView currency=(TextView)convertView.findViewById(R.id.currency_ex);
                TextView description=(TextView)convertView.findViewById(R.id.description_ex);

                ExpensiveData di=data.get(position);
                name.setText(di.getName());
                money.setText(String.valueOf(di.getValue()));
                category.setText(di.getCategory());
                currency.setText(di.getCurrency());
                description.setText(di.getDescription());
                return convertView;
            }
        };

        lv.setAdapter(a);*/
        //}
        return view;
    }


        /*
        una volta tornati in questa attività dopo l'aggiunta di una spesa
        bisogna refreshare in qualche modo l'adapter
        vedi:
            - notifyDataChanged()
         */

}