package com.qdi.rajapay.coupon.transaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.coupon.home.CouponDetailActivity;

import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * @module 4.1.3 Virtual Account
 * @screen 4.1.3.3
 */
public class AddCouponConfirmationModal extends DialogFragment {
    TextView title;
    Button cancel, ok;

    CouponDetailActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_coupon_confirmation_modal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setResult(Activity.RESULT_OK,new Intent()
                        .putExtra("data",parent.data.toString()));
                parent.finish();
            }
        });
    }

    private void init(View view) throws JSONException {
        cancel = view.findViewById(R.id.cancel);
        ok = view.findViewById(R.id.ok);
        title = view.findViewById(R.id.title);

        parent = (CouponDetailActivity) getActivity();

        title.setText(parent.getResources().getString(R.string.coupon_label)+" "+
                parent.data.getString("title")+" "+
                parent.getResources().getString(R.string.add_coupon_confirmation_detail));
    }
}