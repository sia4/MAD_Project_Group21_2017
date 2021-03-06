package it.polito.mad.mad_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import it.polito.mad.mad_app.model.UserData;


public class UsersToAddAdapter extends RecyclerView.Adapter<UsersToAddAdapter.MyViewHolder> {

    private List<String> usersData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
        }
    }


    public UsersToAddAdapter(List<String> usersData) {
        this.usersData = usersData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usertoadd_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String user = usersData.get(position);
        holder.name.setText(user);
    }

    @Override
    public int getItemCount() {
        return usersData.size();
    }
}