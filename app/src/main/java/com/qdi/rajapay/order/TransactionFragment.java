package com.qdi.rajapay.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.enums.TransactionStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment implements APICallback.ItemListCallback<OrderData>, APICallback.ItemCallback<OrderData> {
    RecyclerView list;

    TransactionAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    List<OrderData> array = new ArrayList<>();

    MainActivity parent;
    OrderData selected;
    String query = "";
    TransactionAPI api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_transaction_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new TransactionAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                selected = array.get(position);
                if(selected.getStatus()==TransactionStatus.WAITING && selected.getTypeTxn() == null) {
                    // condition for transaction status waiting and type txn null means deposit transaction which hasn't selected bank method
                    continuePaymentEmptyType(selected);
                } else {
                    // @notes : get order detail to feeding screen 5.1.2
                    api.getOrderDetail(selected, TransactionFragment.this);
                }

            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);

        parent = (MainActivity) getActivity();

        adapter = new TransactionAdapter(new ArrayList<OrderData>(),parent);
        layout_manager = new LinearLayoutManager(parent);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        api = new TransactionAPI(parent, parent.user_SP);
        api.getList(TransactionStatus.ALL, query, this);
    }

    public void setQuery(String query) {
        this.query = query;
        api.getList(TransactionStatus.ALL, query, this);
        parent.show_wait_modal();
    }

    @Override
    public void onListResponseSuccess(List<OrderData> list, String message) {
        parent.dismiss_wait_modal();
        this.array = list;
        adapter.setArr(list);
    }

    @Override
    public void onItemResponseSuccess(OrderData item, String message) {

        /* Possible unused code */
        // item.setIdTxnAgen(selected.getIdTxnAgen());
        /**
         * @author Dinda
         * @note add caller to handling back
         */
        // <code>
        startActivity(new Intent(parent, OrderDetailActivity.class)
                .putExtra("caller", parent.getClass().getCanonicalName())
                .putExtra("data",item));
        // </code>
    }

    @Override
    public void onAPIResponseFailure(VolleyError error) {

        parent.dismiss_wait_modal();
        try {
            parent.error_handling(error, parent.layout);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
            parent.show_error_message(parent.layout, e.getLocalizedMessage());
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

                /**
                 * @author Dinda
                 * @note add caller to handling back
                 */
                // <code>
                startActivity(new Intent(parent, OrderDetailActivity.class)
                        .putExtra("data",data)
                        .putExtra("caller", parent.getClass().getCanonicalName())
                        .putExtra("item", mappedItem.toString())
                );
                // </code>
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                parent.onAPIResponseFailure(error);
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
            for(int x = 0; x < jsonArray.length(); x++)
                array_payment_method.add(jsonArray.getJSONObject(x));

            arr.add(new JSONObject("{\"title\":\"Top Up Deposit RAJAPAY\",\"price\":"+billAmount+"}"));
            arr.add(new JSONObject("{\"title\":\"Biaya Admin\",\"price\":"+admin_price+"}"));

            data.put("data",new JSONObject("{\"data\":"+billAmount+"}"));
            data.put("invoice_data",response_data);
            data.put("data_payment_method",new JSONArray(array_payment_method.toString()));
            data.put("breakdown_price",new JSONArray(arr.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
