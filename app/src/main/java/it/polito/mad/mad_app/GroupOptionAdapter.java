package it.polito.mad.mad_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GroupOptionAdapter extends RecyclerView.Adapter<GroupOptionAdapter.MyViewHolder> {

    private List<String> stringList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView option;

        public MyViewHolder(View view) {
            super(view);
            option = (TextView) view.findViewById(R.id.option);
        }
    }


    public GroupOptionAdapter(List<String> list) {
        this.stringList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.option_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String string = stringList.get(position);
        holder.option.setText(string);
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}