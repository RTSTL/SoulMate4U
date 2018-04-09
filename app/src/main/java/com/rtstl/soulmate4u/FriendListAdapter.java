package com.rtstl.soulmate4u;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by RTSTL17 on 29-01-2018.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> {

    private Context mContext;
    private int itemLayout;
    private ArrayList<AllUserListModel> friendList = new ArrayList<>();
    int friendListType = 0;
    Preferences pref;

    public FriendListAdapter(Context mContext, int itemLayout, ArrayList<AllUserListModel> friendList, int friendListType) {
        this.mContext = mContext;
        this.itemLayout = itemLayout;
        this.friendList = friendList;
        this.friendListType = friendListType;
        pref = new Preferences(mContext);
    }

    @Override
    public FriendListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendListAdapter.MyViewHolder holder, final int position) {

        System.out.println("icon url : " + friendList.get(position).getPicURL());
        Picasso.with(mContext).load(friendList.get(position).getPicURL()).into(holder.user_image);
        holder.tv_name.setText(friendList.get(position).getName());
        holder.tv_profession.setText("Profession : " + friendList.get(position).getProfession());

        System.out.println("mood id = " + friendList.get(position).getMoodID() +
                " of " + friendList.get(position).getName() +
                " -- mood URL : " + friendList.get(position).getMoodURL());

        if (friendList.get(position).getMoodURL().length() > 0)
            Picasso.with(mContext).load(friendList.get(position).getMoodURL()).into(holder.iv_mood);

        /*if (friendList.get(position).getDestLatLng().length() > 0) {
            //User is travelling
           *//* Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            holder.iv_mood.startAnimation(animation);*//*
            Picasso.with(mContext).load(friendList.get(position).getMoodURL()).into(holder.iv_mood);
        } else {
            //user is not travelling
            if (friendList.get(position).getMoodID() != 0) {
                System.out.println("mood URL in else : " + friendList.get(position).getMoodURL());
                Picasso.with(mContext).load(friendList.get(position).getMoodURL()).into(holder.iv_mood);
            }
        }*/

        System.out.println("friendList.get(position).isVisible() : " + friendList.get(position).isVisible());

        if (friendListType == 2) {
            holder.btn_accpt.setVisibility(View.VISIBLE);
            holder.btn_reject.setVisibility(View.VISIBLE);
            holder.iv_online.setVisibility(View.GONE);
            holder.iv_mood.setVisibility(View.GONE);
        } else {
            holder.btn_accpt.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE);
            holder.iv_online.setVisibility(View.VISIBLE);
            holder.iv_mood.setVisibility(View.VISIBLE);
        }

        if (friendList.get(position).isVisible()) {
            System.out.println("online : " + friendList.get(position).isVisible());
            holder.iv_online.setVisibility(View.VISIBLE);
        } else {
            System.out.println("offline : " + friendList.get(position).isVisible());
            holder.iv_online.setVisibility(View.GONE);
        }


        holder.tv_looking_for.setText("Looking for : " + friendList.get(position).getOppponentProfession());



        holder.btn_accpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog("Are you sure you want to accept this profile?",
                        Webservice.approveFriendReq, 2, position);
            }
        });

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Are you sure you want to reject this profile?",
                        Webservice.rejectFriendReq, 3, position);
            }
        });


    }

    private void accptRejectRequest(String urlToFire, final int acceptOrReject, final int position) {
        if (CheckNetwork.isInternetAvailable(mContext)) {

            final ProgressDialog dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            System.out.println("approve reject url : " + urlToFire);
            System.out.println("acceptOrReject : " + acceptOrReject);

            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest postRequest = new StringRequest(Request.Method.POST, urlToFire,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("approve reject response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));

                                    if (jsonObject.optInt("status") == 1) {
                                        //remove the element
                                        friendList.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    //Like Dislike


//                                    userLists.get(position).setIsLiked(likeStatus); // set the user like status
                                    dialog.dismiss();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    dialog.dismiss();
                                }
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    if (acceptOrReject == 2) {
                        params.put("likerowid", friendList.get(position).getId());
                        params.put("islike", String.valueOf(acceptOrReject));
                        params.put("rowid", pref.getStringPreference(mContext, "user_id"));
                    } else if (acceptOrReject == 3) {
                        params.put("likerowid", friendList.get(position).getId());
                        params.put("islike", String.valueOf(acceptOrReject));
                        params.put("rowid", pref.getStringPreference(mContext, "user_id"));
                    }

                    System.out.println("params sending : " + params);


                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            Toast.makeText(mContext, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView user_image;
        private TextView tv_name, tv_profession, tv_looking_for;
        private Button btn_accpt, btn_reject;
        private ImageView iv_online, iv_mood;

        public MyViewHolder(View view) {
            super(view);
            user_image = (CircleImageView) view.findViewById(R.id.user_image);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_profession = (TextView) view.findViewById(R.id.tv_profession);
            tv_looking_for = (TextView) view.findViewById(R.id.tv_looking_for);
            btn_reject = (Button) view.findViewById(R.id.btn_reject);
            btn_accpt = (Button) view.findViewById(R.id.btn_accpt);
            iv_online = (ImageView) view.findViewById(R.id.iv_online);
            iv_mood = (ImageView) view.findViewById(R.id.iv_mood);

        }
    }

    public void showDialog(String text, final String urlToFire, final int acceptOrReject, final int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle("Alert")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with yes
                        accptRejectRequest(urlToFire, acceptOrReject, position);


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
