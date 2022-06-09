package com.qdi.rajapay.inbox.news;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.inbox.InboxListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InboxNewsFragment extends Fragment {
    RecyclerView list;

    InboxListActivity parent;
    InboxNewsAdapter adapter;
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

        adapter.setOnItemClickListener(new InboxNewsAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(parent,DetailNotificationActivity.class)
                        .putExtra("data",array.get(position).toString()));
            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);

        parent = (InboxListActivity) getActivity();

        adapter = new InboxNewsAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        DividerItemDecoration decoration = new DividerItemDecoration(parent,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setLayoutManager(layout_manager);
        list.setAdapter(adapter);

        get_data();
    }

    private void get_data() {
        parent.url = parent.BASE_URL+"/mobile/inbox/news";
        Log.d("url",parent.url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                            array.add(data_news);
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
