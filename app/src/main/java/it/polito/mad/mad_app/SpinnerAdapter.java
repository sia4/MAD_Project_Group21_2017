package it.polito.mad.mad_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import it.polito.mad.mad_app.R;
import it.polito.mad.mad_app.model.ItemData;

public class SpinnerAdapter extends ArrayAdapter<ItemData> {
    int groupid;
    Activity context;
    ArrayList<ItemData> list;
    LayoutInflater inflater;
    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<ItemData>
            list){
        super(context,id,list);
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent ){
        View itemView=inflater.inflate(R.layout.category_item, parent,false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.category_icon);
        TextView textView=(TextView)itemView.findViewById(R.id.category_name);
        if(!list.get(position).getText().equals("Select category"))
            imageView.setImageResource(list.get(position).getImageId());
        else{
            imageView.setVisibility(View.GONE);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins(15, 25, 0, 20); // llp.setMargins(left, top, right, bottom);
            textView.setLayoutParams(llp);
        }

        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }
}