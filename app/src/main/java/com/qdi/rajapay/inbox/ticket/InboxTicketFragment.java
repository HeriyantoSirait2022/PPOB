package com.qdi.rajapay.inbox.ticket;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.R;
import com.qdi.rajapay.inbox.InboxListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 4.2 Kotak Masuk
 * @screen 4.2.3
 */
public class InboxTicketFragment extends Fragment {
    RecyclerView list;

    InboxListActivity parent;
    InboxTicketAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.inbox_news_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new InboxTicketAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    if(!array.get(position).getString("status").equals("Selesai"))
                        startActivity(new Intent(parent, DetailTicketActivity.class)
                                .putExtra("data",array.get(position).toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);

        parent = (InboxListActivity) getActivity();

        adapter = new InboxTicketAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        list.setLayoutManager(layout_manager);
        list.setAdapter(adapter);

        get_data();
    }

    public void get_data() {
        parent.url = parent.BASE_URL+"/mobile/inbox/ticket";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        array.clear();
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
                             * @authors : JesslynM
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
                            data_ticket.put("status_str",parent.translate_ticket_status(data_ticket.getString("status")));
                            data_ticket.put("status",data_ticket.getString("status"));
                            // </code>

                            array.add(data_ticket);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        parent.show_error_message(parent.layout,response_data.getString("message"));
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parent.error_handling(error, parent.layout);
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

        parent.consume_api(jsonObjectRequest);
    }
}
