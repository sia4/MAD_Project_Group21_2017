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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import it.polito.mad.mad_app.model.ExpenseData;
import it.polito.mad.mad_app.model.MainData;
import it.polito.mad.mad_app.model.RecyclerTouchListener;

public class    HistoryFragment extends Fragment {

    private static final int VERTICAL_ITEM_SPACE = 48;

    static private List<ExpenseData> lex = new ArrayList<>();
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
         //       DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new it.polito.mad.mad_app.DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                final ExpenseData expense = lex.get(position);
                Intent intent = new Intent().setClass(view.getContext(), ExpenseInfoActivity.class);
                intent.putExtra("name", expense.getName());
                intent.putExtra("category", expense.getCategory());
                intent.putExtra("currency", expense.getCurrency());
                intent.putExtra("algorithm", expense.getAlgorithm());
                intent.putExtra("description", expense.getDescription());
                //String.format("%.2f", expense.getMyvalue())
                intent.putExtra("myvalue",String.format("%.2f", expense.getMyvalue()) );
                intent.putExtra("value", String.format("%.2f", expense.getValue()));
                intent.putExtra("creator", MainData.getInstance().getMyName());//TODO change with getCreator
                intent.putExtra("date", expense.getDate());
                String GroupName = getArguments().getString("GroupName");
                intent.putExtra("GroupName", GroupName);
                view.getContext().startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        String GroupName = this.getArguments().getString("GroupId");
        System.out.println("H: " + GroupName);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Expenses").child(GroupName);


        final HistoryAdapter hAdapter = new HistoryAdapter(lex);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hAdapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                /*
                if(map!=null) {
                    for (String k : map.keySet())
                        lex.add((ExpenseData) map.get(k));

                    hAdapter.notifyDataSetChanged();
                }
                */
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return view;
    }


}