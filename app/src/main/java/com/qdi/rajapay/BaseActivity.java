package com.qdi.rajapay;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.model.enums.TransactionStatus;
import com.qdi.rajapay.utils.PhoneKeyboard;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

public class BaseActivity extends AppCompatActivity {
    protected ImageView toolbar_back, show_password;
    protected TextView toolbar_title;
    protected Toolbar toolbar_layout;
    protected LinearLayout add_ons_layout;
    public CoordinatorLayout layout;

    protected TextView custom_keyboard_1,custom_keyboard_2,custom_keyboard_3,custom_keyboard_4,
            custom_keyboard_5,custom_keyboard_6,custom_keyboard_7,custom_keyboard_8,custom_keyboard_9,
            custom_keyboard_0,custom_keyboard_c,custom_keyboard_comma,custom_keyboard_next,custom_keyboard_backspace;

    public SharedPreferences user_SP;
    public SharedPreferences.Editor user_edit_SP;

    public RequestQueue requestQueue;
    public DefaultRetryPolicy defaultRetryPolicy = new DefaultRetryPolicy(30000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    public DefaultRetryPolicy paymentRetryPolicy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0,  DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public float y1,y2;
    public boolean swapped = false, is_show_password = false, is_system_changed = false;
    public int MIN_DISTANCE = 150, CAMERA_INTENT = 1001, GALLERY_INTENT = 1002, CONTACT_INTENT = 1003, maximum_id_card_no_length = 16;;
    public int delay_time_for_alert = 1000;
    public int delay_time_for_print = 8000;
    public Uri camera_uri, image_uri;
    public ArrayList<ImageView> add_ons_view_array = new ArrayList<>();

    public DecimalFormat formatter;
    public DecimalFormatSymbols formatSymbols;

    public ClipboardManager clipboard;
    public ClipData clip;

    public PleaseWaitModal please_wait_modal;
    public String BASE_URL = BuildConfig.SERVER_URL,
            url = "", android_id = "", ip_address = "";
    public static final String TAG = "Rajapay";
    public Context context;

    /**
     * @author : Jesslyn
     * @note : Create shimmer effect
     */
    // <code>
    protected ShimmerFrameLayout mShimmerLayout;
    protected boolean isShimmerOn = false;

    protected void shimmerInit(){
        mShimmerLayout = findViewById(R.id.shimmer_view_container);
        isShimmerOn = true;
    }

    protected void shimmerStart(){
        if(isShimmerOn){
            mShimmerLayout.setVisibility(View.VISIBLE);
            mShimmerLayout.startShimmerAnimation();
        }
    }

    protected boolean isShimmerVisible(){
        return mShimmerLayout.getVisibility() == View.VISIBLE;
    }

    protected void shimmerStop(){
        if(isShimmerOn && isShimmerVisible()){
            mShimmerLayout.setVisibility(View.GONE);
            mShimmerLayout.stopShimmerAnimation();
        }
    }
    // </code>

    /**
     * @author Jesslyn
     * @note add new function for getPhoneNo
     */
    // <code>
    public String parsePhoneNo(String phoneNo){
        if(phoneNo.contains("+62")){
            return phoneNo.replace("+62", "0");
        }else if(phoneNo.charAt(0) != '0'){
            return "0" + phoneNo;
        }
        return phoneNo;
    }

    public String getPhoneNo(EditText etPhoneNo){
        return parsePhoneNo(etPhoneNo.getText().toString());
    }
    // </code>

    /**
     * @author Jesslyn
     * @note add base function to handle product is active and is not problem
     * @param isActive
     * @param isProblem
     * @return true if isActive and not in problem state, else return false
     */
    // <code>
    public boolean isActiveNotProblem(boolean isActive, boolean isProblem){
        if(isActive){
            if(isProblem){
                show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                return false;
            }else{
                return true;
            }
        }else{
            show_error_message(layout,getResources().getString(R.string.inactive_click_label));
            return false;
        }
    }
    // </code>

    /**
     * @author Jesslyn
     * @note Add block of code for phone layout keyboard
     */
    // <code>
    protected PhoneKeyboard pk;
    protected InputConnection ic;
    protected LinearLayout keyboard;
    boolean numpadKeyboard = false;

    protected interface NumpadKeyboardSubmit {
        void onSubmit();
    }

    @Override
    public void onBackPressed(){
        if(numpadKeyboard){
            if(keyboard.getVisibility() == View.GONE)
                super.onBackPressed();
            else
                keyboard.setVisibility(View.GONE);
        } else{
            super.onBackPressed();
        }
    }

    protected void initNumpadKeyboard(final EditText phone_no, final NumpadKeyboardSubmit callBack){
        keyboard = findViewById(R.id.keyboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        pk = findViewById(R.id.phone_keyboard);
        ic = phone_no.onCreateInputConnection(new EditorInfo());
        pk.setInputConnection(ic);

        phone_no.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
                        android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if(view.equals(phone_no) && keyboard.getVisibility() == View.GONE) {
                    phone_no.requestFocus();
                    keyboard.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        phone_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    keyboard.setVisibility(View.GONE);

                    callBack.onSubmit();
                }
                return handled;
            }
        });

