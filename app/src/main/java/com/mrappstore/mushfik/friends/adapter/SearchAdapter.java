package com.mrappstore.mushfik.friends.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrappstore.mushfik.friends.R;
import com.mrappstore.mushfik.friends.activity.ProfileActivity;
import com.mrappstore.mushfik.friends.model.User;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    Context context;
    List<User> users;

    public SearchAdapter(Context context,List<User>users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_search_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*
        Get a single user from the list of users.
         */
        final User user = users.get(position);
        holder.userName.setText(user.getName());

        if (!user.getProfileUrl().isEmpty()){
            Picasso.with(context).load(user.getProfileUrl()).placeholder(R.drawable.default_image_placeholder)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.userImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(user.getProfileUrl())
                            .placeholder(R.drawable.default_image_placeholder)
                            .into(holder.userImage);
                }
            });

        }

        /*
        make the search list item clickable
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                hide keyboard
                 */
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("uid",user.getUid()));
            }
        });

    }

    @Override
    public int getItemCount() {
       return users.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_image)
        ImageView userImage;
        @BindView(R.id.user_name)
        TextView userName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
