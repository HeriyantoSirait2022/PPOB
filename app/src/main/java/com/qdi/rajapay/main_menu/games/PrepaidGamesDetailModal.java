package com.qdi.rajapay.main_menu.games;

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
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.enums.ProductType;
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
 * @module 4.22 Games
 * @screen 4.22.3
 */
public class PrepaidGamesDetailModal extends BottomSheetDialogFragment {
    TextView title, detail, total_price, termCondition;

    ImageView image;
    Button buy_now;

    PrepaidGamesInputIdActivity parent;
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
                    String detail = "";
                    if(parent.productType == ProductType.TOPUPGAMES){
                        detail = parent.idGame.getText().toString();
                    }else{
                        if(parent.selectedDetail.has("detailGames")){
                            detail = parent.selectedDetail.getString("detailGames");
                        }
                    }
                    arr.add(new JSONObject("{\"title\":\""+title.getText().toString()+"\",\"price\":"+parent.selectedDetail.getDouble("price")+",\"detail\":\"" + detail + "\"}"));

                    arr.add(new JSONObject("{\"title\":\""+getResources().getString(R.string.admin_cost_label)+
                            "\",\"price\":"+admin_cost+"}"));

                    data.put("data",parent.selectedDetail);
                    data.put("data_post",add_data_to_post());
                    if(parent.productType == ProductType.TOPUPGAMES){
                        data.put("url_post","/mobile/prepaid/tgames-transaction");
                        data.put("url_pay","/mobile/prepaid/topup-tgames");
                    }else if(parent.productType == ProductType.VOUCHERGAMES){
                        data.put("url_post","/mobile/prepaid/vgames-transaction");
                        data.put("url_pay","/mobile/prepaid/topup-vgames");
                    }
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

        Spanned policy = Html.fromHtml(getString(R.string.main_menu_prepaid_data_modal_notice));
        termCondition = view.findViewById(R.id.termsOfUse);
        termCondition.setText(policy);
        termCondition.setMovementMethod(LinkMovementMethod.getInstance());

        parent = (PrepaidGamesInputIdActivity) getActivity();

        admin_cost = parent.selectedDetail.getDouble("adminFee");
        title.setText(parent.selectedDetail.getString("nameGames"));
        if(BaseActivity.isNullOrEmpty(parent.selectedDetail.getString("detailGames"))){
            detail.setVisibility(View.GONE);
        }else{
            detail.setVisibility(View.VISIBLE);
            detail.setText(parent.selectedDetail.getString("detailGames"));
        }
        total_price.setText("Rp. "+parent.formatter.format(parent.selectedDetail.getDouble("totalPrice")));

        if(parent.selectedProduct.has("image") && !BaseActivity.isNullOrEmpty(parent.selectedProduct.getString("image"))){
            Picasso.get()
                    .load(parent.selectedProduct.getString("image"))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit().centerInside()
                    .into(image);
        } else{
            image.setImageDrawable(parent.getResources().getDrawable(R.drawable.ic_default_4));
        }
    }

    private JSONObject add_data_to_post() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if(parent.productType == ProductType.TOPUPGAMES){
            String idGame = parent.idGame.getText().toString();
            jsonObject.put("noCust", idGame);
        }else if(parent.productType == ProductType.VOUCHERGAMES){
            JSONObject user_data = new JSONObject(parent.user_SP.getString("user",""));
            String noHp = user_data.getString("noHp");
            jsonObject.put("noCust", noHp);
        }

        jsonObject.put("typeTxn",parent.productType.toTxnType());
        jsonObject.put("idProduct",parent.selectedDetail.getString("idProduct"));
        jsonObject.put("productType",parent.selectedDetail.getString("productType"));
        jsonObject.put("idProductDetail",parent.selectedDetail.getString("idProductDetail"));
        jsonObject.put("codeGames",parent.selectedDetail.getString("codeGames"));
        jsonObject.put("nameGames",parent.selectedDetail.getString("nameGames"));
        jsonObject.put("idLogin",parent.user_SP.getString("idLogin",""));
        jsonObject.put("idUser",parent.user_SP.getString("idUser",""));
        jsonObject.put("token",parent.user_SP.getString("token",""));

        parent.setUserRefCde(jsonObject);

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
                                    .putExtra("type", parent.productType.toString())
                                    .putExtra("data",data.toString()));
                            dismiss();
                        } else {
                            parent.show_error_message(view, response_data.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
