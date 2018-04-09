package com.rtstl.soulmate4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidadvance.topsnackbar.TSnackbar;
import com.chaos.view.PinView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RTSTL17 on 27-12-2017.
 */

public class OtpVerificationActivity extends AppCompatActivity {

    PinView pinview;
    EditText et_phone;
    Context context;
    ProgressDialog dialog;
    Preferences pref;

    private static OtpVerificationActivity inst;

    public static OtpVerificationActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        context = this;

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Verifying please wait...");

        pref = new Preferences(this);

        et_phone = (EditText) findViewById(R.id.et_phone);

        pinview = (PinView) findViewById(R.id.pinView);
        pinview.setEnabled(false);
        pinview.setClickable(false);
        //pinview.setText("7634");
//        System.out.println("pin value : " + pinview.getText().toString());

        findViewById(R.id.btn_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                verifyPhoneNumber();

//                startActivity(new Intent(OtpVerificationActivity.this, BasicInformationActivity.class));
//                finish();
            }
        });

        findViewById(R.id.btn_resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                verifyPhoneNumber();
            }
        });

    }

    private void verifyPhoneNumber() {
        if (!CheckNetwork.isInternetAvailable(context)) {
            showSnackbar("No internet connection!");
        } else if (et_phone.getText().toString().length() != 10) {
            showSnackbar("Provide valid phone number!");
        } else {
            //success
            dialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.getOTP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response

                            if (response != null) {
                                System.out.println("phone response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));

                                    //dialog.dismiss();

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
                    params.put("rowid", pref.getStringPreference(context, "user_id"));
                    params.put("phone", et_phone.getText().toString());

                    System.out.println("params sending : " + params);

                    return params;
                }
            };
            queue.add(postRequest);
        }

    }

    public void setOTP(final String smsMessage) {
        System.out.println("otp set : " + smsMessage);
        pinview.setText(smsMessage);
        verifyOtpFromServer(smsMessage);
    }

    private void verifyOtpFromServer(final String otp) {
        //dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.verifyOTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response

                        if (response != null) {
                            System.out.println("otp verify response : " + response.toString());
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(String.valueOf(response));
                                if (jsonObject.optInt("status") == 1) {
                                    dialog.dismiss();
                                    pref.storeBooleanPreference(context, "is_otp_verified", true);
                                    startActivity(new Intent(OtpVerificationActivity.this,
                                            BasicInformationActivity.class));
                                    finish();
                                } else {
                                    showSnackbar("Something went wrong!");
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
                params.put("rowid", pref.getStringPreference(context, "user_id"));
                params.put("phone", et_phone.getText().toString());
                params.put("otp", otp);

                System.out.println("params sending : " + params);

                return params;
            }
        };
        queue.add(postRequest);
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

}
