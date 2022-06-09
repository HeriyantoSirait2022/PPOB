package com.qdi.rajapay.utils;

import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.model.enums.NotificationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseInstanceService extends FirebaseMessagingService {
    @Override
    public void onNewToken(final String s) {
        super.onNewToken(s);

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                verifiedFCMToken(s);
            }
        };

        Message message = mHandler.obtainMessage();
        message.sendToTarget();
    }

    /**
     * @author Jesslyn
     * @note 0721531311-179 E05 New user required to change PIN at the first time. New feature would be enhanced at 4.10 - 4.20
     */
    // <code>
    public void verifiedFCMToken(String fcmToken){
        final BaseActivity parent = new BaseActivity();
        parent.minimalInit(getApplicationContext());

        // @debug
        /*
        parent.user_edit_SP.putString("idLogin", "UID85L54");
        parent.user_edit_SP.putString("idUser", "UID85");
        parent.user_edit_SP.putString("token", "GWqkgsJgZYepLZyfka2EifBKD9rr8zBMJIkw4j003uUZ7JNmJm8vpEuABMGZ");
        parent.user_edit_SP.commit();
         //*/

        if(!parent.user_SP.contains("idLogin") || !parent.user_SP.contains("idUser") || !parent.user_SP.contains("token")){
            // FCM token first time generated when user first time install application. this action would trigger HomeFragment::verifiedFCMToken
            postRegistration(parent, fcmToken);
        }else if(parent.user_SP.contains("idLogin") && parent.user_SP.contains("idUser") && parent.user_SP.contains("token")){
            // FCM token refreshed when user already logged in on application
            sendRegistrationServer(parent, fcmToken);
        }
    }

    private void postRegistration(final BaseActivity parent, String fcmToken){
        parent.user_edit_SP.putBoolean("fcm_token_status", false);
        parent.user_edit_SP.putString("fcm_token", fcmToken);
        parent.user_edit_SP.commit();
    }

    private void cancelRegistration(final BaseActivity parent){
        parent.user_edit_SP.putBoolean("fcm_token_status", false);
        parent.user_edit_SP.remove("fcm_token");
        parent.user_edit_SP.commit();
    }

    /**
     * @author Jesslyn
     * @note this function intended for update token
     *       this function also called from HomeFragment and this class
     * @param parent Base Activity
     * @param fcmToken newest token
     */
    public void sendRegistrationServer(final BaseActivity parent, final String fcmToken){
        parent.url = parent.BASE_URL+"/mobile/account/fcm-status";

        JSONObject param = parent.getBaseAuth();
        try{
            param.put("fcmToken", fcmToken);
        }catch(Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if(!response_data.getString("type").equals("Failed") &&
                            response_data.getString("description").equals("FCM_UPDATED")){
                        parent.user_edit_SP.putBoolean("fcm_token_status", true);
                        parent.user_edit_SP.putString("fcm_token", fcmToken);

                        // @debug
                        /*
                        parent.user_edit_SP.remove("idLogin");
                        parent.user_edit_SP.remove("idUser");
                        parent.user_edit_SP.remove("token");
                         //*/

                        parent.user_edit_SP.commit();
                    }else{
                        // possible !response_data.getString("type").equals("Failed") && response_data.getString("description").equals("FCM_ERROR") or else
                        // Cancel all registration process, trigger HomeFragment::verifiedFCMToken
                        cancelRegistration(parent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Cancel all registration process, trigger HomeFragment::verifiedFCMToken
                cancelRegistration(parent);

                // for debugging purposes
                Log.e(BaseActivity.TAG, error.toString());

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e(BaseActivity.TAG, String.valueOf(networkResponse.statusCode));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        parent.consume_api(jsonObjectRequest);
    }
    // </code>

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Map<String, String> data = remoteMessage.getData();

                    handleNotification(data);
                }
            };

            Message message = mHandler.obtainMessage();
            message.sendToTarget();
        }
    }

    public boolean createNotificationChannel(){
        try{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationManager mNotificationManager =
                        (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID_01, Constants.CHANNEL_NAME, importance);
                mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean handleNotification(Map<String, String> data){
        try{
            if(createNotificationChannel()){

                final BaseActivity parent = new BaseActivity();
                parent.minimalInit(getApplicationContext());

                NotificationType notifType = NotificationType.fromString(data.get("fcmType"));

                switch(notifType){
                    case TRANSACTION:
                        com.qdi.rajapay.utils.NotificationManager.getInstance(this).intentToTransactionDetail(parent.user_SP,
                                data.get("title"),
                                data.get("body"),
                                data.get("idOrder"),
                                data.get("txnType"));
                        break;
                    case TICKET:
                        com.qdi.rajapay.utils.NotificationManager.getInstance(this).intentToTicketDetail(
                                data.get("title"),
                                data.get("body"),
                                data.get("idTicket")
                        );
                        break;
                    case NEWS:
                        com.qdi.rajapay.utils.NotificationManager.getInstance(this).intentToNewsDetail(
                                data.get("title"),
                                data.get("body"),
                                data.get("idNews")
                        );
                        break;
                }

            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
