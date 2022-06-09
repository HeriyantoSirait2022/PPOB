package com.qdi.rajapay.payment;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.coupon.transaction.CouponListActivity;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.ReportData;
import com.qdi.rajapay.model.enums.TransactionStatus;
import com.qdi.rajapay.model.enums.TransactionType;
import com.qdi.rajapay.order.OrderDetailActivity;
import com.qdi.rajapay.payment.alfamart.AlfamartPayActivity;
import com.qdi.rajapay.payment.bank_transfer.BankTransferPayActivity;
import com.qdi.rajapay.payment.qris.QrisPayActivity;
import com.qdi.rajapay.payment.virtual_account.VirtualAccountPayActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.1 Isi Saldo
 * @screen 4.1.2
 * @module 4.1.3 Virtual Account
 * @screen 4.1.3.1
 * @module 4.10 Prabayar Data
 * @screen 4.10.6
 */
public class ChoosePaymentActivity extends BaseActivity implements APICallback.ItemCallback<OrderData> {
    ExpandableListView list;
    TextView total_price, order_date, order_id;
    ImageView copy_transaction_id;
    Button coupon, buy_now;
    CoordinatorLayout layout;
    LinearLayout show_detail;
    TextView order_status;

    ChoosePaymentAdapter adapter;
    ArrayList<JSONObject> array_header = new ArrayList<>(), array_additional = new ArrayList<>();
    HashMap<String, ArrayList<JSONObject>> array_child = new HashMap<>();
    int index_header, index_child;
    int indexHeaderPrev = -1, indexChildPrev = -1;

    JSONObject data, order_data, coupon_data = new JSONObject();
    public static final int REQUEST_COUPON = 1;

    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes 0911254300-198 W09 Add coupon discount to pricing component level admin
     * ...... add admin fee value
     */
    // <code>
    Double total = 0d, clean_total = 0d, admin_fee = 0d;
    // </code>

