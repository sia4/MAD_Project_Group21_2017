package it.polito.mad.mad_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import it.polito.mad.mad_app.model.UserData;


public class AlgorithmParametersAdapter extends RecyclerView.Adapter<AlgorithmParametersAdapter.MyViewHolder> {

    private List<UserData> usersData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public EditText value;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.alg_name);
            value = (EditText) view.findViewById(R.id.alg_value);
        }
    }


    public AlgorithmParametersAdapter(List<UserData> usersData) {
        this.usersData = usersData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.algorithm_parameters_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserData user = usersData.get(position);
        holder.name.setText(user.getName()+" "+user.getSurname());
        holder.value.setText(String.format("%.2f", (float)100/(usersData.size())));
    }

    @Override
    public int getItemCount() {
        return usersData.size();
    }
}