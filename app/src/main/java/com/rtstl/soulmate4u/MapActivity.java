package com.rtstl.soulmate4u;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidadvance.topsnackbar.TSnackbar;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by RTSTL17 on 26-12-2017.
 */

public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    GPSTracker gps;
    Preferences pref;
    ArrayList<UserModel> userList = new ArrayList<>();
    ArrayList<UserModel> userLists = new ArrayList<>();

    Handler h = new Handler();
    int delay = 10000; //10 seconds
    Runnable runnable;

    private static final String TAG = "MapActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    private double myCurrentLat = 0.0;
    private double myCurrentLng = 0.0;
    ArrayList<RestaurantList> restaurantList = new ArrayList<>();
    Marker myPositionMarker;
    MarkerOptions myPositionMarkerOptions;
    boolean isMyMarkerAdded = false;
    ArrayList<OpponentMarkerModel> opponentMarkerList = new ArrayList<>();
    MarkerOptions opponetMarkerOptions, restaurantMarkerOptions;
    ArrayList<Marker> restaurantMarkers = new ArrayList<>();
    Button btn_hide, btn_restaurant;
    ProgressDialog dialog;
    Marker tempMarker;
    protected PowerManager.WakeLock mWakeLock;

    RelativeLayout rl_finder;
    ImageView iv_finder, iv_visible, left_icon;
    private Animation animShow, animHide;
    LinearLayout ll_rv_wrapper;
    RecyclerView rv_nearby;
    String searchType = "restaurant", searchDistance = "100";
    MaterialSpinner spinner, spn_distance;
    String visibilityStatus = "0";
    boolean doubleBackToExitPressedOnce = false;
    //boolean isListIconClicked = false;
    ProgressBar nearby_loader;
    ImageView iv_flist, close_flist, iv_list, iv_status;
    RelativeLayout rl_flist;
    LinearLayout ll_flist_wrapper;
    ProgressBar flist_loader;
    RecyclerView rv_flist;
    ArrayList<AllUserListModel> friendList = new ArrayList<>();
    //ArrayList<String> statusList = new ArrayList<>();
    ArrayList<MoodModel> moodList = new ArrayList<>();
    CircleImageView nav_profile_image;
    TextView tv_nav_name, tv_nav_email;
    RequestQueue getUserQueue;
    StringRequest getUserStringRequest;
    Polyline polylineMyRoute, polylineOtherRoute;
    String routeForMe = "";

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        gps = new GPSTracker(MapActivity.this);
        pref = new Preferences(MapActivity.this);

        setContentView(R.layout.activity_main_temp);

        ////// drawer starts ///////

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().hide();

        findViewById(R.id.iv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        View header = navView.getHeaderView(0);

        tv_nav_name = header.findViewById(R.id.tv_nav_name);
        tv_nav_email = header.findViewById(R.id.tv_nav_email);
        nav_profile_image = header.findViewById(R.id.nav_profile_image);

        tv_nav_name.setText(pref.getStringPreference(MapActivity.this, "fb_name"));
        tv_nav_email.setText(pref.getStringPreference(MapActivity.this, "fb_email"));
        Picasso.with(MapActivity.this).load(pref.getStringPreference(MapActivity.this, "fb_pic"))
                .into(nav_profile_image);
        ////// drawer ends ///////

        dialog = new ProgressDialog(MapActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage("Fetching your current location..");
        dialog.show();

        rl_finder = (RelativeLayout) findViewById(R.id.rl_finder);
        iv_finder = (ImageView) findViewById(R.id.iv_finder);
        left_icon = (ImageView) findViewById(R.id.left_icon);
        iv_visible = (ImageView) findViewById(R.id.iv_visible);
        iv_status = findViewById(R.id.iv_status);

        iv_flist = findViewById(R.id.iv_flist);
        rl_flist = findViewById(R.id.rl_flist);
        close_flist = findViewById(R.id.close_flist);
        ll_flist_wrapper = findViewById(R.id.ll_flist_wrapper);
        rv_flist = findViewById(R.id.rv_flist);
        flist_loader = findViewById(R.id.flist_loader);
        iv_list = findViewById(R.id.iv_list);

        ll_rv_wrapper = (LinearLayout) findViewById(R.id.ll_rv_wrapper);
        rv_nearby = (RecyclerView) findViewById(R.id.rv_nearby);
        nearby_loader = (ProgressBar) findViewById(R.id.nearby_loader);

        animShow = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
        animHide = AnimationUtils.loadAnimation(this, R.anim.left_to_right);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        btn_restaurant = (Button) findViewById(R.id.btn_restaurant);
        btn_hide = (Button) findViewById(R.id.btn_hide);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getMoodList();

        if (pref.getStringPreference(MapActivity.this, "fb_gender").equalsIgnoreCase("male")) {
            //male
            iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.invisible_male));
            iv_finder.setImageDrawable(getResources().getDrawable(R.drawable.telescope_male));
            left_icon.setImageDrawable(getResources().getDrawable(R.drawable.telescope_male));
            iv_flist.setImageDrawable(getResources().getDrawable(R.drawable.friendlist_male));
            iv_list.setImageDrawable(getResources().getDrawable(R.drawable.listing_male));
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_male));
        } else {
            //female
            iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.invisible_female));
            iv_finder.setImageDrawable(getResources().getDrawable(R.drawable.telescope_female));
            left_icon.setImageDrawable(getResources().getDrawable(R.drawable.telescope_female));
            iv_flist.setImageDrawable(getResources().getDrawable(R.drawable.friendlist_female));
            iv_list.setImageDrawable(getResources().getDrawable(R.drawable.listing_female));
            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.status_female));
        }


        // Check if GPS enabled
        if (gps.canGetLocation()) {

            GlobalVariable.currentLatitude = gps.getLatitude();
            GlobalVariable.currentLongitude = gps.getLongitude();
            System.out.println("gps.getLatitude() : " + gps.getLatitude());
            System.out.println("gps.getLongitude() : " + gps.getLongitude());

            //sendLocationToServer(gps.getLatitude(), gps.getLongitude());
            //startService(new Intent(this, BackgroundGpsService.class)); //start service


        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }

        btn_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show restaurant
                //h.removeCallbacks(runnable);
                showRestaurantsInMap(String.valueOf(GlobalVariable.currentLatitude),
                        String.valueOf(GlobalVariable.currentLongitude));
                btn_restaurant.setVisibility(View.GONE);
                btn_hide.setVisibility(View.VISIBLE);

            }
        });

        btn_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show restaurant
                if (mMap != null) {
                    for (int i = 0; i < restaurantList.size(); i++) {
                        restaurantMarkers.get(i).remove();
                    }
                    restaurantList.clear();
                    restaurantMarkers.clear();
                }

                btn_hide.setVisibility(View.GONE);
                btn_restaurant.setVisibility(View.VISIBLE);

            }
        });

        //initially
        iv_finder.setVisibility(View.VISIBLE);
        rl_finder.setVisibility(View.GONE);
        ll_rv_wrapper.setVisibility(View.GONE);

        iv_finder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rl_finder.startAnimation(animHide);

                iv_finder.setVisibility(View.GONE);
                rl_finder.setVisibility(View.VISIBLE);


                spinner = (MaterialSpinner) findViewById(R.id.spn_nearby);
