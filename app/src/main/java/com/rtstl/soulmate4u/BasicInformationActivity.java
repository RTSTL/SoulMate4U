package com.rtstl.soulmate4u;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidadvance.topsnackbar.TSnackbar;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by RTSTL17 on 27-12-2017.
 */

public class BasicInformationActivity extends AppCompatActivity {

    EditText et_name, et_email, et_profession, et_partner_profession;
    TextView tv_dob;
    LinearLayout ll_basic_wrapper, ll_looking_wrapper;
    Button btn_basic, btn_looking;
    RadioGroup rg_looking_for, rg_service_provider;
    RadioButton rb_men, rb_women, rb_both, rb_yes, rb_no;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog dd;
    EmailValidator emailValidator;
    String name = "", email = "", dob = "", myProfession = "", interestedIn = "men",
            isServiceProvider = "false", partnerProfession = "";
    Preferences pref;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basic_info);

        Firebase.setAndroidContext(this);

        initView();

        tv_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });

        btn_basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateBasic();

            }
        });
        btn_looking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateLooking();
            }
        });
    }

    private void validateLooking() {

        if (et_profession.getText().toString().length() == 0) {
            showSnackbar("Provide your profession!");
        } else if (et_partner_profession.getText().toString().length() == 0) {
            showSnackbar("Provide what profession u are looking for!");
        } else {
            submitProfileData();
        }

    }

    private void submitProfileData() {

        if (CheckNetwork.isInternetAvailable(BasicInformationActivity.this)) {

            dialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, Webservice.updateUser,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response

                            if (response != null) {
                                System.out.println("basic response : " + response.toString());
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(String.valueOf(response));
                                    if (jsonObject.optInt("status") == 1) {


                                        //Register user into firebase
                                        registerUserInFirebase(email, pref.getStringPreference(BasicInformationActivity.this,
                                                "fb_id"));

                                       /* startActivity(new Intent(BasicInformationActivity.this,
                                                MapActivity.class));
                                        finish();*/
                                    }

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

                    //Intentionally left blank for update
                    params.put("Rowid", pref.getStringPreference(BasicInformationActivity.this, "user_id"));
                    params.put("Fbid", pref.getStringPreference(BasicInformationActivity.this, "fb_id"));
                    params.put("Name", "");
                    params.put("Email", "");
                    params.put("Pictureurl", "");
                    params.put("Gender", "");
                    params.put("Device_id", "abc12345");
                    params.put("Device_type", "android");
                    params.put("Dob", dob);
                    params.put("Profession", et_profession.getText().toString());
                    params.put("Interested_in", interestedIn);
                    params.put("Opponent_profession", et_partner_profession.getText().toString());
                    params.put("My_lat", "");
                    params.put("My_long", "");
                    params.put("isServiceProvider", isServiceProvider);


                    System.out.println("params sending : " + params);

                    return params;
                }
            };
            queue.add(postRequest);

        } else {
            showSnackbar("No internet connection!");
        }

    }

    private void registerUserInFirebase(String email, String fb_id) {

        String url = "https://soulmate-190205.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://soulmate-190205.firebaseio.com/users");

                if (s.equals("null")) {
                    reference.child(pref.getStringPreference(BasicInformationActivity.this, "user_id"))
                            .child("name")
                            .setValue(pref.getStringPreference(BasicInformationActivity.this, "fb_name"));

                    reference.child(pref.getStringPreference(BasicInformationActivity.this, "user_id"))
                            .child("pic")
                            .setValue(pref.getStringPreference(BasicInformationActivity.this, "fb_pic"));

                    reference.child(pref.getStringPreference(BasicInformationActivity.this, "user_id"))
                            .child("isonline").setValue("offline");

                    pref.storeStringPreference(BasicInformationActivity.this,
                            "registration_completed", "1");

                    startActivity(new Intent(BasicInformationActivity.this,
                            MapActivity.class));
                    finish();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(pref.getStringPreference(BasicInformationActivity.this, "user_id"))) {
                            reference.child(pref.getStringPreference(BasicInformationActivity.this, "user_id"))
                                    .child("name")
                                    .setValue(pref.getStringPreference(BasicInformationActivity.this, "fb_name"));
                            reference.child(pref.getStringPreference(BasicInformationActivity.this, "user_id"))
                                    .child("pic")
                                    .setValue(pref.getStringPreference(BasicInformationActivity.this, "fb_pic"));

                            reference.child(pref.getStringPreference(BasicInformationActivity.this, "user_id"))
                                    .child("isonline").setValue("offline");

                            Toast.makeText(BasicInformationActivity.this, "registration successful",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // Toast.makeText(Register.this, "username already exists", Toast.LENGTH_LONG).show();
                        }

                        pref.storeStringPreference(BasicInformationActivity.this,
                                "registration_completed", "1");

                        startActivity(new Intent(BasicInformationActivity.this,
                                MapActivity.class));
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                dialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                dialog.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(BasicInformationActivity.this);
        rQueue.add(request);

    }

    private void validateBasic() {

        if (et_name.getText().toString().trim().length() == 0) {
            showSnackbar("Provide name!");
        } else if (et_email.getText().toString().trim().length() == 0) {
            showSnackbar("Provide email!");
        } else if (!emailValidator.validate(et_email.getText().toString().trim())) {
            showSnackbar("Provide valid email!");
        } else if (tv_dob.getText().toString().length() == 0) {
            showSnackbar("Provide date of birth!");
        } else {
            name = et_name.getText().toString();
            email = et_email.getText().toString();
            dob = tv_dob.getText().toString();
            goToLookingSection();
        }

    }

    private void goToLookingSection() {

        ll_basic_wrapper.setVisibility(View.GONE);
        ll_looking_wrapper.setVisibility(View.VISIBLE);

    }

    private void openCalendar() {
        new DatePickerDialog(BasicInformationActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void initView() {

        pref = new Preferences(BasicInformationActivity.this);
        dialog = new ProgressDialog(BasicInformationActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_profession = (EditText) findViewById(R.id.et_profession);
        tv_dob = (TextView) findViewById(R.id.tv_dob);
        btn_basic = (Button) findViewById(R.id.btn_basic);
        ll_basic_wrapper = (LinearLayout) findViewById(R.id.ll_basic_wrapper);
        ll_looking_wrapper = (LinearLayout) findViewById(R.id.ll_looking_wrapper);
        et_partner_profession = (EditText) findViewById(R.id.et_partner_profession);
        rg_looking_for = (RadioGroup) findViewById(R.id.rg_looking_for);
        rg_service_provider = (RadioGroup) findViewById(R.id.rg_service_provider);

        rb_men = (RadioButton) findViewById(R.id.rb_men);
        rb_women = (RadioButton) findViewById(R.id.rb_women);
        rb_both = (RadioButton) findViewById(R.id.rb_both);
        rb_yes = (RadioButton) findViewById(R.id.rb_yes);
        rb_no = (RadioButton) findViewById(R.id.rb_no);

        btn_looking = (Button) findViewById(R.id.btn_looking);
        myCalendar = Calendar.getInstance();
        emailValidator = new EmailValidator();

        et_email.setText(pref.getStringPreference(BasicInformationActivity.this, "fb_email"));
        et_name.setText(pref.getStringPreference(BasicInformationActivity.this, "fb_name"));


        int selectedRadioButtonID = rg_looking_for.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
        rg_looking_for.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_men) {
                    interestedIn = "men";
                } else if (checkedId == R.id.rb_women) {
                    interestedIn = "women";
                } else if (checkedId == R.id.rb_both) {
                    interestedIn = "both";
                }
            }
        });

        rg_service_provider.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_yes) {
                    isServiceProvider = "true";
                } else if (checkedId == R.id.rb_no) {
                    isServiceProvider = "false";
                }
            }
        });

        ll_basic_wrapper.setVisibility(View.VISIBLE);

        int mYear = myCalendar.get(Calendar.YEAR);
        int mMonth = myCalendar.get(Calendar.MONTH);
        int mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

        dd = new DatePickerDialog(this, date, mYear, mMonth, mDay);

        dd.getDatePicker().setMaxDate(myCalendar.getTimeInMillis());

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tv_dob.setText(sdf.format(myCalendar.getTime()));
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
