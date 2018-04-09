package com.rtstl.soulmate4u;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by RTSTL17 on 05-12-2017.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("on receive called " + intent.getExtras());


        Bundle intentExtras = intent.getExtras();
        try {

            if (intentExtras != null) {
                System.out.println("receiver if");

                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                String smsMessageStr = "";
                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                    String smsBody = smsMessage.getMessageBody().toString();
                    String address = smsMessage.getOriginatingAddress();

                    smsMessageStr += "SMS From: " + address + "\n";
                    smsMessageStr += smsBody + "\n";

                    String otpText = smsBody.substring(13, 17);

                    OtpVerificationActivity inst = OtpVerificationActivity.instance();
                    System.out.println("otp extracted : " + otpText);
                    inst.setOTP(otpText);

                }
                //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            } else {
                System.out.println("receiver else");
            }

        } catch (Exception e) {
        }
    }


}
