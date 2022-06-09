package com.qdi.rajapay.api;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private final Gson gson;
    private final Type type;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private JSONObject body;
    private final Response.Listener<T> listener;

    /**
     * @author Jesslyn
     * @patch P2301
     * @notes change code to support P2301 API
     */
    // <code>
    // get without deserializer (cannot use body message)
    public GsonRequest(String url, Map<String, String> params, Type type, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(url, params, type, headers, listener, errorListener, null);
    }

    // get with deserializer (cannot use body message)
    public GsonRequest(String url, Map<String, String> params, Type type, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener,
                       JsonDeserializer deserializer) {
        super(Method.GET, url, errorListener);
        this.url = url;
        this.params = params;
        this.type = type;
        this.headers = headers;
        this.listener = listener;
        if(deserializer != null){
            this.gson = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .registerTypeAdapter(type, deserializer).create();
        }else{
            this.gson = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
        }
    }

    // any method without deserializer
    public GsonRequest(int method, String url, Map<String, String> params, Type type, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(method, url, params, type, headers, listener, errorListener, null);
    }

    // any method with deserializer
    public GsonRequest(int method, String url, Map<String, String> params, Type type, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener,
                       JsonDeserializer deserializer) {
        super(method, url, errorListener);
        this.url = url;

        /**
         * @author Jesslyn
         * @see https://stackoverflow.com/a/29841381/1533670
         */
        if(method == Method.POST || method == Method.PUT || method == Method.PATCH){
            this.body = new JSONObject(params);
        }else{
            this.params = params;
        }

        this.type = type;
        this.headers = headers;
        this.listener = listener;

        if(deserializer != null){
            this.gson = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .registerTypeAdapter(type, deserializer).create();
        }else{
            this.gson = new GsonBuilder()
                    .serializeNulls()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
        }

    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return this.body.toString().getBytes();
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
    // </code>


    @Override
    public String getUrl() {

        if(params != null) {
            StringBuilder stringBuilder = new StringBuilder(url);
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (i == 1) {
                    stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                } else {
                    stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                iterator.remove(); // avoids a ConcurrentModificationException
                i++;
            }
            url = stringBuilder.toString();
        }
        return url;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return (Response<T>) Response.success(
                    gson.fromJson(json, type),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}