package com.qdi.rajapay.inbox.news;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.utils.NotificationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.2 Kotak Masuk
 * @screen 4.2.2
 */
public class DetailNotificationActivity extends BaseActivity {
    TextView title,date,detail;

    JSONObject data;
    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes 0911254300-210 W03 News notification update
     */
    // <code>
    boolean calledFromFcm = false;
    String idNews = "";
    // </code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail_notification);

        init_toolbar(getResources().getString(R.string.activity_title_notif_detail));
        try {
            init();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException, ParseException {
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        detail = findViewById(R.id.detail);

        /**
         * @author Eliza Sutantya
         * @patch FR19022
         * @notes 0911254300-210 W05 Ticket notification update
         */
        // <code>
        Intent intent = getIntent();
        if(intent.hasExtra("caller") && intent.getStringExtra("caller").equals(NotificationManager.class.getCanonicalName())){
            calledFromFcm = true;

            if(intent.hasExtra("idNews")){
                idNews = intent.getStringExtra("idNews");
                get_data(idNews);
            }
        }else{
            data = new JSONObject(getIntent().getStringExtra("data"));
            initData();
        }
        // </code>
    }

    private void initData() throws JSONException, ParseException {
        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format_date = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");

        /**
         * @authors : Jesslyn
         * @notes : 1. Fixing design issue
         *          2. Fixing compatibility issue
         *
         * @author Eliza Sutantya
         * @patch FR19022
         * @notes possible error but not sure what happened
         */
        // <code>
        if(data.has("date")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                date.setText(Html.fromHtml(data.getString("date"), Html.FROM_HTML_MODE_COMPACT));
            } else {
                date.setText(Html.fromHtml(data.getString("date")));
            }
        }
        // </code>
        detail.setText(data.getString("content"));
        title.setText(data.getString("title"));
    }

    private void get_data(String idNews)
    {
        show_wait_modal();
        url = BASE_URL+"/mobile/inbox/news";
        Log.d("url",url);

        JSONObject param = getBaseAuth();
        try{
            param.put("idNews", idNews);
        }catch(JSONException e){
            e.printStackTrace();
        }

        Log.d("param", param.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, response -> {
            try {
                JSONObject response_data = response.getJSONObject("response");
                if (!response_data.getString("type").equals("Failed")) {
                    for(int x = 0; x < response_data.getJSONArray("arrNewsRslt").length(); x++){
                        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        // <code>
                        SimpleDateFormat format_date = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                        // </code>
                        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
                        JSONObject data_news = response_data.getJSONArray("arrNewsRslt").getJSONObject(x);

                        data_news.put("title",data_news.getString("subject"));
                        /**
                         * @authors : Jesslyn
                         * @notes : Issue 27 - JIRA
                         */
                        // <code>
                        data_news.put("date", BaseActivity.monthToBahasa(BaseActivity.dayToBahasa( format_date.format(parse_date.parse(data_news.getString("dateNews"))) ))+
                                " (<b>" + format_time.format(parse_date.parse(data_news.getString("dateNews"))) + " WIB</b>)");
                        // </code>
                        data_news.put("is_today", DateUtils.isToday(parse_date.parse(data_news.getString("dateNews")).getTime()));
                        data = data_news;
                        // api only return 1 record (sending api by using idTicket)
                        break;
                    }

                    if(response_data.getJSONArray("arrNewsRslt").length() > 0)
                        initData();
                    // else use default text data from layout
                } else {
                    show_error_message(layout,response_data.getString("message"));
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            } finally {
                dismiss_wait_modal();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    error_handling(error, layout);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
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

        consume_api(jsonObjectRequest);
    }
}