    boolean handleBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_payment);

        init_toolbar(getResources().getString(R.string.activity_title_transaction));
        try {
            init();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!check_selected_option())
                        show_error_message(layout, getResources().getString(R.string.choose_payment_not_choosen));
                    else {
                        JSONObject data_header = array_header.get(index_header);

                        JSONObject data1 = new JSONObject();
                        if (getIntent().hasExtra("type") && getIntent().getStringExtra("type").equals("deposit")) {
                            data1.put("categoryCoupon", data_header.getString("category"));
                            data1.put("typeCoupon", data_header.getString("type"));
                            data1.put("idProduct", data_header.getJSONArray("arr").getJSONObject(index_child).getString("idBank"));
                        } else {
                            data1.put("categoryCoupon", data_header.getString("category"));
                            data1.put("typeCoupon", data_header.getString("type"));
                            data1.put("idProduct", data_header.getString("idProduct"));
                            if (data.getJSONObject("invoice_data").getString("productCategory").equals("PREPAID")){
                                /**
                                 * @author Eliza Sutantya
                                 * @patch FR19022
                                 * @notes exclude BANKTRANSFER because bank transfer doesn't have child at tbl_product_detail
                                 */
                                // <code>
                                if(data.getJSONObject("invoice_data").has("idProductDetail") && !data.getJSONObject("invoice_data").isNull("idProductDetail")){
                                    data1.put("idProductDetail", data.getJSONObject("invoice_data").getInt("idProductDetail"));
                                }
                                // </code>
                            }
                        }

                        setUpSelected();

                        startActivityForResult(new Intent(ChoosePaymentActivity.this, CouponListActivity.class)
                                .putExtra("data", data1.toString())
                                .putExtra("admin_fee", admin_fee)
                                .putExtra("total", clean_total), REQUEST_COUPON);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    buy_now();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        show_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDetailModal modal = new PaymentDetailModal();
                modal.show(getSupportFragmentManager(), "modal");
            }
        });

        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });

        adapter.setOnItemClickListener(new ChoosePaymentAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    for (int x = 0; x < array_header.size(); x++) {
                        JSONArray jsonArray = array_header.get(x).getJSONArray("arr");
                        for (int y = 0; y < jsonArray.length(); y++)
                            jsonArray.getJSONObject(y)
                                    .put("selected", false);
                    }

                    array_header.get(index_header).getJSONArray("arr")
                            .getJSONObject(position)
                            .put("selected", true);
                    index_child = position;
                    if(indexChildPrev == -1)
                        indexChildPrev = index_child;

                    set_admin_fee();
                    /**
                     * @author Liao Mei
                     * @note refresh clean total price (after set admin fee), use total
                     */
                    // <code>
                    set_total_price();
                    JSONObject bank_info = array_header.get(index_header).getJSONArray("arr").getJSONObject(index_child);
                    total_price.setText("Rp. " + formatter.format(total));
                    // </code>

                    /**
                     * @author Lio Mei
                     * @note if user change payment method, delete coupon
                     */
                    // <code>
                    if(coupon_data != null && coupon_data.has("cdeCoupon")) {
                        if(index_header != indexHeaderPrev || index_child != indexChildPrev){
                            displaySnackBar(getStr(R.string.i_coupon_canceled));
                            refreshBreakdownPrice("");
                        }
                    }
                    // </code>

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                /**
                 * @author Jesslyn
                 * @note disable if the child item only one and isProblem = 1, if isInactive had sorted by Web Service
                 */
                // <code>
                JSONObject data = array_header.get(groupPosition);
                try {
                    JSONArray arr = data.getJSONArray("arr");
                    if (arr.length() == 1 && arr.getJSONObject(0).getInt("isProblem") == 1) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // </code>

                return list.isGroupExpanded(groupPosition) ? list.collapseGroup(groupPosition) : list.expandGroup(groupPosition);
//                try {
//                    if(groupPosition < 2)
//                    else {
//                        if(!array_header.get(groupPosition).getBoolean("selected")) {
//                            for (int x = 0; x < array_header.size(); x++) {
//                                if (x != groupPosition)
//                                    list.collapseGroup(x);
//                            }
//                            array_header.get(groupPosition).put("selected", true);
//                            index_header = groupPosition;
//
//                            adapter.notifyDataSetChanged();
//                        }
//                        else{
//                            array_header.get(groupPosition).put("selected",false);
//                            total_price.setText("Rp. "+formatter.format(total));
//
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return true;
            }
        });

        list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                try {
                    for (int x = 0; x < array_header.size(); x++) {
                        if (x != groupPosition)
                            list.collapseGroup(x);
                    }
                    array_header.get(groupPosition).put("selected", true);
                    index_header = groupPosition;
                    if(indexHeaderPrev == -1)
                        indexHeaderPrev = index_header;

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                try {
                    array_header.get(groupPosition).put("selected", false);
                    total_price.setText("Rp. " + formatter.format(total));

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        copy_transaction_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clip = ClipData.newPlainText("", String.valueOf(data.getJSONObject("invoice_data").getString("idOrder")));
                    clipboard.setPrimaryClip(clip);
                    show_error_message(layout, getResources().getString(R.string.copied_clipboard_label));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setUpSelected(){
        indexHeaderPrev = index_header;
        indexChildPrev = index_child;
    }

    private void init() throws JSONException, ParseException {
        list = findViewById(R.id.list);
        total_price = findViewById(R.id.total_price);
        coupon = findViewById(R.id.coupon);
        buy_now = findViewById(R.id.buy_now);
        layout = findViewById(R.id.layout);
        show_detail = findViewById(R.id.show_detail);
        order_status = findViewById(R.id.order_status);
        order_date = findViewById(R.id.order_date);
        order_id = findViewById(R.id.order_id);
        copy_transaction_id = findViewById(R.id.copy_transaction_id);

        data = new JSONObject(getIntent().getStringExtra("data"));
        /**
         * @author Jesslyn
         * @note handling back action if needed.
         *       calling super.onBackPressed when caller actvity comes from OrderDetailActivity
         *       this algorithm intended to minimized loading / server cost to get from api
         *       Handling back pressed only for BankTransferActivity, VAActivity, RetailActivity, QRActivity, prepaid, and postpaid activity
         */
        // <code>
        if (getIntent().hasExtra("caller")) {
            if (getIntent().getStringExtra("caller").equalsIgnoreCase(OrderDetailActivity.class.getCanonicalName())) {
                handleBack = false;
            } else {
                handleBack = true;
            }
        }
        // </code>
        try {
            prepare_data();
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter = new ChoosePaymentAdapter(this, array_header, array_child);
        list.setAdapter(adapter);

        order_status.setBackgroundColor(getResources().getColor(R.color.waiting));
        order_status.setText(getResources().getString(R.string.agency_commition_history_pending));

        SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format_date = new SimpleDateFormat("dd MMMM yyyy (HH:mm)");

        order_date.setText(format_date.format(parse_date.parse(data.getJSONObject("invoice_data").getString("dtOrder"))));
        order_id.setText("Order ID #" + data.getJSONObject("invoice_data").getString("idOrder"));

        set_total_price();
        if (!getIntent().hasExtra("type") || (getIntent().hasExtra("type") && !getIntent().getStringExtra("type").equals("deposit")))
            get_deposit_data();

        if (getIntent().hasExtra("type") && getIntent().getStringExtra("type").equals("coupon"))
            coupon.setVisibility(View.INVISIBLE);
    }

    public void set_total_price() throws JSONException {
        Double total = 0d, clean_total = 0d;
        JSONArray array = data.getJSONArray("breakdown_price");
        for (int x = 0; x < array.length(); x++) {
            if (array.getJSONObject(x).getDouble("price") > 0)
                clean_total += array.getJSONObject(x).getDouble("price");
            total += array.getJSONObject(x).getDouble("price");
            /**
             * @author Eliza Sutantya
             * @patch FR19022
             * @notes 0911254300-198 W09 Add coupon discount to pricing component level admin
             * ...... add admin fee value
             */
            // <code>
            if (array.getJSONObject(x).getString("title").equalsIgnoreCase("Biaya Admin")){
                admin_fee = array.getJSONObject(x).getDouble("price");
            }
            // </code>
        }
        total_price.setText("Rp. " + formatter.format(total));
        this.total = total;
        this.clean_total = clean_total;
    }

    public Boolean check_selected_option() throws JSONException {
        int counter = 0, index = 0;
        for (int x = 0; x < array_header.size(); x++) {
            if (array_header.get(x).getBoolean("selected")) {
                index = x;
                break;
            }
            counter++;
        }

        if (counter == array_header.size())
            return false;

        if (array_header.get(index).has("arr") && array_header.get(index).getJSONArray("arr").length() > 0) {
            JSONArray jsonArray = array_header.get(index).getJSONArray("arr");
            counter = 0;
            for (int x = 0; x < jsonArray.length(); x++) {
                if (jsonArray.getJSONObject(x).getBoolean("selected"))
                    break;
                counter++;
            }

            if (counter == jsonArray.length())
                return false;
        }
        return true;
    }

    public void buy_now() throws JSONException {
        if (!check_selected_option())
            show_error_message(layout, getResources().getString(R.string.choose_payment_not_choosen));
        else {
            if (getIntent().hasExtra("type") && getIntent().getStringExtra("type").equals("deposit")) {
                Intent intent = new Intent();
                if (array_header.get(index_header).getString("title").equals(getResources().getString(R.string.va_label)))
                    intent = new Intent(ChoosePaymentActivity.this, VirtualAccountPayActivity.class);
                else if (array_header.get(index_header).getString("title").equals(getResources().getString(R.string.bank_transfer_label)))
                    intent = new Intent(ChoosePaymentActivity.this, BankTransferPayActivity.class);
                else if (array_header.get(index_header).getString("title").equals(getResources().getString(R.string.qris_label)))
                    intent = new Intent(ChoosePaymentActivity.this, QrisPayActivity.class);
                else if (array_header.get(index_header).getString("title").equals(getResources().getString(R.string.alfamart_label)))
                    intent = new Intent(ChoosePaymentActivity.this, AlfamartPayActivity.class);

                set_admin_fee();
                JSONObject intent_data_payment = array_header.get(index_header).getJSONArray("arr").length() > 0 ?
                        array_header.get(index_header).getJSONArray("arr").getJSONObject(index_child) :
                        new JSONObject();
                JSONObject intent_order_data = data.getJSONObject("invoice_data");

                intent.putExtra("data", data.toString())
                        .putExtra("type", getIntent().getStringExtra("type"))
                        .putExtra("order_data", intent_order_data.toString())
                        .putExtra("coupon_data", coupon_data.toString())
                        .putExtra("payment", intent_data_payment.toString())
                        .putExtra("payment_header", array_header.get(index_header).toString());

                if(coupon_data.has("idCoupon"))
                    submit_coupon(intent, intent_data_payment, intent_order_data);
                else
                    startActivity(intent);
            } else
                startActivity(new Intent(this, PaymentPinActivity.class)
                        .putExtra("data", getIntent().getStringExtra("data"))
                        .putExtra("coupon_data", coupon_data.toString()));
        }
    }

    private void submit_coupon(final Intent intent, JSONObject dataPayment, JSONObject orderData) throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("idOrder",orderData.getString("idOrder"));
        arr.put("idCoupon",coupon_data.getString("idCoupon"));
        arr.put("cdeCoupon",coupon_data.getString("cdeCoupon"));
        arr.put("categoryCoupon",coupon_data.getString("categoryCoupon"));
        arr.put("typeCoupon",coupon_data.getString("typeCoupon"));
        arr.put("idProduct",dataPayment.getString("idBank"));
        arr.put("expCoupon",coupon_data.getString("expCoupon"));
        arr.put("amount",coupon_data.getDouble("price") * -1);
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/coupon/send";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        startActivity(intent);
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

    private void set_admin_fee() throws JSONException {
        JSONArray jsonArray = data.getJSONArray("breakdown_price");
        for (int x = 0; x < jsonArray.length(); x++) {
            if (array_header.get(index_header).getJSONArray("arr").length() > 0) {
                JSONObject bank_info = array_header.get(index_header).getJSONArray("arr").getJSONObject(index_child);
                if (jsonArray.getJSONObject(x).getString("title").equals("Biaya Admin") && bank_info.has("adminFee")) {
                    jsonArray.getJSONObject(x).put("price", bank_info.getDouble("adminFee"));
                }
            }
        }
    }

    private void prepare_data() throws JSONException {
        if (getIntent().hasExtra("type") && getIntent().getStringExtra("type").equals("deposit")) {
            JSONArray jsonArray = data.getJSONArray("data_payment_method");
            int index_va = -1, index_bt = -1, index_qris = -1, index_alfamart = -1;
            for (int x = 0; x < jsonArray.length(); x++) {
                if (jsonArray.getJSONObject(x).getString("type").equals("TOPUPVA"))
                    index_va = x;
                else if (jsonArray.getJSONObject(x).getString("type").equals("TOPUPBT"))
                    index_bt = x;
                else if (jsonArray.getJSONObject(x).getString("type").equals("TOPUPQRIS"))
                    index_qris = x;
                else if (jsonArray.getJSONObject(x).getString("type").equals("TOPUPALFA"))
                    index_alfamart = x;
            }

            /**
             * @author Jesslyn
             * @note fixing issue inactive parent payment record, check if index exist on the record or not
             */
            // <code>
            JSONObject data = jsonArray.getJSONObject(index_va);
            if (index_va >= 0) {
                data.put("title", getResources().getString(R.string.va_label));
                data.put("detail", getResources().getString(R.string.choose_payment_detail_va));
                data.put("selected", false);
                data.put("arr", new JSONArray(manage_fee_detail(data)));
                array_header.add(data);
            }

            if (index_bt >= 0) {
                data = jsonArray.getJSONObject(index_bt);
                data.put("title", getResources().getString(R.string.bank_transfer_label));
                data.put("detail", getResources().getString(R.string.choose_payment_detail_bank_transfer));
                data.put("selected", false);
                data.put("arr", new JSONArray(manage_fee_detail(data)));
                array_header.add(data);
            }

            if (index_alfamart >= 0) {
                data = jsonArray.getJSONObject(index_alfamart);
                data.put("title", getResources().getString(R.string.alfamart_label));
                data.put("detail", getResources().getString(R.string.choose_payment_detail_retail));
                data.put("selected", false);
                data.put("arr", new JSONArray(manage_fee_detail(data)));
                array_header.add(data);
            }

            if (index_qris > -1) {
                data = jsonArray.getJSONObject(index_qris);
                data.put("title", getResources().getString(R.string.qris_label));
                data.put("detail", getResources().getString(R.string.choose_payment_detail_qris));
                data.put("selected", false);
                data.put("arr", new JSONArray(manage_fee_detail(data)));
                array_header.add(data);
            }
            // </code>

            for (int x = 0; x < array_header.size(); x++)
                manage_child(x);
        } else {
            JSONObject data = new JSONObject();
            data.put("title", getResources().getString(R.string.deposit_label));
            data.put("detail", "Rp. " + formatter.format(user_SP.getFloat("balance_deposit", 0)));
            data.put("selected", false);
            data.put("category", this.data.getJSONObject("invoice_data").getString("productCategory"));
            data.put("type", this.data.getJSONObject("invoice_data").getString("productType"));
            data.put("idProduct", this.data.getJSONObject("invoice_data").getString("idProduct"));
            data.put("arr", new JSONArray());
            array_header.add(data);

            array_child.put(array_header.get(0).getString("title"), new ArrayList<JSONObject>());
        }
    }

    private String manage_fee_detail(JSONObject data) throws JSONException {
        ArrayList<JSONObject> arr = new ArrayList<>();
        JSONArray jsonArrayDetail = data.getJSONArray("arrFeeDetail");
        for (int y = 0; y < jsonArrayDetail.length(); y++) {
            JSONObject jsonObjectDetail = jsonArrayDetail.getJSONObject(y);
            jsonObjectDetail.put("name", jsonObjectDetail.getString("cdeBank"));
            jsonObjectDetail.put("image_url", jsonObjectDetail.getString("image"));
            jsonObjectDetail.put("selected", false);
            jsonObjectDetail.put("image", null);
            arr.add(jsonObjectDetail);
        }

        return arr.toString();
    }

    private void manage_child(int index) throws JSONException {
        ArrayList<JSONObject> arr = new ArrayList<>();
        arr.add(new JSONObject());
        array_child.put(array_header.get(index).getString("title"), arr);
    }

    private void get_deposit_data() {
        url = BASE_URL + "/mobile/dashboard/show-info";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        user_edit_SP.putFloat("balance_deposit", (float) response_data.getDouble("deposit"));
                        user_edit_SP.commit();

                        JSONObject jsonObject = array_header.get(0);
                        jsonObject.put("detail", "Rp. " + formatter.format(response_data.getDouble("deposit")));
                        array_header.set(0, jsonObject);
                        adapter.notifyDataSetChanged();
                    } else {
                        show_error_message(layout, response_data.getString("message"));
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

    private void refreshBreakdownPrice(String coupon) throws JSONException{
        JSONArray array = data.getJSONArray("breakdown_price");

        for (int x = 0; x < array_additional.size(); x++) {
            for (int y = 0; y < array.length(); y++) {
                if(array_additional.get(x).has("cdeCoupon") && array.getJSONObject(y).has("cdeCoupon")){
                    if (array_additional.get(x).getString("cdeCoupon").equals(
                            array.getJSONObject(y).getString("cdeCoupon")
                    )) {
                        array.remove(y);
                        break;
                    }
                }

            }
        }
        array_additional.clear();

        if(coupon.isEmpty()){
            coupon_data = new JSONObject();
        }else{
            array.put(array.length(), new JSONObject(coupon));
            array_additional.add(new JSONObject(coupon));
            coupon_data = new JSONObject(coupon);
        }

        set_total_price();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data1) {
        super.onActivityResult(requestCode, resultCode, data1);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_COUPON) {
                try {
                    refreshBreakdownPrice(data1.getStringExtra("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author Jesslyn
     * @note handling back action and redirect to order detail
     */
    // <code>
    OrderData selected;
    ReportData reportData;
    TransactionAPI api;

    @Override
    public void onBackPressed() {
        if (handleBack) {
            onProcessBackEvent();
        } else {
            super.onBackPressed();
        }
    }

    private void onProcessBackEvent() {
        api = new TransactionAPI(this, user_SP);

        try {
            reportData = new ReportData();
            if (data.getJSONObject("invoice_data").has("idOrder"))
                reportData.setId(data.getJSONObject("invoice_data").getString("idOrder"));
            if (data.getJSONObject("invoice_data").has("typeTxn"))
                reportData.setTypeTxn(TransactionType.fromString(data.getJSONObject("invoice_data").getString("typeTxn")));

            selected = new OrderData(reportData);

            if (data.getJSONObject("invoice_data").has("dtOrder")) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                selected.setDtOrder(format.parse(data.getJSONObject("invoice_data").getString("dtOrder")));
            }

            if (data.getJSONObject("invoice_data").has("status"))
                selected.setStatus(TransactionStatus.fromString(data.getJSONObject("invoice_data").getString("status")));

            if (data.getJSONArray("breakdown_price").getJSONObject(0).has("title"))
                selected.setNmTxn(data.getJSONArray("breakdown_price").getJSONObject(0).getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (selected.getStatus() == TransactionStatus.WAITING && selected.getTypeTxn() == null) {
            continuePaymentEmptyType(selected);
        } else {
            api.getOrderDetail(selected, ChoosePaymentActivity.this);
        }
    }

    private void continuePaymentEmptyType(final OrderData data) {
        api.continuePayment(data, new APICallback.ItemCallback<JSONObject>() {
            @Override
            public void onItemResponseSuccess(JSONObject item, String message) {

                double billAmount = item.optDouble("billAmount", 0);
                data.setBillAmount(billAmount);
                data.setTotBillAmount(billAmount);

                JSONObject mappedItem = mapContinuePayment(item);

                startActivity(new Intent(ChoosePaymentActivity.this, OrderDetailActivity.class)
                        .putExtra("data", data)
                        .putExtra("item", mappedItem.toString())
                );
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                // TODO
            }
        });
    }

    private JSONObject mapContinuePayment(JSONObject response_data) {

        double admin_price = 0;
        double billAmount = response_data.optDouble("billAmount", 0);
        ArrayList<JSONObject> arr = new ArrayList<>();
        ArrayList<JSONObject> array_payment_method = new ArrayList<>();
        JSONObject data = new JSONObject();

        try {
            JSONArray jsonArray = response_data.getJSONArray("arrPaymentMtdFee");
            for (int x = 0; x < jsonArray.length(); x++)
                array_payment_method.add(jsonArray.getJSONObject(x));

            arr.add(new JSONObject("{\"title\":\"Top Up Deposit RAJAPAY\",\"price\":" + billAmount + "}"));
            arr.add(new JSONObject("{\"title\":\"Biaya Admin\",\"price\":" + admin_price + "}"));

            data.put("data", new JSONObject("{\"data\":" + billAmount + "}"));
            data.put("invoice_data", response_data);
            data.put("data_payment_method", new JSONArray(array_payment_method.toString()));
            data.put("breakdown_price", new JSONArray(arr.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onItemResponseSuccess(OrderData item, String message) {
        item.setIdTxnAgen(selected.getIdTxnAgen());
        finish();
        startActivity(new Intent(ChoosePaymentActivity.this, OrderDetailActivity.class)
                .putExtra("data", item));
    }
    // </code>
}