package it.polito.mad.mad_app;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import it.polito.mad.mad_app.model.UserData;


public class AlgorithmParametersAdapter extends RecyclerView.Adapter<AlgorithmParametersAdapter.MyViewHolder> {

    private List<UserData> usersData;
    private int alg;
    private float val;
    private TextView algInfo;
    private TextView algInfoSmall;

    private GruopInfo gi;
    private GruopInfoValue giv;

    private class GruopInfo {
        private int tot;
        private int remaining;
        private TreeMap<Integer, Integer> dataset = new TreeMap<>();

        public GruopInfo() {
            this.tot = 0;
            this.remaining = 100;
        }

        public String toStringTot(){
            return String.valueOf(tot) + "% total";
        }
        public String toStringRemaining(){
            return String.valueOf(remaining) + "% remaining";
        }
        public void addData(int pos, int value) {
            dataset.put(pos, value);
            tot = 0;
            for(float f: dataset.values())
                tot += f;
            remaining = 100 - tot;
        }
    }

    private class GruopInfoValue {
        private float tot;
        private float remaining;
        private float max;
        private String currency;
        private TreeMap<Integer, Float> dataset = new TreeMap<>();

        public GruopInfoValue(float remaining, String currency) {
            this.tot = 0;
            this.remaining = remaining;
            this.max = remaining;
            this.currency = currency;
        }

        public String toStringTot(){
            return String.valueOf(tot) + " " + currency + " total";
        }
        public String toStringRemaining(){
            return String.valueOf(remaining) + " " + currency + " remaining";
        }
        public void addData(int pos, float value) {
            dataset.put(pos, value);
            tot = 0;
            for(float f: dataset.values())
                tot += f;
            remaining = max - tot;
        }
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public EditText value;
        public TextView simbol;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.alg_name);
            value = (EditText) view.findViewById(R.id.alg_value);
            simbol = (TextView) view.findViewById(R.id.alg_simbol);
        }
    }


    public AlgorithmParametersAdapter(List<UserData> usersData, int alg, float val, TextView algInfo, TextView algInfoSmall) {
        this.usersData = usersData;
        this.alg = alg;
        this.val = val;
        this.algInfo = algInfo;
        this.algInfoSmall = algInfoSmall;
        if(alg == 1) {
            gi = new GruopInfo();
            algInfo.setText(gi.toStringTot());
            algInfoSmall.setText(gi.toStringRemaining());
        } else {
            algInfo.setText("");
            algInfoSmall.setText("");
        }
    }

    public AlgorithmParametersAdapter(List<UserData> usersData, int alg, float val, String cur, TextView algInfo, TextView algInfoSmall) {
        this.usersData = usersData;
        this.alg = alg;
        this.val = val;
        this.algInfo = algInfo;
        this.algInfoSmall = algInfoSmall;
        if(alg == 2) {
            giv = new GruopInfoValue(val, "€"); //TODO CURRENCIES
            algInfo.setText(giv.toStringTot());
            algInfoSmall.setText(giv.toStringRemaining());
        } else {
            algInfo.setText("");
            algInfoSmall.setText("");
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.algorithm_parameters_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        UserData user = usersData.get(position);
        holder.name.setText(user.getName()+" "+user.getSurname());
        if(alg == 1) {
            holder.simbol.setText("%");
            holder.value.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() != 0) {
                        gi.addData(position, Integer.parseInt(s.toString()));
                    } else {
                        gi.addData(position, 0);
                    }
                    algInfo.setText(gi.toStringTot());
                    algInfoSmall.setText(gi.toStringRemaining());

                }

            });
        }
            //holder.value.setText(String.format("%.2f", (float)100/(usersData.size())));
        if(alg == 2) {
            holder.value.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() != 0) {
                        giv.addData(position, Float.parseFloat(s.toString()));
                    } else {
                        giv.addData(position, 0);
                    }
                    algInfo.setText(giv.toStringTot());
                    algInfoSmall.setText(giv.toStringRemaining());

                }

            });
        }
            //holder.value.setText(String.format("%.2f", (float)val/(usersData.size())));
    }

    @Override
    public int getItemCount() {
        return usersData.size();
    }
}