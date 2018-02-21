package com.rtstl.soulmate4u;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by RTSTL17 on 29-01-2018.
 */

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.MyViewHolder> {

    private Context mContext;
    private int itemLayout;
    private ArrayList<RestaurantList> nearbyList = new ArrayList<>();

    public NearbyAdapter(Context mContext, int itemLayout, ArrayList<RestaurantList> nearbyList) {
        this.mContext = mContext;
        this.itemLayout = itemLayout;
        this.nearbyList = nearbyList;
    }

    @Override
    public NearbyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NearbyAdapter.MyViewHolder holder, final int position) {

        System.out.println("icon url : " + nearbyList.get(position).getIcon());
        Picasso.with(mContext).load(nearbyList.get(position).getIcon()).into(holder.iv_image);
        holder.tv_name.setText(nearbyList.get(position).getName());
        holder.tv_address.setText(nearbyList.get(position).getAddress());
        if (nearbyList.get(position).isOpened()) {
            holder.tv_open.setText("Open Now");
        } else {
            holder.tv_open.setText("Closed Now");
        }
        if (nearbyList.get(position).getRating() > 0.0) {
            holder.rating_star.setRating((float) (nearbyList.get(position).getRating()));
        }
        holder.rating_star.setEnabled(false);
        holder.rating_star.setClickable(false);

        holder.rl_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("lat lng : " + GlobalVariable.currentLatitude);
                String uri = "http://maps.google.com/maps?saddr=" + GlobalVariable.currentLatitude + ","
                        + GlobalVariable.currentLongitude + "&daddr=" + nearbyList.get(position).getLatitude() + "," +
                        nearbyList.get(position).getLongitude();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                mContext.startActivity(intent);
            }
        });

        LatLng ltln1 = new LatLng(Double.valueOf(GlobalVariable.currentLatitude), Double.valueOf(GlobalVariable.currentLongitude));
        LatLng ltln2 = new LatLng(Double.valueOf(nearbyList.get(position).getLatitude()),
                Double.valueOf(nearbyList.get(position).getLongitude()));
       int distanceFromMe = distanceBetween(ltln1, ltln2);
       double distanceInKm = (distanceFromMe)/1000;

       if(distanceFromMe > 1000){
           holder.tv_distance.setText("" + distanceInKm+" km");
       } else{
           holder.tv_distance.setText("" + distanceFromMe+" m");

       }


    }

    @Override
    public int getItemCount() {
        return nearbyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView iv_image;
        private TextView tv_name, tv_address, tv_open, tv_distance;
        private SimpleRatingBar rating_star;
        private RelativeLayout rl_wrapper;

        public MyViewHolder(View view) {
            super(view);
            iv_image = (CircleImageView) view.findViewById(R.id.nearby_image);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_address = (TextView) view.findViewById(R.id.tv_address);
            tv_distance = (TextView) view.findViewById(R.id.tv_distance);
            tv_open = (TextView) view.findViewById(R.id.tv_open);
            rl_wrapper = (RelativeLayout) view.findViewById(R.id.rl_wrapper);
            rating_star = (SimpleRatingBar) view.findViewById(R.id.rating_star);
        }
    }

    public static int distanceBetween(LatLng point1, LatLng point2) {

        //Returns in meter

        if (point1 == null || point2 == null) {
            return 0;
        }

        System.out.println("new distance : " + SphericalUtil.computeDistanceBetween(point1, point2));
        return (int) SphericalUtil.computeDistanceBetween(point1, point2);
    }

}