//                spinner.setItems("Restaurant", "ATM", "Bar", "Hospital", "Salon");
                spinner.setItems("Food", "Fun", "ATM", "Drink", "Hotel", "Salon", "Transport", "Books", "Worship");

                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        //Snackbar.make(view, "Clicked " + item.toLowerCase(), Snackbar.LENGTH_LONG).show();
                        /*if (item.toLowerCase().equalsIgnoreCase("Salon")) {
                            searchType = "beauty_salon";
                        } else {
                            searchType = item.toLowerCase();
                        }*/

                        if (item.toLowerCase().equalsIgnoreCase("food")) {
                            searchType = "restaurant||cafe";
                        } else if (item.toLowerCase().equalsIgnoreCase("fun")) {
                            searchType = "zoo||amusement_park||park||museum";
                        } else if (item.toLowerCase().equalsIgnoreCase("atm")) {
                            searchType = "atm";
                        } else if (item.toLowerCase().equalsIgnoreCase("drink")) {
                            searchType = "bar||night_club||liquor_store";
                        } else if (item.toLowerCase().equalsIgnoreCase("hotel")) {
                            searchType = "lodging||hotel";
                        } else if (item.toLowerCase().equalsIgnoreCase("salon")) {
                            searchType = "beauty_salon||spa||hair_care";
                        } else if (item.toLowerCase().equalsIgnoreCase("transport")) {
                            searchType = "bus_stand||taxi_stand";
                        } else if (item.toLowerCase().equalsIgnoreCase("books")) {
                            searchType = "library||book_store";
                        } else if (item.toLowerCase().equalsIgnoreCase("worship")) {
                            searchType = "hindu_temple||church";
                        }

                        showRestaurantsInMap(String.valueOf(GlobalVariable.currentLatitude),
                                String.valueOf(GlobalVariable.currentLongitude));
                    }
                });

                spn_distance = (MaterialSpinner) findViewById(R.id.spn_distance);
                spn_distance.setItems("100m", "200m", "300m", "500m", "1km", "1.5km", "2km", "5km", "10km");
                spn_distance.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {


                        switch (item) {
                            case "100m":
                                searchDistance = item.split("m")[0];
                                break;
                            case "200m":
                                searchDistance = item.split("m")[0];
                                break;
                            case "300m":
                                searchDistance = item.split("m")[0];
                                break;
                            case "500m":
                                searchDistance = item.split("m")[0];
                                break;
                            case "1km":
                                searchDistance = "1000";
                                break;
                            case "1.5km":
                                searchDistance = "1000";
                                break;
                            case "2km":
                                searchDistance = "2000";
                                break;
                            case "5km":
                                searchDistance = "5000";
                                break;
                            case "10km":
                                searchDistance = "10000";
                                break;

                            default:
                                searchDistance = "100";
                        }


                        //searchDistance = item.split("m")[0];
                        //Snackbar.make(view, "Clicked " + searchDistance, Snackbar.LENGTH_LONG).show();
                        showRestaurantsInMap(String.valueOf(GlobalVariable.currentLatitude),
                                String.valueOf(GlobalVariable.currentLongitude));
                    }
                });

                showRestaurantsInMap(String.valueOf(GlobalVariable.currentLatitude),
                        String.valueOf(GlobalVariable.currentLongitude));

            }
        });

        findViewById(R.id.left_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_finder.setVisibility(View.VISIBLE);
                rl_finder.setVisibility(View.GONE);
                rl_finder.startAnimation(animShow);
                restaurantList.clear();
                rv_nearby.removeAllViews();
                ll_rv_wrapper.setVisibility(View.GONE);
                spinner.setSelectedIndex(0);
                spn_distance.setSelectedIndex(0);
                searchType = "restaurant||cafe";//default
                searchDistance = "100";
            }
        });

        findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRestaurantsInMap(String.valueOf(GlobalVariable.currentLatitude),
                        String.valueOf(GlobalVariable.currentLongitude));
            }
        });

        findViewById(R.id.iv_visible).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (visibilityStatus.equalsIgnoreCase("1")) {
                    //make invisible
                    showTextDialogWithYesNo(MapActivity.this,
                            "Are you sure you want to make yourself invisible to other users?",
                            pref.getStringPreference(MapActivity.this, "fb_gender"),
                            "0");
                } else if (visibilityStatus.equalsIgnoreCase("0")) {
                    //make visible
                    showTextDialogWithYesNo(MapActivity.this,
                            "Are you sure you want to make yourself visible to other users?",
                            pref.getStringPreference(MapActivity.this, "fb_gender"),
                            "1");
                }


            }
        });

        findViewById(R.id.iv_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to list activity
                GlobalVariable.isListIconClickedOnMap = true;
                Intent intent = new Intent(MapActivity.this, UserListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //initially
        iv_flist.setVisibility(View.VISIBLE);
        rl_flist.setVisibility(View.GONE);
        ll_flist_wrapper.setVisibility(View.GONE);


        findViewById(R.id.iv_flist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rl_flist.startAnimation(animHide);
                iv_flist.setVisibility(View.GONE);
                rl_flist.setVisibility(View.VISIBLE);

                loadFriendList(Webservice.getFriendList, 1);

            }
        });

        close_flist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_flist.setVisibility(View.VISIBLE);
                rl_flist.setVisibility(View.GONE);
                rl_flist.startAnimation(animShow);
                rv_flist.removeAllViews();
                ll_flist_wrapper.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.iv_friend_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFriendList(Webservice.getFriendList, 1);
            }
        });

        findViewById(R.id.iv_pending_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFriendList(Webservice.getPendingList, 2);
            }
        });
        findViewById(R.id.iv_rejected_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFriendList(Webservice.getRejectList, 3);
            }
        });
        findViewById(R.id.iv_blocked_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFriendList(Webservice.getBlockedList, 4);
            }
        });

        iv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (moodList.size() > 0) {
                    showBottomDialog(
                            Gravity.BOTTOM,
                            true,
                            false,
                            true
                    );
                } else {
                    getMoodList();
                }


            }
        });

    }

    private void getMoodList() {

        System.out.println("inside mood");

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {
            moodList.clear();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.moodList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("mood list response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    JSONArray list = jsonObject.optJSONArray("list");

                                    for (int i = 0; i < list.length(); i++) {
                                        JSONObject jo = list.optJSONObject(i);
                                        MoodModel mm = new MoodModel(jo.optString("id"),
                                                jo.optString("mood"),
                                                jo.optString("moodimgurl"));
                                        moodList.add(mm);
                                    }

                                    //getting my mood ID
                                    GlobalVariable.myMoodID = String.valueOf(jsonObject.optInt("selectedmood"));


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
                    params.put("rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    params.put("id", "");
                    return params;
                }
            };
            queue.add(postRequest);
        }

    }

    private void loadFriendList(String urlToFire, final int friendListType) {

        ll_flist_wrapper.setVisibility(View.VISIBLE);
        flist_loader.setVisibility(View.VISIBLE);
        rv_flist.setVisibility(View.GONE);

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            friendList.clear();
            System.out.println("url fire : " + urlToFire);
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, urlToFire,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("f list response : " + response.toString());
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

                                        if (!pref.getStringPreference(MapActivity.this, "user_id")
                                                .equalsIgnoreCase(listObj.optString("rowid"))) {
                                            friendList.add(new AllUserListModel(
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
                                                    "",
                                                    distanceFromMe,
                                                    0

                                            ));
                                        }


                                    }

                                    if (friendList.size() > 0) {


                                        flist_loader.setVisibility(View.GONE);
                                        rv_flist.setVisibility(View.VISIBLE);

                                        FriendListAdapter fla = new FriendListAdapter(MapActivity.this,
                                                R.layout.friend_list_item, friendList, friendListType);
                                        LinearLayoutManager lm
                                                = new LinearLayoutManager(MapActivity.this,
                                                LinearLayoutManager.VERTICAL, false);
                                        rv_flist.setLayoutManager(lm);
                                        rv_flist.setAdapter(fla);


                                    } else {
                                        flist_loader.setVisibility(View.GONE);
                                        Toast.makeText(MapActivity.this, "No item found",
                                                Toast.LENGTH_SHORT).show();
                                    }

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
                    params.put("rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }


    }

    private void showTextDialogWithYesNo(Context context, String text, final String gender, final String visibilityStatusToSet) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Alert")
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with yes
                        changeVisibilityStatus(gender, visibilityStatusToSet);

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

    private void changeVisibilityStatus(final String gender, final String visibilityStatusToSet) {

        final ProgressDialog dialog = new ProgressDialog(MapActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            System.out.println("visibility status called");

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.visibleInvisible,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("visibility response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    if (visibilityStatusToSet.equalsIgnoreCase("1")) {
                                        if (gender.equalsIgnoreCase("male")) {
                                            iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.visible_male));
                                        } else if (gender.equalsIgnoreCase("female")) {
                                            iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.visible_female));
                                        }
                                    } else if (visibilityStatusToSet.equalsIgnoreCase("0")) {
                                        tempMarker.setVisible(true);
                                        tempMarker.setPosition(new LatLng(Double.valueOf(GlobalVariable.currentLatitude),
                                                Double.valueOf(GlobalVariable.currentLongitude)));
                                        if (gender.equalsIgnoreCase("male")) {
                                            iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.invisible_male));
                                        } else if (gender.equalsIgnoreCase("female")) {
                                            iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.invisible_female));
                                        }
                                    }

                                    visibilityStatus = visibilityStatusToSet;
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
                    String vs = "";
                    if (visibilityStatusToSet.equalsIgnoreCase("0")) {
                        vs = "false";
                    } else if (visibilityStatusToSet.equalsIgnoreCase("1")) {
                        vs = "true";
                    }

                    params.put("rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    params.put("isvisible", vs);

                    System.out.println("params sending : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }

    private void showRestaurantsInMap(String latitude, String longitude) {
        ll_rv_wrapper.setVisibility(View.VISIBLE);
        nearby_loader.setVisibility(View.VISIBLE);
        rv_nearby.setVisibility(View.GONE);

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            restaurantList.clear();
            RequestQueue queue = Volley.newRequestQueue(this);
            System.out.println("google api  : " + "https://maps.googleapis.com/maps/api/place/" +
                    "nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + searchDistance + "&type="
                    + searchType + "&key=AIzaSyCUA98jhSIi-uvJpsGwFFJ2XKfppObMXro");
            StringRequest postRequest = new StringRequest(Request.Method.GET, "https://maps.googleapis.com/maps/api/place/" +
                    "nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + searchDistance + "&type="
                    + searchType + "&key=AIzaSyCUA98jhSIi-uvJpsGwFFJ2XKfppObMXro",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("google response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    JSONArray results = jsonObject.optJSONArray("results");
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject obj = results.optJSONObject(i);
                                        JSONObject geometry = obj.optJSONObject("geometry");
                                        JSONObject location = geometry.optJSONObject("location");
                                        JSONObject open_now = obj.optJSONObject("opening_hours");

                                        boolean isRestaurantOpened = false;

                                        if (open_now != null) {
                                            System.out.println("open now  : " + open_now.optBoolean("open_now"));
                                            isRestaurantOpened = open_now.optBoolean("open_now");
                                        }

                                        double rating = 0.0;
                                        if (obj.optString("rating") != null) {
                                            System.out.println("rating : " + obj.optDouble("rating"));
                                            rating = obj.optDouble("rating");
                                        }

                                        int distanceFromMe = distanceBetween(Double.valueOf(GlobalVariable.currentLatitude),
                                                Double.valueOf(GlobalVariable.currentLongitude),
                                                location.optDouble("lat"),
                                                location.optDouble("lng"));

                                        restaurantList.add(new RestaurantList(
                                                obj.optString("id"),
                                                obj.optString("name"),
                                                location.optDouble("lat"),
                                                location.optDouble("lng"),
                                                obj.optString("vicinity"),
                                                isRestaurantOpened,
                                                obj.optString("icon"),
                                                rating,
                                                distanceFromMe
                                        ));

                                        System.out.println("distance from me : " + distanceFromMe);
                                    }


                                    System.out.println("restaurant list size : " + restaurantList.size());

                                    if (restaurantList.size() > 0) {

                                        /*for (int j = 0; j < restaurantList.size(); j++) {
                                            LatLng resLatLng = new LatLng(Double.valueOf(restaurantList.get(j).getLatitude()),
                                                    Double.valueOf(restaurantList.get(j).getLongitude()));
                                            restaurantMarkerOptions = new MarkerOptions()
                                                    .position(resLatLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.res_marker))
                                                    .title(restaurantList.get(j).getName());
                                            restaurantMarkers.add(mMap.addMarker(restaurantMarkerOptions));

                                        }*/

                                        for (int k = 0; k < restaurantList.size(); k++) {
                                            Collections.sort(restaurantList, new Comparator<RestaurantList>() {
                                                public int compare(RestaurantList obj1, RestaurantList obj2) {
                                                    // ## Ascending order
                                                    return Integer.valueOf(obj1.distance).compareTo(obj2.distance); // To compare string values
                                                    // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                                                    // ## Descending order
                                                    // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                                    // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                                                }
                                            });
                                        }

                                        nearby_loader.setVisibility(View.GONE);
                                        rv_nearby.setVisibility(View.VISIBLE);

                                        NearbyAdapter nba = new NearbyAdapter(MapActivity.this,
                                                R.layout.nearby_list_item, restaurantList);
                                        LinearLayoutManager lm
                                                = new LinearLayoutManager(MapActivity.this,
                                                LinearLayoutManager.VERTICAL, false);
                                        rv_nearby.setLayoutManager(lm);
                                        rv_nearby.setAdapter(nba);


                                    } else {
                                        nearby_loader.setVisibility(View.GONE);
                                        Toast.makeText(MapActivity.this, "No item found",
                                                Toast.LENGTH_SHORT).show();
                                    }

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

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }


    }

    private void sendLocationToServer(final double latitude, final double longitude) {

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {
            String currentTimestamp = String.valueOf((System.currentTimeMillis() / 1000));
            System.out.println("ctT : " + currentTimestamp);
            userList.clear();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.sendLatLng,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("lt lng response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    if (jsonObject.optInt("status") == 1) {
                                        JSONArray list = jsonObject.optJSONArray("list");
                                        for (int i = 0; i < list.length(); i++) {
                                            JSONObject jo = list.optJSONObject(i);
                                            userList.add(new UserModel(
                                                    jo.optString("rowid"),
                                                    jo.optString("my_lat"),
                                                    jo.optString("my_long"),
                                                    jo.optString("name"),
                                                    jo.optString("gender"),
                                                    jo.optString("pictureurl"),
                                                    jo.optBoolean("isvisible"),
                                                    jo.optInt("islike"),
                                                    jo.optString("fbid"),
                                                    jo.optString("profession"),
                                                    jo.optString("opponent_profession"),
                                                    jo.optString("interested_in"),
                                                    jo.optString("moodid"),
                                                    jo.optInt("isfriend"),
                                                    jo.optString("sourcelatlong"),
                                                    jo.optString("destlatlong")
                                            ));
                                        }

                                        //setOtherMarkers();


                                    }

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

                    params.put("Rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    params.put("My_lat", String.valueOf(latitude));
                    params.put("My_long", String.valueOf(longitude));
                    params.put("status", "true");

                    System.out.println("params sending : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }

    private void setOtherMarkers() {

        System.out.println("list size : " + userLists.size());

        if (mCurrentLocation.getLatitude() > 0.0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude()));
            mMap.animateCamera(cameraUpdate);
        }

        if (userLists.size() == 0) {
            dialog.dismiss();// just to dismiss the loader when size is 0
            if (tempMarker.isVisible()) {
                tempMarker.setPosition(new LatLng(Double.valueOf(GlobalVariable.currentLatitude),
                        Double.valueOf(GlobalVariable.currentLongitude)));
            }
        }
        for (int i = 0; i < userLists.size(); i++) {
            LatLng oppnentLatLng = new LatLng(Double.valueOf(userLists.get(i).getLatitude()),
                    Double.valueOf(userLists.get(i).getLongitude()));

            //System.out.println(" userLists.get(i).getUrl() : " +  userLists.get(i).getUrl());

            if (userLists.get(i).getId().equalsIgnoreCase(pref.getStringPreference(MapActivity.this, "user_id"))) {
                if (tempMarker != null) {
                    //tempMarker.remove();
                    tempMarker.setVisible(false);
                }

                opponetMarkerOptions = new MarkerOptions()
                        .position(oppnentLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.profile_default,
                                pref.getStringPreference(MapActivity.this, "fb_pic"), userLists.get(i).getGender())))
                        .title("Me");

                //draw your own route
                System.out.println("destination found : " + userLists.get(i).getDestLatLng());
                System.out.println("source found : " + userLists.get(i).getSourceLatLng());
//                System.out.println("userLists.get(i).getDestLatLng().split(\"|\")[0] : " + userLists.get(i).getDestLatLng().split("7")[0]);

                if (userLists.get(i).getDestLatLng().length() > 0) {
                    //route found
                    System.out.println("&& " + userLists.get(i).getSourceLatLng().split("\\|")[0] + " ++ " +
                            userLists.get(i).getSourceLatLng().split("\\|")[1] + " ++ " +
                            userLists.get(i).getDestLatLng().split("\\|")[0] + " ++ " +
                            userLists.get(i).getDestLatLng().split("\\|")[1]);
                    String sp = userLists.get(i).getSourceLatLng().toString();

                    System.out.println("split print : " + sp.split("\\|")[0].toString());

                    routeForMe = "me";
                    drawRoute(userLists.get(i).getSourceLatLng().split("\\|")[0],
                            userLists.get(i).getSourceLatLng().split("\\|")[1],
                            userLists.get(i).getDestLatLng().split("\\|")[0],
                            userLists.get(i).getDestLatLng().split("\\|")[1],
                            polylineMyRoute);
                }


            } else {
                opponetMarkerOptions = new MarkerOptions()
                        .position(oppnentLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.profile_default,
                                userLists.get(i).getUrl(), userLists.get(i).getGender())))
                        .title(userLists.get(i).getName());

                System.out.println("mood is : " + userLists.get(i).getMoodid());
            }

            if (opponentMarkerList.size() == 0) {
                opponentMarkerList.add(new OpponentMarkerModel(
                        mMap.addMarker(opponetMarkerOptions),
                        userLists.get(i).getId()
                ));
                System.out.println("setting tag to marker : " + " title : " + opponentMarkerList.get(i).getOpponentMarker()
                        .getTitle()
                        + " with id :" + userLists.get(i).getId());
                opponentMarkerList.get(i).getOpponentMarker().setTag(userLists.get(i).getId());

            } else {
                String isMatched = "";
                int pos = 0;
                for (int j = 0; j < opponentMarkerList.size(); j++) {
                    // System.out.println(opponentMarkerList.get(j).getUserID() + " == " + userLists.get(i).getId());
                    if (opponentMarkerList.get(j).getUserID().equalsIgnoreCase(userLists.get(i).getId())) {
                        //System.out.println("yes found");
                        isMatched = "1";
                        pos = j;
                    } else {
                        // System.out.println("not found");

                    }
                }

                if (isMatched.equalsIgnoreCase("1")) {
                    System.out.println("is matched");
                    opponentMarkerList.get(pos).getOpponentMarker().setPosition(oppnentLatLng);
                    /*System.out.println("setting tag to marker is matched: " + " title : " + opponentMarkerList.get(i).getOpponentMarker().getTitle()
                            + " with id :" + userLists.get(i).getId());*/
                    opponentMarkerList.get(pos).getOpponentMarker().setTag(userLists.get(i).getId());
                    opponentMarkerList.get(pos).getOpponentMarker()
                            .setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.profile_default,
                                    userLists.get(i).getUrl(), userLists.get(i).getGender())));
                } else {
                    //System.out.println("new match found");
                    opponentMarkerList.add(new OpponentMarkerModel(
                            mMap.addMarker(opponetMarkerOptions),
                            userLists.get(i).getId()
                    ));
/*                    System.out.println("setting tag to marker in else: " + " title : " + opponentMarkerList.get(i).getOpponentMarker().getTitle()
                            + " with id :" + userLists.get(i).getId());*/
                    opponentMarkerList.get(i).getOpponentMarker().setTag(userLists.get(i).getId());

                }

            }

            dialog.dismiss();

        }

        //remove marker
        int position = 0;
        for (int k = 0; k < opponentMarkerList.size(); k++) {
            position = k;
            int count = 0;
            for (int x = 0; x < userLists.size(); x++) {
                System.out.println("remove check : " + opponentMarkerList.get(k).getUserID() + " == " +
                        userLists.get(x).getId());
                if (opponentMarkerList.get(k).getUserID().equalsIgnoreCase(userLists.get(x).getId())) {
                    System.out.println("inside if");
                } else {
                    //remove
                    count++;
                    System.out.println("inside else " + count);

                }

            }
            System.out.println("count : " + count);
            if (count == userLists.size()) {
                opponentMarkerList.get(position).getOpponentMarker().remove();
                System.out.println("ML size : " + opponentMarkerList.size());
                opponentMarkerList.remove(position);//new addition
                count = 0;
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        // Add a marker in my location and move the camera
        LatLng myLatLng = new LatLng(GlobalVariable.currentLatitude,
                GlobalVariable.currentLongitude);
//        mMap.addMarker(new
//                MarkerOptions().position(myLatLng).title("Me"));
        System.out.println("moving camera to : " + GlobalVariable.currentLatitude + " :: " + GlobalVariable.currentLongitude);
        System.out.println("my gender : " + pref.getStringPreference(MapActivity.this, "fb_gender"));
        tempMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey_default))
                .title("Me"));
        tempMarker.setTag("0");//blank tag

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(10.0f);

        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println("clicked marker : " + marker.getTag());

                int position = 0;
                for (int i = 0; i < userLists.size(); i++) {
                    if (marker.getTag() != null) {
                        System.out.println("get tag if: " + marker.getTag());
                        if (marker.getTag().toString().equalsIgnoreCase(userLists.get(i).getId())) {
                            position = i;
                            if (!marker.getTag().toString().equalsIgnoreCase(pref.getStringPreference(MapActivity.this,
                                    "user_id")))
                                showDialog(MapActivity.this, 0, 200, position, userLists.get(i).getId());
                        }
                    } else {
                        System.out.println("get tag else: " + marker.getTag());
                    }
                }

                /*if (!marker.getTitle().equalsIgnoreCase("Me")) {
                    showDialog(MapActivity.this, 0, 250, position);
                }*/
                return false;
            }
        });


    }

    private void drawRoute(String lat1, String lng1, String lat2, String lng2, Polyline route) {

        LatLng origin = new LatLng(Double.valueOf(lat1), Double.valueOf(lng1));
        LatLng dest = new LatLng(Double.valueOf(lat2), Double.valueOf(lng2));

        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (routeForMe.equalsIgnoreCase("me")) {
                ParserTask parserTask = new ParserTask();
                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            } else if (routeForMe.equalsIgnoreCase("other")) {
                ParserTaskForOther parserTaskForOther = new ParserTaskForOther();
                // Invokes the thread for parsing the JSON data
                parserTaskForOther.execute(result);
            }


        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(14);
                lineOptions.color(getResources().getColor(R.color.colorAccent));
            }

            // Drawing polyline in the Google Map for the i-th route
            if (polylineMyRoute != null) {
                polylineMyRoute.remove(); //removing the previous route and adding new route
                polylineMyRoute = mMap.addPolyline(lineOptions);
            } else {
                polylineMyRoute = mMap.addPolyline(lineOptions);

            }

          /*  // Drawing polyline in the Google Map for the i-th route
            if (polylineOtherRoute != null) {
                polylineOtherRoute.remove(); //removing the previous route and adding new route
                polylineOtherRoute = mMap.addPolyline(lineOptions);
            } else {
                polylineOtherRoute = mMap.addPolyline(lineOptions);

            }*/

        }
    }

    private class ParserTaskForOther extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(14);
                lineOptions.color(getResources().getColor(R.color.colorPrimaryDark));
            }

            // Drawing polyline in the Google Map for the i-th route
           /* if (polylineMyRoute != null) {
                polylineMyRoute.remove(); //removing the previous route and adding new route
                polylineMyRoute = mMap.addPolyline(lineOptions);
            } else {
                polylineMyRoute = mMap.addPolyline(lineOptions);

            }*/

            // Drawing polyline in the Google Map for the i-th route
            if (polylineOtherRoute != null) {
                polylineOtherRoute.remove(); //removing the previous route and adding new route
                polylineOtherRoute = mMap.addPolyline(lineOptions);
            } else {
                polylineOtherRoute = mMap.addPolyline(lineOptions);

            }

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onResume() {

        //isListIconClicked = false;


        if (!GlobalVariable.isListIconClickedOnMap) {
            visibilityStatus = "0";
            if (pref.getStringPreference(MapActivity.this, "fb_gender").equalsIgnoreCase("male")) {
                iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.invisible_male));
            } else if (pref.getStringPreference(MapActivity.this, "fb_gender").equalsIgnoreCase("female")) {
                iv_visible.setImageDrawable(getResources().getDrawable(R.drawable.invisible_female));
            }
        } else {
            GlobalVariable.isListIconClickedOnMap = false;
        }


        //getOtherUsers();
        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                System.out.println("running handler");
                if (mMap != null) {
                    getOtherUsers();
                }

                runnable = this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
        //stop the background service
