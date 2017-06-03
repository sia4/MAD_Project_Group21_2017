package it.polito.mad.mad_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import it.polito.mad.mad_app.model.User;

/**
 * Created by Lucia on 07/05/2017.
 */

public class UserAdapterIm  extends RecyclerView.Adapter<UserAdapterIm.MyViewHolder> {

    private List<User> usersData;
    private View context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView im;

        public MyViewHolder(View view) {
            super(view);
            context=view;
            name = (TextView) view.findViewById(R.id.name_u_lis);
            im=(ImageView) view.findViewById(R.id.list_im_us);
        }
    }

    public UserAdapterIm(List<User> usersData) {
        this.usersData = usersData;
    }

    @Override
    public UserAdapterIm.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_im, parent, false);
        return new UserAdapterIm.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final  UserAdapterIm.MyViewHolder holder, int position) {

        String s = null;
        if(!usersData.get(position).getName().equals(""))
            s = usersData.get(position).getName()+ " "+usersData.get(position).getSurname();
        else
            s = usersData.get(position).getEmail();

        holder.name.setText(s);
        String p=usersData.get(position).getImagePath();

        if(p==null)
            holder.im.setImageResource(R.drawable.user_icon_default);
        else {
            final Context context = holder.name.getContext();
            Glide.with(context).load(p).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.im) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);

                    holder.im.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return usersData.size();
    }
}
