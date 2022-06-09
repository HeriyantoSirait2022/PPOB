package com.qdi.rajapay.main_menu.prepaid_data;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.R;
import com.qdi.rajapay.payment.ChoosePaymentActivity;
import com.qdi.rajapay.payment.SuccessActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.10 Prabayar Data
 * @screen 4.10.2
 */
public class PrepaidDataInputPhoneNoModal extends BottomSheetDialogFragment {
    // <code>
    TextView title, detail, total_price, termCondition;
    // </code>

    ImageView image;
    Button buy_now;

    PrepaidDataInputPhoneNoActivity parent;
    Double admin_cost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_menu_prepaid_data_modal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<JSONObject> arr = new ArrayList<>();
                JSONObject data = new JSONObject();
                try {
                    /**
                     * @author Jesslyn
                     * @note add detail phone number
                     */
                    // <code>
                    String noHp = parent.getPhoneNo(parent.phone_no);
                    arr.add(new JSONObject("{\"title\":\""+title.getText().toString()+"\",\"price\":"+parent.data_selected.getDouble("price")+",\"detail\":\"" + noHp + "\"}"));
                    // </code>

                    arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+
                            "\",\"price\":"+admin_cost+"}"));

                    data.put("data",parent.data_selected);
                    data.put("data_post",add_data_to_post());
                    data.put("url_post","/mobile/prepaid/data-transaction");
                    data.put("url_pay","/mobile/prepaid/topup-data");
                    data.put("breakdown_price",new JSONArray(arr.toString()));

                    post_main_menu(view, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view) throws JSONException {
        title = view.findViewById(R.id.title);
        detail = view.findViewById(R.id.detail);
        total_price = view.findViewById(R.id.total_price);
        buy_now = view.findViewById(R.id.buy_now);
        image = view.findViewById(R.id.image);

        /**
         * @author : Liao Mei
         * @note : Fixing case 30
         */
        // <code>
        Spanned policy = Html.fromHtml(getString(R.string.main_menu_prepaid_data_modal_notice));
        termCondition = view.findViewById(R.id.termsOfUse);
        termCondition.setText(policy);
        termCondition.setMovementMethod(LinkMovementMethod.getInstance());
        // </code>

        parent = (PrepaidDataInputPhoneNoActivity) getActivity();
        /**
         * @author Jesslyn
         * @note 1. Standardization admin cost calculation
         *       2. change title and detail data
         *       3. set visibility for detail
         *       4. optimize picasso
         */
        // <code>
        admin_cost = parent.data_selected.getDouble("amount") - parent.data_selected.getDouble("vendorAmount");
        title.setText(parent.data_selected.getString("name"));
        if(parent.isNullOrEmpty(parent.data_selected.getString("detail"))){
            detail.setVisibility(View.GONE);
        }else{
            detail.setVisibility(View.VISIBLE);
            detail.setText(parent.data_selected.getString("detail"));
        }
        total_price.setText("Rp. "+parent.formatter.format(parent.data_selected.getDouble("price")+admin_cost));
        Picasso.get()
                .load(parent.data_selected.getString("image_url"))
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .fit().centerInside()
                .into(image);
        // </code>
    }

    private JSONObject add_data_to_post() throws JSONException {
        String phone = parent.phone_no.getText().toString();
        if(phone.charAt(0) != '0')
            phone = "0"+parent.phone_no.getText().toString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noHp",phone);
        jsonObject.put("typeTxn","PREPAIDDATA");
        jsonObject.put("idProduct",parent.selected_provider.getString("idProduct"));
        jsonObject.put("cdeProduct",parent.selected_provider.getString("cdeProduct"));
        jsonObject.put("nameProduct",parent.selected_provider.getString("nameProduct"));
        jsonObject.put("productType","DATA");
        jsonObject.put("productCategory","PREPAID");
        jsonObject.put("image",parent.selected_provider.getString("image"));
        jsonObject.put("idProductDetail",parent.data_selected.getString("idProductDetail"));
        jsonObject.put("codeData",parent.data_selected.getString("codeData"));
        jsonObject.put("nameData",parent.data_selected.getString("nameData"));
        jsonObject.put("detailData",parent.data_selected.getString("detailData"));
        jsonObject.put("vendorAmount",parent.data_selected.has("vendorAmount") ?
                parent.data_selected.getDouble("vendorAmount") : parent.data_selected.getDouble("amount"));
        jsonObject.put("amount",parent.data_selected.getDouble("amount"));
        jsonObject.put("idLogin",parent.user_SP.getString("idLogin",""));
        jsonObject.put("idUser",parent.user_SP.getString("idUser",""));
        jsonObject.put("token",parent.user_SP.getString("token",""));

        /**
         * @author Dinda
         * @note Case CICD 10245 - missing usedRefCde at prepaid/{type}-transaction
         */
        // <code>
        parent.setUserRefCde(jsonObject);
        // </code>

        return jsonObject;
    }

    private void post_main_menu(final View view, final JSONObject data) throws JSONException {
        if(data.has("data_post") && data.has("url_post")) {
            JSONObject arr = data.getJSONObject("data_post");

            parent.url = parent.BASE_URL + data.getString("url_post");
            parent.show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        parent.dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            data.put("invoice_data",response_data);

                            startActivity(new Intent(parent, ChoosePaymentActivity.class)
                                    .putExtra("type","mobile_data")
                                    .putExtra("data",data.toString()));
                        } else {
                            parent.show_error_message(parent.layout, response_data.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        parent.error_handling(error, view);
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
        else
            startActivity(new Intent(parent, SuccessActivity.class)
                    .putExtra("data", data.toString()));
    }
}
