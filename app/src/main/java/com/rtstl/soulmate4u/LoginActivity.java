package com.rtstl.soulmate4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidadvance.topsnackbar.TSnackbar;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    LoginButton login_button;
    CallbackManager callbackManager;
    Context context;
    String fb_id = "";
    ProgressDialog dialog;
    private String fb_name = "", fb_email = "", fb_picUrl = "", fb_gender = "";
    Preferences pref;
    AccessToken accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        initView();


        findViewById(R.id.iv_fb_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, OtpVerificationActivity.class));
                finish();
            }
        });

    }

    private void initView() {

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading...");

        pref = new Preferences(context);

        getHashKey();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        //loginButton.setText("");
        //loginButton.setBackgroundResource(R.drawable.fbbutton);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetails(loginResult);

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                System.out.println("fb exception : " + exception);
            }
        });
    }

    private void getHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.rtstl.soulmate4u",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }


    }

    protected void getUserDetails(final LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object, GraphResponse response) {

                        System.out.println("user details json : " + json_object.toString());
                        JSONObject graphObj = response.getJSONObject();

                        fb_id = graphObj.optString("id");
                        fb_name = graphObj.optString("name");
                        fb_email = graphObj.optString("email");
                        fb_gender = graphObj.optString("gender");
                        JSONObject picObj = graphObj.optJSONObject("picture");
                        JSONObject data = picObj.optJSONObject("data");
                        fb_picUrl = data.optString("url");


                        System.out.println("loginResult.getAccessToken() : " + AccessToken.getCurrentAccessToken().getToken());

                        doFbLogin();


                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(650).height(650),gender");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    private void doFbLogin() {

        if (CheckNetwork.isInternetAvailable(context)) {

            dialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.addUser,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response

                            if (response != null) {
                                System.out.println("login response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    if (jsonObject.optInt("status") == 1) {

                                        JSONArray list = jsonObject.optJSONArray("list");
                                        for (int i = 0; i < list.length(); i++) {
                                            JSONObject listObj = list.optJSONObject(i);

                                            pref.storeStringPreference(context, "user_id",
                                                    listObj.optString("rowid"));
                                            pref.storeBooleanPreference(context, "is_otp_verified",
                                                    listObj.optBoolean("isotpverified"));
                                            pref.storeBooleanPreference(context, "is_basic_profile_updated",
                                                    listObj.optBoolean("isbasicprofileupdated"));

                                            pref.storeStringPreference(context, "fb_id", fb_id);
                                            pref.storeStringPreference(context, "fb_gender", fb_gender);
                                            pref.storeStringPreference(context, "fb_name", fb_name);
                                            pref.storeStringPreference(context, "fb_email", fb_email);
                                            pref.storeStringPreference(context, "is_login",
                                                    "1");
                                            pref.storeStringPreference(context, "fb_pic", fb_picUrl);
                                            System.out.println("is_login in login : " +
                                                    (pref.getStringPreference(LoginActivity.this, "is_login")));
                                        }


                                        if (pref.getBooleanPreference(context, "is_basic_profile_updated")) {
                                            pref.storeStringPreference(context,
                                                    "registration_completed", "1");
                                            startActivity(new Intent(LoginActivity.this,
                                                    MapActivity.class));
                                            finish();
                                        } else if (pref.getBooleanPreference(context, "is_otp_verified")) {
                                            pref.storeStringPreference(context,
                                                    "registration_completed", "0");
                                            startActivity(new Intent(LoginActivity.this,
                                                    BasicInformationActivity.class));
                                            finish();
                                        } else {
                                            startActivity(new Intent(LoginActivity.this,
                                                    OtpVerificationActivity.class));
                                            finish();
                                        }


                                    }

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
                    params.put("fbid", fb_id);
                    params.put("name", fb_name);
                    params.put("email", fb_email);
                    params.put("pictureurl", fb_picUrl);
                    params.put("gender", fb_gender);
                    params.put("device_id", "abc12345");
                    params.put("device_type", "android");

                    System.out.println("params sending : " + params);

                    return params;
                }
            };

            //To avoid Timeout Error
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
