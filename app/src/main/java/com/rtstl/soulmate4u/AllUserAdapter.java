package com.rtstl.soulmate4u;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by RTSTL17 on 29-01-2018.
 */

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.MyViewHolder> {

    private Context mContext;
    private int itemLayout;
    private ArrayList<AllUserListModel> allUserList = new ArrayList<>();

    public AllUserAdapter(Context mContext, int itemLayout, ArrayList<AllUserListModel> allUserList) {
        this.mContext = mContext;
        this.itemLayout = itemLayout;
        this.allUserList = allUserList;
    }

    @Override
    public AllUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AllUserAdapter.MyViewHolder holder, final int position) {

        System.out.println("icon url : " + allUserList.get(position).getPicURL());
        Picasso.with(mContext).load(allUserList.get(position).getPicURL()).into(holder.user_image);
        holder.tv_name.setText(allUserList.get(position).getName());
        holder.tv_profession.setText("Profession : " + allUserList.get(position).getProfession());

        int distanceFromMe = allUserList.get(position).getDistanceFromMe();
        double distanceInKm = (distanceFromMe) / 1000;
        if (distanceFromMe > 1000) {
            holder.tv_email.setText("" + distanceInKm + " km");
        } else {
            holder.tv_email.setText("" + distanceFromMe + " m");

        }

        holder.tv_looking_for.setText("Looking for : " + allUserList.get(position).getOppponentProfession());
        holder.tv_interested.setText("Interested In : " + allUserList.get(position).getInterestedIN());

        if (allUserList.get(position).isOnline()) {
            holder.iv_online.setVisibility(View.VISIBLE);
        } else {
            holder.iv_online.setVisibility(View.GONE);
        }


        /*if(allUserList.get(position).getLikeStatus() == 0){
            //neutral
            holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.like_nofill2));
            holder.iv_dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.dislike_nofill2));
        } else if(allUserList.get(position).getLikeStatus() == 1){
            //liked
            holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.like_fill2));
            holder.iv_dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.dislike_nofill2));
        } else if(allUserList.get(position).getLikeStatus() == 2){
            //disliked
            holder.iv_like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.like_nofill2));
            holder.iv_dislike.setImageDrawable(mContext.getResources().getDrawable(R.drawable.dislike_fill2));
        }*/


        if(allUserList.get(position).getIsFriend() > 0){
            holder.iv_chat.setVisibility(View.VISIBLE);
        } else{
            holder.iv_chat.setVisibility(View.GONE);
        }

        holder.iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to chat
                GlobalVariable.currentOpponentChatID = allUserList.get(position).getId();
                mContext.startActivity(new Intent(mContext, ChatActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return allUserList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView user_image;
        private TextView tv_name, tv_profession, tv_interested, tv_email, tv_looking_for;
        ImageView iv_online,iv_like,iv_dislike, iv_chat;

        public MyViewHolder(View view) {
            super(view);
            user_image = (CircleImageView) view.findViewById(R.id.user_image);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_profession = (TextView) view.findViewById(R.id.tv_profession);
            tv_interested = (TextView) view.findViewById(R.id.tv_interested);
            tv_email = (TextView) view.findViewById(R.id.tv_email);
            tv_looking_for = (TextView) view.findViewById(R.id.tv_looking_for);
            iv_online = (ImageView) view.findViewById(R.id.iv_online);
            iv_like = (ImageView) view.findViewById(R.id.iv_like);
            iv_dislike = (ImageView) view.findViewById(R.id.iv_dislike);
            iv_chat = (ImageView) view.findViewById(R.id.iv_chat);


        }
    }
}
