package com.qdi.rajapay.payment.qris;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.qdi.rajapay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @module 4.1.3 Virtual Account
 * @screen 4.1.3.6
 */
public class QrisPaymentDetailContModal extends DialogFragment {
    LinearLayout content_layout;
    TextView total;

    QrisPayContActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.virtual_account_detail_modal,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init(View view) throws JSONException {
        content_layout = view.findViewById(R.id.content_layout);
        total = view.findViewById(R.id.total);

        parent = (QrisPayContActivity) getActivity();

        List<JSONObject> array = parent.orderData.getBreakdownPrice();
        Double total_data = 0d;
        for(int x=0;x<array.size();x++){
            add_view(array.get(x),content_layout);
            total_data += array.get(x).getDouble("price");
        }

        total.setText("Rp. "+parent.formatter.format(total_data));
    }

    private void add_view(JSONObject data, LinearLayout layout) throws JSONException {
        View view1 = getLayoutInflater().inflate(R.layout.payment_detail_item,null);

        TextView title = view1.findViewById(R.id.title);
        TextView price = view1.findViewById(R.id.price);
        TextView detail = view1.findViewById(R.id.detail);
//        price.setTextColor(null);

        title.setText(data.getString("title"));
        price.setText("Rp. "+parent.formatter.format(data.getDouble("price")));
        if(!data.getString("title").equals(getResources().getString(R.string.total_price_label)))
            price.setTextColor(getResources().getColor(R.color.black));
        if(data.isNull("detail"))
            detail.setVisibility(View.GONE);
        else{
            detail.setVisibility(View.VISIBLE);
            detail.setText(data.getString("detail"));
        }

        layout.addView(view1);
    }
}