        numpadKeyboard = true;
    }
    // </code>

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * @author Dinda
         * @note Case CICD 7882 - set portrait mode
         */
        // <code>
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // </code>

        /**
         * @author Jesslyn
         * @note add new variable for application context
         */
        // <code>
        context = getApplicationContext();
        // </code>

        user_SP = getSharedPreferences("data",MODE_PRIVATE);
        user_edit_SP = user_SP.edit();

        formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator(',');
        formatSymbols.setGroupingSeparator('.');
        formatter = new DecimalFormat("#,###,###",formatSymbols);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        please_wait_modal = new PleaseWaitModal();

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ip_address = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, 1);
        }
    }

    /**
     * @author Jesslyn
     * @note 0721531311-179 E05 New user required to change PIN at the first time. New feature would be enhanced at 4.10 - 4.20
     *       this function intended for using BaseActivity class with minimal initialization
     *       this function called by FirebaseInstanceService::verifiedFCMToken
     */
    // <code>
    public void minimalInit(Context ctx){
        this.context = ctx;
        user_SP = context.getSharedPreferences("data",MODE_PRIVATE);
        user_edit_SP = user_SP.edit();

        requestQueue = Volley.newRequestQueue(context);
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    // </code>

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static int getStatusColor(TransactionStatus status){
        int statusColor = R.color.process;
        if(status == TransactionStatus.SUCCESS){
            statusColor = R.color.success;
        }else if(status == TransactionStatus.CANCEL) {
            statusColor = R.color.cancel;
        }else if(status == TransactionStatus.WAITING){
            statusColor = R.color.waiting;
        }else if(status == TransactionStatus.FAILED){
            statusColor = R.color.failed;
        }else if(status == TransactionStatus.REFUND){
            statusColor = R.color.refund;
        }else if(status == TransactionStatus.EXPIRED){
            statusColor = R.color.expired;
        }else if(status == TransactionStatus.PROCESS){
            statusColor = R.color.process;
        /**
         * @author Jesslyn
         * @note Add new status for ManualAdvicePrepaid
         */
        // <code>
        }else if(status == TransactionStatus.MANUAL)
            statusColor = R.color.purple;
        // </code>

        return statusColor;
    }

    /**
     * @author : jesslyn
     * @notes : 1. important part, required to update. please use this code
     *          2. update code with check string length
     *          3. using short-circuit breaker
     *          4. rename string to str
     * @updated-date : 30.10.2020
     * <code>
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0 || str.trim().equals("") || str.trim().equalsIgnoreCase("null");
    }
    // <code>

    /**
     * @author Dinda
     * @note using pass-by-reference to setup value. please use this function to handle usedRefCde paramter
     * @param jsonObject
     * @throws JSONException
     */
    // <code>
    public void setUserRefCde(JSONObject jsonObject) throws JSONException{
        JSONObject user_data = new JSONObject(user_SP.getString("user",""));
        if(user_data != null && user_data.has("usedRefCde") && !isNullOrEmpty(user_data.getString("usedRefCde")))
            jsonObject.put("usedRefCde", user_data.getString("usedRefCde"));
    }
    // </code>

    /**
     * @authors : Jesslyn
     * @notes : change protected function to public
     */
    // <code>
    public static String dayToBahasa(String day){
        day = day.replace("Monday", "Senin");
        day = day.replace("Tuesday", "Selasa");
        day = day.replace("Wednesday", "Rabu");
        day = day.replace("Thursday", "Kamis");
        day = day.replace("Friday", "Jumat");
        day = day.replace("Saturday", "Sabtu");
        day = day.replace("Sunday", "Minggu");

        return day;
    }

    public static String shortDayToBahasa(String day){
        day = day.replace("Mon", "Sen");
        day = day.replace("Tue", "Sel");
        day = day.replace("Wed", "Rab");
        day = day.replace("Thu", "Kam");
        day = day.replace("Fri", "Jum");
        day = day.replace("Sat", "Sab");
        day = day.replace("Sun", "Min");

        return day;
    }

    public static String monthToBahasa(String month){
        month = month.replace("January", "Januari");
        month = month.replace("February", "Februari");
        month = month.replace("March", "Maret");
        month = month.replace("April", "April");
        month = month.replace("May", "Mei");
        month = month.replace("June", "Juni");
        month = month.replace("July", "Juli");
        month = month.replace("August", "Agustus");
        month = month.replace("September", "September");
        month = month.replace("October", "Oktober");
        month = month.replace("November", "Nopember");
        month = month.replace("December", "Desember");

        return month;
    }
    // </code>

    /**
     * @author : Jesslyn
     * @note : check intent exist or not
     * @param intent
     * @param key
     * @return object, please cast this object to object whatever you want. return null if it doesn't exist
     */
    // <code>
    protected Object isExtras(Intent intent, String key){
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey(key)){
                return extras.get(key);
            }
        }
        return null;
    }
    // </code>

    public void showMessage(Intent intent){
        if(intent.hasExtra("data")){
            try{
                JSONObject data = new JSONObject(intent.getStringExtra("data"));

                String dMsg = data.getString("dMsg");
                displaySnackBar(dMsg);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * @author Liao Mei
     * @note add function redirect to main with message
     *       message will be shown as snackbar
     * @param ctx
     * @param message
     * @throws JSONException
     */
    // <code>
    public void redirectToMain(Context ctx, String message) throws JSONException{
        user_edit_SP.putString("bottom_main_menu","home");
        user_edit_SP.commit();

        Intent intent = new Intent(ctx, MainActivity.class);
        JSONObject data = new JSONObject();
        data.put("dMsg", message);
        intent.putExtra("data", data.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        finish();
        startActivity(intent);
    }
    // </code>

    protected void on_typed_referal_otp(int type, String message, ArrayList<EditText> arr_edittext) {
        EditText selected_edittext = arr_edittext.get(0);
        boolean flag = false;
        if(!message.equals("")) {
            if(type < arr_edittext.size()) {
                for (int x = 1; x < arr_edittext.size(); x++) {
                    if (type == x) {
                        selected_edittext = arr_edittext.get(x);
                        flag = true;
                        break;
                    }
                }
            }
            else{
                selected_edittext = arr_edittext.get(type - 1);
                flag = true;
            }
        }
        else{
            for(int x = 2; x <= arr_edittext.size(); x++){
                if(type == x) {
                    selected_edittext = arr_edittext.get(x - 2);
                    flag = true;
                    break;
                }
            }
        }
        if(flag) {
            selected_edittext.requestFocus();
            selected_edittext.setSelection(selected_edittext.getText().length());
        }
    }

    public void consume_api(JsonObjectRequest request){
        if(request.getBody() != null)
            Log.i("VOLLEY", "Request body: " + new String(request.getBody()));

        request.setRetryPolicy(defaultRetryPolicy);
        requestQueue.add(request);
    }

    /**
     * @author Jesslyn
     * @note add new function for handling payment retry policy (postpaidPayment return code 401 if responceCode from bosbiller other than 00)
     * @param request
     * @param retryPolicy
     */
    // <code>
    public void consume_api(JsonObjectRequest request, DefaultRetryPolicy retryPolicy){
        if(request.getBody() != null)
            Log.i("VOLLEY", "Payment Request body: " + new String(request.getBody()));

        request.setShouldCache(false);
        request.setRetryPolicy(retryPolicy);

        requestQueue.add(request);
    }
    // </code>

    public void show_error_message(View view, String message){
        Snackbar.make(view, message, LENGTH_LONG).show();
    }

    public void show_error_message(EditText et, String message){
        et.setError(message);
    }

    public void show_wait_modal(){
        try{
            FragmentManager fm = getSupportFragmentManager();
            Fragment oldFragment = fm.findFragmentByTag("wait_modal");

            if(oldFragment != null && oldFragment.isAdded())
                return;

            if(oldFragment == null && !please_wait_modal.isAdded() && !please_wait_modal.isVisible()){
                fm.executePendingTransactions();
                please_wait_modal.show(fm,"wait_modal");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dismiss_wait_modal(){
        if(please_wait_modal.isVisible())
            please_wait_modal.dismiss();
    }

    protected void init_toolbar(){
        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_layout = findViewById(R.id.toolbar_layout);
        add_ons_layout = findViewById(R.id.add_ons_layout);
        layout = findViewById(R.id.layout);

        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public String getErrorType(VolleyError error) throws UnsupportedEncodingException{
        String message="";
        if(error.networkResponse == null){
            if (error instanceof NetworkError)
                message = "Koneksi bermasalah, silahkan coba lagi";
            else if (error instanceof ServerError)
                message = "Terjadi kesalahan pada server, silahkan coba lagi";
            else if (error instanceof AuthFailureError)
                message = "Terdapat permasalahan pada otentikasi, coba lagi atau hubungi CS";
            else if (error instanceof ParseError)
                message = "Masalah parsing, coba lagi atau hubungi CS";
            else if (error instanceof NoConnectionError)
                message = "Tidak ada jaringan, periksa konektivitas jaringan anda";
            else if (error instanceof TimeoutError)
                message = "Peladen tidak dapat dijangkau, coba lagi atau hubungi CS";
            else if(error instanceof  ServerError)
                message = "Peladen ada masalah, hubungi CS";
            else if(error instanceof ClientError)
                message = "Terjadi masalah pada aplikasi anda, silahkan coba lagi atau install ulang";
            else
                message = "Terjadi masalah pada sistem, hubungi CS";
        }else{
            if(error.networkResponse != null){
                switch(error.networkResponse.statusCode){
                    case 429:
                        message = "Terlalu banyak permintaan dari perangkat anda, silahkan coba lagi nanti";
                        break;
                    default:
                        // all unhandled error message would be this message
                        // message = "Terjadi masalah pada sistem, hubungi CS";
                }
            }
        }

        return message;
    }

    public String error_handling(VolleyError error, View layout, String context) throws UnsupportedEncodingException, JSONException {
        dismiss_wait_modal();

        String message = getErrorType(error);
        if(message.isEmpty()){
            String response_data = new String(error.networkResponse.data, "UTF-8");
            JSONObject data = new JSONObject(response_data);
            if(data.has("response")) {
                JSONObject data_response = data.getJSONObject("response");
                if(data_response.has("code")){
                    message = getErr(context, data_response.getInt("code"));
                }else if (data_response.has("message")){
                    message = data_response.getString("message");
                    Object obj = new JSONTokener(message).nextValue();

                    /**
                     * @updated-by : jesslyn
                     * @notes : make error message from API human readable
                     */
                    if(obj instanceof JSONObject){
                        message = "";

                        JSONObject json = (JSONObject) obj;

                        Iterator<String> keys= json.keys();
                        while (keys.hasNext())
                        {
                            String keyValue = (String)keys.next();
                            Object obj2 = json.get(keyValue);

                            if(obj2 instanceof JSONArray){
                                JSONArray jsonArray = (JSONArray) obj2;
                                for(int i = 0; i < jsonArray.length(); i++){
                                    message += jsonArray.get(i) + " ";
                                }
                            }else{
                                message += json.getString(keyValue) + " ";
                            }
                        }
                    }
                }
            }
            else if(error.networkResponse.statusCode == 413){
                message = "HTTP code: 413\nImage size too big";
            }
        }
        Snackbar snackbar=Snackbar.make(layout,message,Snackbar.LENGTH_LONG);
        snackbar.show();

        return message;
    }

     public String error_handling(VolleyError error, View layout) throws UnsupportedEncodingException, JSONException {
        dismiss_wait_modal();

        String message = getErrorType(error);
        if(message.isEmpty()) {
            String response_data = new String(error.networkResponse.data, "UTF-8");
            JSONObject data = new JSONObject(response_data);
            if (data.has("response")) {
                JSONObject data_response = data.getJSONObject("response");
                if (data_response.has("message")) {
                    message = data_response.getString("message");
                    Object obj = new JSONTokener(message).nextValue();

                    /**
                     * @updated-by : jesslyn
                     * @notes : make error message from API human readable
                     */
                    if (obj instanceof JSONObject) {
                        message = "";

                        JSONObject json = (JSONObject) obj;

                        Iterator<String> keys = json.keys();
                        while (keys.hasNext()) {
                            String keyValue = (String) keys.next();
                            Object obj2 = json.get(keyValue);

                            if (obj2 instanceof JSONArray) {
                                JSONArray jsonArray = (JSONArray) obj2;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    message += jsonArray.get(i) + " ";
                                }
                            } else {
                                message += json.getString(keyValue) + " ";
                            }
                        }
                    }
                }
            } else if (error.networkResponse.statusCode == 413) {
                message = "HTTP code: 413\nImage size too big";
            }
        }
        Snackbar snackbar=Snackbar.make(layout,message,Snackbar.LENGTH_LONG);
        snackbar.show();

        return message;
    }

    /**
     * @auhtor Eliza Sutantya
     * @patch FR19022
     * @notes change method from protected to public due to be used at CouponListFragment
     *
     * @param message
     */
    // <code>
    public void displaySnackBar(String message){
        // </code>
        /**
         * @author Dinda
         * @note check if layout == null, possible for activity without toolbar
         */
        // <code>
        if(layout == null){
            layout = findViewById(R.id.layout);
        }
        // </code>
        final Snackbar snackbar = Snackbar
                .make(layout, message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // DO Nothing
                        // @see https://stackoverflow.com/a/30729394/1533670
                    }
                });

        snackbar.show();
    }

    public void displayToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * @author : Jesslyn
     * @note : 1. change getRes function to getStr
     *         2. add getDimen function
     *         3. add getInt function
     *         4. setDrawable function
     *         5. getDrawable function
     */

    // <code>
    public String getStr(int id){
        return getResources().getString(id);
    }

    public Drawable getDrw(int image){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(image, getApplicationContext().getTheme());
        } else {
            return getResources().getDrawable(image);
        }
    }

    public void setDrawable(ImageView imageView, int image){
        imageView.setImageDrawable(getDrw(image));
    }

    public int getDimen(int id){
        return (int) getResources().getDimension(id);
    }

    public int getInt(int id){
        return getResources().getInteger(id);
    }
    // </code>

    protected String getErr(String context, int code) throws JSONException{
        switch (code){
            case 404 :
                return context + " " + "tidak ditemukan, silahkan coba lagi.";
            case 400:
                return context + " " + "kadaluarsa, silahkan kirim ulang " + context;
            default:
                return "terjadi kesalahan pada " + context;
        }
    }

    protected boolean isFileExists(String contentUri){
        File file = new File(URI.create(contentUri).getPath());
        if (file.exists()) {
            return true;
        }else{
            Uri uri = Uri.parse(contentUri);
            ContentResolver cr = getContentResolver();
            String mimeType = cr.getType(uri);

            if(mimeType != null) {
                return true;
            }
        }

        return false;
    }

    protected String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    protected boolean validateDataType(Uri uri){
        String ext = getMimeType(this, uri);

        if(ext.contains("jpeg") || ext.contains("jpg") || ext.contains("png"))
            return true;
        return false;
    }


    protected void init_toolbar(String title_str){
        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_layout = findViewById(R.id.toolbar_layout);
        add_ons_layout = findViewById(R.id.add_ons_layout);
        layout = findViewById(R.id.layout);

        toolbar_title.setText(title_str);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * @author : Jesslyn, Credit to Cherry
     * @note : this function intended for short hand if there were any activity required price tag on the right-top activity.
     *         for anyone wanted to change this function, please make sure add_on_toolbar setup first before set click listener
     * @param ctx
     * @param cls
     * @throws JSONException
     */
    // <code>
    protected void preparePriceTag(final Context ctx, final Class<?> cls) throws JSONException {
        ArrayList<JSONObject> add_ons_array = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image",R.drawable.ic_icon_taghdpi);
        add_ons_array.add(jsonObject);

        add_ons_toolbar(add_ons_layout,add_ons_array);
        add_ons_view_array.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, cls));
            }
        });
    }
    // </code>

    protected void add_ons_toolbar(LinearLayout linearLayout, ArrayList<JSONObject> array) throws JSONException {
        for(int x=0;x<array.size();x++) {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.selectableItemBackground,typedValue,true);

            ImageView image = new ImageView(this);
            image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            image.setImageDrawable(getResources().getDrawable(array.get(x).getInt("image")));
            image.setBackgroundResource(typedValue.resourceId);

            /**
             * @author : Jesslyn
             * @note : set width and height for image (price tag), Have check the function usage, this function only intended for set price tag at the left-top of activity
             */
            // <code>
            image.getLayoutParams().height = getDimen(R.dimen.widget_icon_top_height);
            image.getLayoutParams().width = getDimen(R.dimen.widget_icon_top_width);
            // </code>

            add_ons_view_array.add(image);
            linearLayout.addView(image);
        }
    }

    public String uri_to_string(Uri uri) throws FileNotFoundException {
        InputStream imageStream = getContentResolver().openInputStream(uri);
        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String resizeBase64Image(Uri uri) throws FileNotFoundException {
        String base64image = uri_to_string(uri);
        byte [] encodeByte=Base64.decode(base64image.getBytes(),Base64.DEFAULT);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);


        if(image.getHeight() <= 400 && image.getWidth() <= 400){
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, 500, 500, false);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.e(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.e(tag, content);
        }
    }

    public void edittext_currency(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(is_system_changed) {
                    is_system_changed = false;
                    return;
                }
                if(!s.toString().equals("")) {
                    is_system_changed = true;
                    editText.setText(formatter.format(Double.valueOf(s.toString().replace(".", ""))));
                    editText.setSelection(editText.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * @author Jesslyn
     * @note function to convert from string to local idr
     * @param currency
     * @return
     */
    // <code>
    public String toLocalIdr(String currency){
        return "Rp. " + formatter.format(Double.valueOf(currency.replace(".", "")));
    }

    protected void init_show_password(final EditText password){
        show_password = findViewById(R.id.show_password);

        show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_show_password)
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                is_show_password = !is_show_password;
            }
        });
    }

    public void show_qr_code(ImageView imageView, String text) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        imageView.setImageBitmap(bitmap);
    }

    public Bitmap get_qr_bitmap(String text) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        return bitmap;
    }

    public String translate_status(String status){
        String message = "";
        if(status.equals("S"))
            message = "Berhasil";
        else if(status.equals("W"))
            message = "Menunggu";
        else if(status.equals("C"))
            message = "Batal";
        else if(status.equals("P"))
            message = "Proses";
        else if(status.equals("F"))
            message = "Gagal";
        else if(status.equals("E"))
            message = "Expired";
        else if(status.equals("R"))
            message = "Refund";
        else if(status.equals("M"))
            message = "Tindakan";
        return message;
    }

    public String translate_ticket_status(String status){
        String message = "";
        if(status.equals("S"))
            message = "Selesai";
        else if(status.equals("W"))
            message = "Sedang di Proses";
        else if(status.equals("C"))
            message = "Menunggu Balasan";
        else if(status.equals("P"))
            message = "Proses";
        else if(status.equals("F"))
            message = "Gagal";
        else if(status.equals("E"))
            message = "Expired";
        else if(status.equals("R"))
            message = "Refund";
        else if(status.equals("M"))
            message = "Tindakan";
        return message;
    }

    /**
     * @author Jesslyn
     * @note change function to use getStatusColor only
     * @param status_textview
     * @param status
     * @param context
     */
    // <code>
    public void setColorFromDisplayStr(TextView status_textview, String status, Context context){
        TransactionStatus ts = TransactionStatus.fromDisplayString(status);
        status_textview.setBackgroundColor(context.getResources().getColor(getStatusColor(ts)));
    }
    // </code>

    /**
     * @author Liao Mei
     * @note set color based on status code
     * @param status_textview
     * @param status
     * @param context
     */
    // <code>
    public void setColor(TextView status_textview, String status, Context context){
        TransactionStatus ts = TransactionStatus.fromString(status);
        status_textview.setBackgroundColor(context.getResources().getColor(getStatusColor(ts)));
    }
    // </code>

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected void pick_contact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent,CONTACT_INTENT);
    }

    protected void manage_custom_keyboard(final EditText editText, final Class next_activity){
        custom_keyboard_next = findViewById(R.id.custom_keyboard_next);
        custom_keyboard_1 = findViewById(R.id.custom_keyboard_1);
        custom_keyboard_2 = findViewById(R.id.custom_keyboard_2);
        custom_keyboard_3 = findViewById(R.id.custom_keyboard_3);
        custom_keyboard_4 = findViewById(R.id.custom_keyboard_4);
        custom_keyboard_5 = findViewById(R.id.custom_keyboard_5);
        custom_keyboard_6 = findViewById(R.id.custom_keyboard_6);
        custom_keyboard_7 = findViewById(R.id.custom_keyboard_7);
        custom_keyboard_8 = findViewById(R.id.custom_keyboard_8);
        custom_keyboard_9 = findViewById(R.id.custom_keyboard_9);
        custom_keyboard_0 = findViewById(R.id.custom_keyboard_0);
        custom_keyboard_c = findViewById(R.id.custom_keyboard_c);
        custom_keyboard_comma = findViewById(R.id.custom_keyboard_comma);
        custom_keyboard_backspace = findViewById(R.id.custom_keyboard_backspace);

        ArrayList<TextView> array = new ArrayList<>();
        array.add(custom_keyboard_0);
        array.add(custom_keyboard_1);
        array.add(custom_keyboard_2);
        array.add(custom_keyboard_3);
        array.add(custom_keyboard_4);
        array.add(custom_keyboard_5);
        array.add(custom_keyboard_6);
        array.add(custom_keyboard_7);
        array.add(custom_keyboard_8);
        array.add(custom_keyboard_9);
        array.add(custom_keyboard_c);
        array.add(custom_keyboard_comma);
        array.add(custom_keyboard_backspace);

        final ArrayList<String> array_data = new ArrayList<>();
        array_data.add("0");
        array_data.add("1");
        array_data.add("2");
        array_data.add("3");
        array_data.add("4");
        array_data.add("5");
        array_data.add("6");
        array_data.add("7");
        array_data.add("8");
        array_data.add("9");
        array_data.add("c");
        array_data.add(",");
        array_data.add("backspace");

        for(int x=0;x<array.size();x++){
            final int final_x = x;
            array.get(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = editText.getText().toString();
                    if(array_data.get(final_x).equals("c"))
                        str = "";
                    else if(array_data.get(final_x).equals("backspace"))
                        str += str.substring(str.length(),1);
                    else
                        str += array_data.get(final_x);
                    editText.setText(str);
                }
            });
        }
    }

    protected ArrayList<JSONObject> add_condition_password() throws JSONException {
        ArrayList<JSONObject> arr = new ArrayList<>();
        arr.add(new JSONObject("{\"text\":\""+getResources().getString(R.string.condition_password_1)+"\",\"is_fulfill_condition\":false}"));
        arr.add(new JSONObject("{\"text\":\""+getResources().getString(R.string.condition_password_2)+"\",\"is_fulfill_condition\":false}"));
        arr.add(new JSONObject("{\"text\":\""+getResources().getString(R.string.condition_password_3)+"\",\"is_fulfill_condition\":false}"));
        arr.add(new JSONObject("{\"text\":\""+getResources().getString(R.string.condition_password_4)+"\",\"is_fulfill_condition\":false}"));

        return arr;
    }

    protected void check_condition(ArrayList<JSONObject> array, CharSequence charSequence) throws JSONException {
        for(int x=0;x<array.size();x++)
            array.get(x).put("is_fulfill_condition",false);

        if(charSequence.toString().length() >= 8)
            array.get(0).put("is_fulfill_condition",true);

        if(charSequence.toString().length() > 0) {
            for(int x=0;x<charSequence.length();x++) {
                if (String.valueOf(charSequence.charAt(x)).matches("-?\\d+(\\.\\d+)?")) {
                    array.get(3).put("is_fulfill_condition", true);
                    break;
                }
            }
        }

        if(charSequence.toString().length() > 0) {
            for(int x=0;x<charSequence.length();x++) {
                if (String.valueOf(charSequence.charAt(x)).matches(".*[A-Z].*"))
                    array.get(1).put("is_fulfill_condition", true);
                else if (String.valueOf(charSequence.charAt(x)).matches(".*[a-z].*"))
                    array.get(2).put("is_fulfill_condition", true);
            }
        }
    }

    public void execute_media_intent(Intent i,String type){
        startActivityForResult(i, type == "gallery" ? GALLERY_INTENT : CAMERA_INTENT);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();

        if (swapped) {
            swapped = false;
            return super.dispatchTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                y2 = event.getY();
                float deltaX = y2 - y1;
                if (Math.abs(deltaX) < MIN_DISTANCE) {
                    if(v != null &&
                            (v instanceof EditText || v instanceof TextInputEditText || v instanceof AutoCompleteTextView) &&
                            !v.getClass().getName().startsWith("android.webkit.")){
                        int scrcoords[] = new int[2];
                        v.getLocationOnScreen(scrcoords);
                        float x = event.getRawX() + v.getLeft() - scrcoords[0];
                        float y = event.getRawY() + v.getTop() - scrcoords[1];

                        if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                            hideSoftKeyboard(this);
                            v.clearFocus();
                        }
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public JSONObject getBaseAuth(){
        JSONObject param = new JSONObject();

        try{
            param.put("idLogin",user_SP.getString("idLogin",""));
            param.put("idUser",user_SP.getString("idUser",""));
            param.put("token",user_SP.getString("token",""));
        }catch(JSONException e){
            e.printStackTrace();
        }

        return param;
    }

    public HashMap<String, String> getBaseAuth(HashMap<String, String> headers){
        headers.put("idLogin",user_SP.getString("idLogin",""));
        headers.put("idUser",user_SP.getString("idUser",""));
        headers.put("token",user_SP.getString("token",""));

        return headers;
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void onAPIResponseFailure(VolleyError error) {
        dismiss_wait_modal();
        try {
            error_handling(error, layout);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
            show_error_message(layout, e.getLocalizedMessage());
        }
    }

    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new AsteriskPasswordTransformationMethod.PasswordCharSequence(source);
        }
        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }
            public char charAt(int index) {
                return '⚫'; // This is the important part
            }
            public int length() {
                return mSource.length(); // Return default
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    };

}
