package com.rtstl.soulmate4u;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SimpleBottomAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean isGrid;
    private ArrayList<MoodModel> statusList = new ArrayList<>();
    private String myMoodID = "0";


    public SimpleBottomAdapter(Context context, boolean isGrid, ArrayList<MoodModel> statusList, String myMoodID) {
        layoutInflater = LayoutInflater.from(context);
        this.isGrid = isGrid;
        this.statusList = statusList;
        this.myMoodID = myMoodID;
    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.simple_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            viewHolder.ll_wrap = (LinearLayout) view.findViewById(R.id.ll_wrap);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        viewHolder.textView.setText(statusList.get(position).getMoodName());
//        viewHolder.imageView.setImageResource(R.drawable.ic_menu_camera);
        Picasso.with(context).load(statusList.get(position).getMoodUrl())
                .resize(120, 120).into(viewHolder.imageView);

        System.out.println("// statusList.get(position).getId() : " + statusList.get(position).getId() + " ==== " + myMoodID);

        if(statusList.get(position).getId().equalsIgnoreCase(myMoodID)){
            viewHolder.ll_wrap.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
        } else{
            viewHolder.ll_wrap.setBackgroundColor(context.getResources().getColor(R.color.white));
        }


        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
        LinearLayout ll_wrap;
    }
}
