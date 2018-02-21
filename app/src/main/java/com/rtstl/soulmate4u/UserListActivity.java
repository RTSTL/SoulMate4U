package com.rtstl.soulmate4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidadvance.topsnackbar.TSnackbar;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {

    RecyclerView rv_user_list;
    Context context;
    Preferences pref;
    ProgressDialog dialog;
    ArrayList<AllUserListModel> allUserList = new ArrayList<>();
    MaterialSpinner spn_online, spn_visible_invisible;
    String onlineSort = "all", visibleInvisibleSort = "all";
    ArrayList<AllUserListModel> tempOnlineSortList = new ArrayList<>();
    ArrayList<AllUserListModel> tempVisibleInvisibleSortList = new ArrayList<>();
    ArrayList<AllUserListModel> tempList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_user_list);

        initView();

    }

    private void initView() {

        context = this;

        pref = new Preferences(context);

        dialog = new ProgressDialog(context);
        dialog.setMessage("Fetching users...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        rv_user_list = (RecyclerView) findViewById(R.id.rv_user_list);
        spn_online = (MaterialSpinner) findViewById(R.id.spn_online);
        spn_visible_invisible = (MaterialSpinner) findViewById(R.id.spn_near_far);

        spn_online.setItems("all", "online", "offline");
        spn_online.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item.toLowerCase(), Snackbar.LENGTH_LONG).show();
                onlineSort = item.toLowerCase();
                if (onlineSort.equalsIgnoreCase("offline")) {
                    spn_visible_invisible.setSelectedIndex(0);
                    spn_visible_invisible.setEnabled(false);
                } else if (onlineSort.equalsIgnoreCase("online")) {
                    spn_visible_invisible.setEnabled(true);
                } else if (onlineSort.equalsIgnoreCase("all")) {
                    spn_visible_invisible.setSelectedIndex(0);
                    spn_visible_invisible.setEnabled(true);
                }
                doOnlineOfflineSort(onlineSort);
            }
        });

        spn_visible_invisible.setItems("all", "visible", "invisible");
        spn_visible_invisible.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item.toLowerCase(), Snackbar.LENGTH_LONG).show();
                visibleInvisibleSort = item.toLowerCase();
                doVisibleInvisibleSort(visibleInvisibleSort);
            }
        });


        getAllUserList();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void doOnlineOfflineSort(String onlineSort) {

        tempOnlineSortList.clear();

        if (onlineSort.equalsIgnoreCase("online")) {

            for (int i = 0; i < allUserList.size(); i++) {
                if (allUserList.get(i).isOnline() == true) {
                    tempOnlineSortList.add(allUserList.get(i));
                }
            }
            //set adapter
            rv_user_list.removeAllViews();
            AllUserAdapter au = new AllUserAdapter(context, R.layout.user_list_item, tempOnlineSortList);
            LinearLayoutManager lm
                    = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rv_user_list.setLayoutManager(lm);
            rv_user_list.setAdapter(au);

        } else if (onlineSort.equalsIgnoreCase("offline")) {

            for (int i = 0; i < allUserList.size(); i++) {
                if (allUserList.get(i).isOnline() == false) {
                    tempOnlineSortList.add(allUserList.get(i));
                }
            }

            //set adapter
            rv_user_list.removeAllViews();
            AllUserAdapter au = new AllUserAdapter(context, R.layout.user_list_item, tempOnlineSortList);
            LinearLayoutManager lm
                    = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rv_user_list.setLayoutManager(lm);
            rv_user_list.setAdapter(au);
        } else {
            //set adapter
            rv_user_list.removeAllViews();
            AllUserAdapter au = new AllUserAdapter(context, R.layout.user_list_item, allUserList);
            LinearLayoutManager lm
                    = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rv_user_list.setLayoutManager(lm);
            rv_user_list.setAdapter(au);
        }


    }

    private void doVisibleInvisibleSort(String visibleInvisibleSort) {

        tempVisibleInvisibleSortList.clear();

        if (tempOnlineSortList.size() == 0) {
            tempOnlineSortList.addAll(allUserList);
        }

        if (visibleInvisibleSort.equalsIgnoreCase("visible")) {

            for (int i = 0; i < tempOnlineSortList.size(); i++) {
                if (tempOnlineSortList.get(i).isVisible() == true) {
                    tempVisibleInvisibleSortList.add(allUserList.get(i));
                }
            }
            //set adapter
            rv_user_list.removeAllViews();
            AllUserAdapter au = new AllUserAdapter(context, R.layout.user_list_item, tempVisibleInvisibleSortList);
            LinearLayoutManager lm
                    = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rv_user_list.setLayoutManager(lm);
            rv_user_list.setAdapter(au);

        } else if (visibleInvisibleSort.equalsIgnoreCase("invisible")) {

            for (int i = 0; i < tempOnlineSortList.size(); i++) {
                if (tempOnlineSortList.get(i).isVisible() == false) {
                    tempVisibleInvisibleSortList.add(allUserList.get(i));
                }
            }

            //set adapter
            rv_user_list.removeAllViews();
            AllUserAdapter au = new AllUserAdapter(context, R.layout.user_list_item, tempVisibleInvisibleSortList);
            LinearLayoutManager lm
                    = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rv_user_list.setLayoutManager(lm);
            rv_user_list.setAdapter(au);
        } else {
            //set adapter
            rv_user_list.removeAllViews();
            AllUserAdapter au = new AllUserAdapter(context, R.layout.user_list_item, tempOnlineSortList);
            LinearLayoutManager lm
                    = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rv_user_list.setLayoutManager(lm);
            rv_user_list.setAdapter(au);
        }


    }

    private void getAllUserList() {

        dialog.show();

        if (CheckNetwork.isInternetAvailable(context)) {

            System.out.println("user list called");

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.getAllUserList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("visibility response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));

                                    JSONArray list = jsonObject.optJSONArray("list");
                                    for (int i = 0; i < list.length(); i++) {
                                        JSONObject listObj = list.optJSONObject(i);

                                        int distanceFromMe = distanceBetween(Double.valueOf(GlobalVariable.currentLatitude),
                                                Double.valueOf(GlobalVariable.currentLongitude),
                                                Double.valueOf(listObj.optString("my_lat")),
                                                Double.valueOf(listObj.optString("my_long")));

                                        if (!pref.getStringPreference(context, "user_id")
                                                .equalsIgnoreCase(listObj.optString("rowid"))) {
                                            allUserList.add(new AllUserListModel(
                                                    listObj.optString("rowid"),
                                                    listObj.optString("fbid"),
                                                    listObj.optString("name"),
                                                    listObj.optString("email"),
                                                    listObj.optString("pictureurl"),
                                                    listObj.optString("gender"),
                                                    listObj.optString("phone"),
                                                    listObj.optString("interested_in"),
                                                    listObj.optString("opponent_profession"),
                                                    listObj.optString("my_lat"),
                                                    listObj.optString("my_long"),
                                                    listObj.optBoolean("status"),
                                                    listObj.optBoolean("isvisible"),
                                                    listObj.optInt("islike"),
                                                    listObj.optString("profession"),
                                                    listObj.optString("dob"),
                                                    listObj.optString("lasttime"),
                                                    distanceFromMe,
                                                    listObj.optInt("isfriend")

                                            ));
                                        }


                                    }

                                    for (int k = 0; k < allUserList.size(); k++) {
                                        Collections.sort(allUserList, new Comparator<AllUserListModel>() {
                                            public int compare(AllUserListModel obj1, AllUserListModel obj2) {
                                                // ## Ascending order
                                                return Integer.valueOf(obj1.distanceFromMe).compareTo(obj2.distanceFromMe); // To compare string values
                                                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                                                // ## Descending order
                                                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                                            }
                                        });
                                    }

                                    AllUserAdapter au = new AllUserAdapter(context, R.layout.user_list_item, allUserList);
                                    LinearLayoutManager lm
                                            = new LinearLayoutManager(context,
                                            LinearLayoutManager.VERTICAL, false);
                                    rv_user_list.setLayoutManager(lm);
                                    rv_user_list.setAdapter(au);

                                    dialog.dismiss();

                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                    params.put("rowid", pref.getStringPreference(UserListActivity.this, "user_id"));

                    System.out.println("params sending : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }

    private void showSnackbar(String text) {

        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content),
                text, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#FF6B5B60"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static int distanceBetween(Double point1, Double point2, Double point3, Double point4) {

        //Returns in meter

        LatLng ltln1 = new LatLng(point1, point2);
        LatLng ltln2 = new LatLng(point3, point4);

        System.out.println("ltlng1 : " + ltln1);
        System.out.println("ltlng2 : " + ltln2);

        if (ltln1 == null || ltln2 == null) {
            return 0;
        }

        System.out.println("new distance : " + SphericalUtil.computeDistanceBetween(ltln1, ltln2));
        return (int) SphericalUtil.computeDistanceBetween(ltln1, ltln2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //GlobalVariable.isListIconClickedOnMap = true;
        startService(new Intent(this, BackgroundGpsService.class)); //start service
    }

    @Override
    protected void onPause() {
        super.onPause();
        //GlobalVariable.isListIconClickedOnMap = true;
        stopService(new Intent(this, BackgroundGpsService.class)); //stop service
    }
}
