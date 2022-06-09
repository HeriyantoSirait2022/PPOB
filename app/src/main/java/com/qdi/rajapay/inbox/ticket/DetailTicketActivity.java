package com.qdi.rajapay.inbox.ticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.ChooseMediaPickerModal;
import com.qdi.rajapay.R;
import com.qdi.rajapay.utils.NotificationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 4.2 Kotak Masuk
 * @screen 4.2.4
 */
public class DetailTicketActivity extends BaseActivity {
    TextView title,date,status,id;
    EditText chat;
    RecyclerView list;
    ImageView attach;
    LinearLayout send;

    JSONObject data;
    Uri image_uri;

    DetailTicketAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes 0911254300-210 W05 Ticket notification update
     */
    // <code>
    boolean calledFromFcm = false;
    String idTicket = "";
    // </code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_detail_ticket);

        try {
            init();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseMediaPickerModal modal = new ChooseMediaPickerModal("ticket.jpg");
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        chat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list.scrollToPosition(array.size()-1);
                        }
                    },100);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showSoftKeyboard();
                    chat.requestFocus();
                    if(chat.getText().toString().equals(""))
                        show_error_message(layout,getResources().getString(R.string.detail_ticket_chat_empty));
                    else{
                        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");

                        JSONObject data = new JSONObject();
                        data.put("text",chat.getText().toString());
                        data.put("time",format_time.format(new Date()));
                        data.put("type","sender");
                        data.put("data_type","text");
                        array.add(data);
                        adapter.notifyItemInserted(array.size());

                        send("text");
                        chat.setText("");
                        list.scrollToPosition(array.size()-1);
                    }
                } catch (JSONException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException, ParseException {
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        status = findViewById(R.id.status);
        id = findViewById(R.id.id);
        list = findViewById(R.id.list);
        chat = findViewById(R.id.chat);
        attach = findViewById(R.id.attach);
        send = findViewById(R.id.send);

        /**
         * @author Eliza Sutantya
         * @patch FR19022
         * @notes 0911254300-210 W05 Ticket notification update
         */
        // <code>
        Intent intent = getIntent();
        if(intent.hasExtra("caller") && intent.getStringExtra("caller").equals(NotificationManager.class.getCanonicalName())){
            calledFromFcm = true;

            if(intent.hasExtra("idTicket")){
                idTicket = intent.getStringExtra("idTicket");
                get_data(idTicket);
            }
        }else{
            data = new JSONObject(getIntent().getStringExtra("data"));
            initData();
        }
        // </code>
    }

    private void initData() throws JSONException, ParseException{
        init_toolbar(data.getString("title"));

        title.setText(data.getString("title"));
        date.setText(data.getString("date"));
        status.setText(translate_ticket_status(data.getString("status")));
        /**
         * @author : Jesslyn
         * @notes : Issue JIRA 29
         */
        // <code>
        id.setText(data.getString("id"));
        // </code>
        setColor(status,data.getString("status"),this);

        prepare_data();
        adapter = new DetailTicketAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        list.setLayoutManager(layout_manager);
        list.setAdapter(adapter);

        /**
         * @author Eliza Sutantya
         * @patch FR19022
         * @notes 0911254300-210 W05 Ticket notification update
         * ...... add feature to automatically scroll to bottom chat
         */
        // <code>
        list.post(() -> list.smoothScrollToPosition(array.size() - 1));
        // </code>
    }

    public void get_data(String idTicket) {
        show_wait_modal();

        url = BASE_URL+"/mobile/inbox/ticket";
        JSONObject param = getBaseAuth();

        try {
            param.put("idTicket", idTicket);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // sending api with idTicket will return only one record
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray jsonArray = response_data.getJSONArray("arrTicketRslt");
                        for(int x = 0; x < jsonArray.length(); x++){
                            SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy");
                            SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
                            JSONObject data_ticket = jsonArray.getJSONObject(x);

                            String replace_open_ticket = data_ticket.getString("openTicket")
                                    .replace("T"," ")
                                    .replace("Z","");
                            data_ticket.put("title",data_ticket.getString("subject"));
                            data_ticket.put("date",format_date.format(parse_date.parse(replace_open_ticket))+
                                    " ("+format_time.format(parse_date.parse(replace_open_ticket))+")");

                            /**
                             * @authors : Jesslyn
                             * @notes : JIRA - 28
                             */
                            // <code>
                            data_ticket.put("id","Tiket #"+data_ticket.getString("idTicket"));
                            // </code>
                            /**
                             * @author : Liao Mei
                             * @note : Bug issue at tiket because not status using translate ticket status. Seperate status
                             */
                            // <code>
                            data_ticket.put("status_str", translate_ticket_status(data_ticket.getString("status")));
                            data_ticket.put("status",data_ticket.getString("status"));
                            // </code>

                            data = data_ticket;
                            // api only return 1 record (sending api by using idTicket)
                            break;
                        }
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }

                    initData();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                } finally {
                    dismiss_wait_modal();
                }
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

    private void prepare_data() throws JSONException, ParseException {
        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");

        JSONArray jsonArray = data.getJSONArray("arrConvo");
        for(int x = 0; x < jsonArray.length(); x++){
            String replace_date_time = jsonArray.getJSONObject(x).getString("convoDateTime")
                    .replace("T"," ")
                    .replace("Z","");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text",jsonArray.getJSONObject(x).getString("content"));
            jsonObject.put("time",format_time.format(parse_date.parse(replace_date_time)));
            jsonObject.put("type",jsonArray.getJSONObject(x).getString("idUser").equals(
                    user_SP.getString("idUser","")
            ) ? "sender" : "receiver");
            jsonObject.put("data_type","text");
            array.add(jsonObject);
        }
    }

    private void send_image() throws JSONException, FileNotFoundException {
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");

        JSONObject data = new JSONObject();
        data.put("image",image_uri);
        data.put("time",format_time.format(new Date()));
        data.put("type","sender");
        data.put("data_type","image");
        array.add(data);
        adapter.notifyDataSetChanged();

        send("image");
        list.scrollToPosition(array.size()-1);
    }

    private void send(String type) throws JSONException, FileNotFoundException {
        final String statusCode = "W";
        JSONObject arr = new JSONObject();
        arr.put("idTicket",data.getString("idTicket"));
        if(type == "text")
            arr.put("content",chat.getText().toString());
        else if(type == "image")
            arr.put("images",uri_to_string(image_uri));
        arr.put("status", statusCode);
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/inbox/send-ticket";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        status.setText(translate_ticket_status(statusCode));
                        setColor(status, statusCode,DetailTicketActivity.this);
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(resultCode == RESULT_OK){
                if(requestCode == CAMERA_INTENT){
                    image_uri = camera_uri;
                    send_image();
                }
                else if(requestCode == GALLERY_INTENT){
                    String dataString = data.getDataString();
                    image_uri = Uri.parse(dataString);
                    send_image();
                }
            }
        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}