//        stopService(new Intent(this, BackgroundGpsService.class)); //stop service
    }

    private void getOtherUsers() {

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            userLists.clear();
            getUserQueue = Volley.newRequestQueue(this);
            getUserStringRequest = new StringRequest(Request.Method.POST, Webservice.getUserList,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("get user list response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    if (jsonObject.optInt("status") == 1) {
                                        JSONArray list = jsonObject.optJSONArray("list");
                                        for (int i = 0; i < list.length(); i++) {
                                            JSONObject jo = list.optJSONObject(i);
                                            System.out.println("entering like is : " + jo.optInt("islike"));
                                            userLists.add(new UserModel(
                                                    jo.optString("rowid"),
                                                    jo.optString("my_lat"),
                                                    jo.optString("my_long"),
                                                    jo.optString("name"),
                                                    jo.optString("gender"),
                                                    jo.optString("pictureurl"),
                                                    jo.optBoolean("isvisible"),
                                                    jo.optInt("islike"),
                                                    jo.optString("fbid"),
                                                    jo.optString("profession"),
                                                    jo.optString("opponent_profession"),
                                                    jo.optString("interested_in"),
                                                    jo.optString("moodid"),
                                                    jo.optInt("isfriend"),
                                                    jo.optString("sourcelatlong"),
                                                    jo.optString("destlatlong")
                                            ));
                                        }


                                        setOtherMarkers();
                                    }

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
                    params.put("rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    return params;
                }
            };
            //To avoid Timeout Error
            getUserStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            getUserQueue.add(getUserStringRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }

    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
        System.out.println("on destroy...");
        //start the background service
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

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable);
        super.onPause();
        if (!GlobalVariable.isListIconClickedOnMap) {
            sendOfflineStatusToServer(myCurrentLat, myCurrentLng);
        }
        stopLocationUpdates();
    }

    private void sendOfflineStatusToServer(final double latitude, final double longitude) {

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            System.out.println("send offline status called");

            userList.clear();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.sendLatLng,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("offline response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    if (jsonObject.optInt("status") == 1) {
                                        JSONArray list = jsonObject.optJSONArray("list");
                                        for (int i = 0; i < list.length(); i++) {
                                            JSONObject jo = list.optJSONObject(i);
                                            userList.add(new UserModel(
                                                    jo.optString("rowid"),
                                                    jo.optString("my_lat"),
                                                    jo.optString("my_long"),
                                                    jo.optString("name"),
                                                    jo.optString("gender"),
                                                    jo.optString("pictureurl"),
                                                    jo.optBoolean("isvisible"),
                                                    jo.optInt("isLike"),
                                                    jo.optString("fbid"),
                                                    jo.optString("profession"),
                                                    jo.optString("opponent_profession"),
                                                    jo.optString("interested_in"),
                                                    jo.optString("moodid"),
                                                    jo.optInt("isfriend"),
                                                    jo.optString("sourcelatlong"),
                                                    jo.optString("destlatlong")
                                            ));
                                        }

                                    }

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

                    params.put("Rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    params.put("My_lat", String.valueOf(latitude));
                    params.put("My_long", String.valueOf(longitude));
                    params.put("status", "false");

                    System.out.println("params sending : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop fired ..............");
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.
                requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    private void updateLocation() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
//            Toast.makeText(MapActivity.this, "At Time: " + mLastUpdateTime + "\n" +
//                    "Latitude: " + lat + "\n" +
//                    "Longitude: " + lng + "\n" +
//                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                    "Provider: " + mCurrentLocation.getProvider(), Toast.LENGTH_SHORT).show();

            myCurrentLat = mCurrentLocation.getLatitude();
            myCurrentLng = mCurrentLocation.getLongitude();
            GlobalVariable.currentLatitude = mCurrentLocation.getLatitude();
            GlobalVariable.currentLongitude = mCurrentLocation.getLongitude();

            System.out.println("GlobalVariable.currentLatitude : " + GlobalVariable.currentLatitude);

            //currentZoomLevel =
            /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude()));
            mMap.animateCamera(cameraUpdate);*/


            sendLocationToServer(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId, String url, String gender) {
        System.out.println("url : " + url);

        View customMarkerView = null;
        if (gender.equalsIgnoreCase("male")) {
            customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_marker_male, null);
        } else if (gender.equalsIgnoreCase("female")) {
            customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_marker, null);
        } else {
            customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_marker_male, null);
        }

        CircleImageView markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.profile_image);
        //markerImageView.setImageResource(resId);
        if (url.length() > 0) {
            Picasso.with(MapActivity.this).load(url).into(markerImageView);
        }
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public void showDialog(final Context context, int x, int y, final int position, final String clickedMarkerID) {
        // x -->  X-Cordinate
        // y -->  Y-Cordinate
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (userLists.get(position).getGender().equalsIgnoreCase("male")) {
            dialog.setContentView(R.layout.male_profile);

        } else if (userLists.get(position).getGender().equalsIgnoreCase("female")) {
            dialog.setContentView(R.layout.female_profile);
        }
        dialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        wmlp.x = x;
        wmlp.y = y;

        TextView tv_name_age = (TextView) dialog.findViewById(R.id.tv_name_age);
        TextView tv_working = (TextView) dialog.findViewById(R.id.tv_working);
        TextView tv_searching_for = (TextView) dialog.findViewById(R.id.tv_searching_for);
        final ImageView iv_like = (ImageView) dialog.findViewById(R.id.iv_like);
        final ImageView iv_dislike = (ImageView) dialog.findViewById(R.id.iv_dislike);
        final ImageView iv_chat = (ImageView) dialog.findViewById(R.id.iv_chat);
        //ImageView iv_profile = (ImageView) dialog.findViewById(R.id.iv_profile);
        ImageView iv_navigate = (ImageView) dialog.findViewById(R.id.iv_navigate);
        ImageView iv_online = (ImageView) dialog.findViewById(R.id.iv_online);
        CircleImageView profile_image = (CircleImageView) dialog.findViewById(R.id.profile_image);
        System.out.println("url to hit : " + userLists.get(position).getUrl());
        Picasso.with(context).load(userLists.get(position).getUrl()).into(profile_image);
        String gender = "";
        if (userLists.get(position).getGender().equalsIgnoreCase("male")) {
            gender = "M";
        } else {
            gender = "F";
        }
        tv_name_age.setText(userLists.get(position).getName() + ", XX, " + gender);
        tv_working.setText("Working as " + userLists.get(position).getMyProfession());
        tv_searching_for.setText("Searching for " + userLists.get(position).getOpponentProfession());


        //Like Dislike
        System.out.println(userLists.get(position).getName() + " is liked : " + userLists.get(position).getIsLiked());
        if (userLists.get(position).getIsLiked() == 0) {
            //do nothing
            iv_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
            iv_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike));
        } else if (userLists.get(position).getIsLiked() == 1 || userLists.get(position).getIsLiked() == 2) {
            //do nothing
            iv_like.setImageDrawable(getResources().getDrawable(R.drawable.like_fill));
            iv_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike));
        } else if (userLists.get(position).getIsLiked() == 3) {
            //do nothing
            iv_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
            iv_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike_fill));
        }

        //chat
        if (userLists.get(position).getIsFriend() > 0) {
            iv_chat.setImageDrawable(getResources().getDrawable(R.drawable.chat));
        } else {
            iv_chat.setImageDrawable(getResources().getDrawable(R.drawable.chat_disable));
        }

        //set mood
        if (!userLists.get(position).getMoodid().equalsIgnoreCase("0")) {
            iv_navigate.setVisibility(View.VISIBLE);


            if (!getMoodDetailsFromID(userLists.get(position).getMoodid()).equalsIgnoreCase("")) {
                if (userLists.get(position).getDestLatLng().length() > 0) {
                    //user is driving
                    System.out.println("user is travelling");
                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(1000);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.REVERSE);
                    iv_navigate.startAnimation(animation);
                    Picasso.with(MapActivity.this).load(getMoodDetailsFromID(userLists.get(position).getMoodid()))
                            .resize(40, 40)
                            .into(iv_navigate);
                } else {
                    System.out.println("user is not travelling");
                    Picasso.with(MapActivity.this).load(getMoodDetailsFromID(userLists.get(position).getMoodid()))
                            .resize(40, 40)
                            .into(iv_navigate);
                }
            }
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.findViewById(R.id.iv_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("like user");
                System.out.println("userlist size now  : " + userLists.size());
                if (isIdExists(clickedMarkerID)) {
//                    System.out.println("userLists.get(position).getIsLiked() : " + userLists.get(position).getIsLiked());
                    iv_chat.setImageDrawable(getResources().getDrawable(R.drawable.chat));
                    if (userLists.get(position).getIsLiked() == 0 || userLists.get(position).getIsLiked() == 3) {
                        likeDislikeUser(1, position, iv_like, iv_dislike, Webservice.sendFriendReq);
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });

        dialog.findViewById(R.id.iv_dislike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isIdExists(clickedMarkerID)) {
                    if (userLists.get(position).getIsLiked() != 3) {
                        iv_chat.setImageDrawable(getResources().getDrawable(R.drawable.chat_disable));
                        likeDislikeUser(3, position, iv_like, iv_dislike, Webservice.rejectFriendReq);
                    }
                }
            }
        });

        dialog.findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to chat room
                if (isIdExists(clickedMarkerID)) {

                    if (userLists.get(position).getIsFriend() > 0) {
                        GlobalVariable.currentOpponentChatID = clickedMarkerID;
                        GlobalVariable.isListIconClickedOnMap = true;
                        startActivity(new Intent(MapActivity.this, ChatActivity.class));
                    }
                }
                dialog.dismiss();

            }
        });

        dialog.findViewById(R.id.iv_navigate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigate
                dialog.dismiss();
               /* System.out.println("GCL : " + GlobalVariable.currentLatitude);
                String uri = "http://maps.google.com/maps?saddr=" + GlobalVariable.currentLatitude + ","
                        + GlobalVariable.currentLongitude + "&daddr=" + userLists.get(position).getLatitude() + "," +
                        userLists.get(position).getLongitude();*/
                /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                context.startActivity(intent);*/

                //if travelling
                routeForMe = "other";
                if (userLists.get(position).getDestLatLng().length() > 0) {
                    drawRoute(userLists.get(position).getSourceLatLng().split("\\|")[0],
                            userLists.get(position).getSourceLatLng().split("\\|")[1],
                            userLists.get(position).getDestLatLng().split("\\|")[0],
                            userLists.get(position).getDestLatLng().split("\\|")[1],
                            polylineOtherRoute);
                }

            }
        });

        dialog.show();
    }

    private void likeDislikeUser(final int likeStatus, final int position,
                                 final ImageView iv_like, final ImageView iv_dislike, String urlToFire) {

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            final ProgressDialog dialog = new ProgressDialog(MapActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, urlToFire,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("like response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));

                                    //Like Dislike
                                    if (likeStatus == 0) {
                                        //do nothing
                                        iv_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                        iv_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike));
                                    } else if (likeStatus == 1) {
                                        //do nothing
                                        iv_like.setImageDrawable(getResources().getDrawable(R.drawable.like_fill));
                                        iv_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike));
                                    } else if (likeStatus == 3) {
                                        //do nothing
                                        iv_like.setImageDrawable(getResources().getDrawable(R.drawable.like));
                                        iv_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike_fill));
                                    }

                                    userLists.get(position).setIsLiked(likeStatus); // set the user like status
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

                    params.put("likerowid", userLists.get(position).getId());
                    //params.put("likefbid", userLists.get(position).getFbID());
                    params.put("islike", String.valueOf(likeStatus));
                    params.put("rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    //params.put("fbid", pref.getStringPreference(MapActivity.this, "fb_id"));

                    System.out.println("like params : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }


        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static int distanceBetween(Double point1, Double point2, Double point3, Double point4) {

        //Returns in meter

        LatLng ltln1 = new LatLng(point1, point2);
        LatLng ltln2 = new LatLng(point3, point4);

        if (ltln1 == null || ltln2 == null) {
            return 0;
        }

        System.out.println("new distance : " + SphericalUtil.computeDistanceBetween(ltln1, ltln2));
        return (int) SphericalUtil.computeDistanceBetween(ltln1, ltln2);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_option) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            logoutTheUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutTheUser() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MapActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MapActivity.this);
        }
        builder.setTitle("Alert")
                .setMessage("Are you sure you want to logout from the app?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with yes
                        pref.clearPreference();
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(MapActivity.this, LoginActivity.class));
                        finishAffinity();

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

    private void showBottomDialog(int gravity, boolean showHeader, boolean showFooter, boolean expanded) {
        boolean isGrid;
        Holder holder;
        holder = new ListHolder();
        isGrid = false;
        /*switch (holderId) {
            case R.id.basic_holder_radio_button:
                holder = new ViewHolder(R.layout.content);
                isGrid = false;
                break;
            case R.id.list_holder_radio_button:
                holder = new ListHolder();
                isGrid = false;
                break;
            default:
                holder = new GridHolder(3);
                isGrid = true;
        }*/

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
            }
        };

        OnItemClickListener itemClickListener = new OnItemClickListener() {
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                System.out.println("clicked bottom");
                TextView textView = (TextView) view.findViewById(R.id.text_view);
                String clickedAppName = textView.getText().toString();
                dialog.dismiss();
                Toast.makeText(MapActivity.this, clickedAppName + " clicked", Toast.LENGTH_LONG).show();
            }
        };

        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogPlus dialog) {
                //        Toast.makeText(MainActivity.this, "dismiss listener invoked!", Toast.LENGTH_SHORT).show();
            }
        };

        OnCancelListener cancelListener = new OnCancelListener() {
            @Override
            public void onCancel(DialogPlus dialog) {
                //        Toast.makeText(MainActivity.this, "cancel listener invoked!", Toast.LENGTH_SHORT).show();
            }
        };

        SimpleBottomAdapter adapter = new SimpleBottomAdapter(MapActivity.this, isGrid, moodList, GlobalVariable.myMoodID);


        if (showHeader && !showFooter) {
            showNoFooterDialog(holder, gravity, adapter, clickListener, itemClickListener, dismissListener, cancelListener,
                    expanded);
            return;
        }


    }


    private void showNoFooterDialog(Holder holder, int gravity, BaseAdapter adapter,
                                    OnClickListener clickListener, OnItemClickListener itemClickListener,
                                    OnDismissListener dismissListener, OnCancelListener cancelListener,
                                    boolean expanded) {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setHeader(R.layout.header)
                .setCancelable(true)
                .setGravity(gravity)
                .setAdapter(adapter)
                .setOnClickListener(clickListener)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                        Log.d("clicked", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]" + moodList.get(position).getMoodName());
                        view.setBackgroundColor(getResources().getColor(R.color.light_grey));
                        GlobalVariable.myMoodID = moodList.get(position).getId();
                        setMyMood(moodList.get(position).getId(), moodList.get(position).getMoodName());
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(dismissListener)
                .setOnCancelListener(cancelListener)
                .setExpanded(expanded)
                .create();
        dialog.show();
    }

    private void setMyMood(final String moodID, final String moodName) {

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            final ProgressDialog dialog = new ProgressDialog(MapActivity.this);
            dialog.setMessage("Setting Mood...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.setMood,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("set mood response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));

                                    //Like Dislike

                                    dialog.dismiss();

                                    //check whether selected long drive
                                    if (moodName.equalsIgnoreCase("long drive")) {
                                        showDestinationDialog();
                                    }

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


                    params.put("moodid", moodID);
                    params.put("rowid", pref.getStringPreference(MapActivity.this, "user_id"));

                    System.out.println("set mood params : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }


    private boolean isIdExists(String id) {
        boolean existStatus = false;
        for (int i = 0; i < userLists.size(); i++) {
            if (userLists.get(i).getId().equalsIgnoreCase(id)) {
                existStatus = true;
            }
        }

        return existStatus;
    }

    private String getMoodDetailsFromID(String id) {
        String moodURL = "";
        for (int i = 0; i < moodList.size(); i++) {
            if (id.equalsIgnoreCase(moodList.get(i).getId())) {
                moodURL = moodList.get(i).getMoodUrl();
            }
        }
        System.out.println("moodURL : " + moodURL);
        return moodURL;
    }

    public void showDestinationDialog() {


        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MapActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MapActivity.this);
        }
        builder.setTitle("Set Destination")
                .setMessage("Please set your destination point so that your friends know where are you travelling.")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with search
                        Intent intent =
                                null;
                        try {

                            GlobalVariable.isListIconClickedOnMap = true;//so that it does not get offline
                            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MapActivity.this);
                            startActivityForResult(intent, 1111);

                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setCancelable(false)
                .show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1111) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Place ltlng: " + place.getLatLng());
                String value = place.getLatLng().toString();
                System.out.println(value.substring(10, value.length() - 1));
                String splittedValue = value.substring(10, value.length() - 1);
                System.out.println(splittedValue.split(",")[0]);
                sendDestinationPointToServer(splittedValue.split(",")[0], splittedValue.split(",")[1]);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void sendDestinationPointToServer(final String lat, final String lng) {

        if (CheckNetwork.isInternetAvailable(MapActivity.this)) {

            final ProgressDialog dialog = new ProgressDialog(MapActivity.this);
            dialog.setMessage("Sharing your destination...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.setDestination,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            if (response != null) {
                                System.out.println("set dest response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));

                                    routeForMe = "me";
                                    drawRoute(String.valueOf(GlobalVariable.currentLatitude),
                                            String.valueOf(GlobalVariable.currentLongitude), lat, lng, polylineMyRoute);
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


                    params.put("moodid", GlobalVariable.myMoodID);
                    params.put("rowid", pref.getStringPreference(MapActivity.this, "user_id"));
                    params.put("sourcelatlong", GlobalVariable.currentLatitude + "|" + GlobalVariable.currentLongitude);
                    params.put("destlatlong", lat + "|" + lng);
                    params.put("endtime", "01:00");

                    System.out.println("set mood params : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }


}

