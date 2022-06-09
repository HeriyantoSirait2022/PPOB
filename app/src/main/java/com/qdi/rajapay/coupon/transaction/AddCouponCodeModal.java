package com.qdi.rajapay.coupon.transaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qdi.rajapay.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * @screen 4.10.4
 * @screen 4.11.5
 * @screen 4.12.5
 * @screen 4.13.5
 * @screen 4.14.5
 * @screen 4.15.5
 * @screen 4.16.5
 * @screen 4.17.6
 * @screen 4.18.6
 * @screen 4.19.6
 * @screen 4.20.6
 */
public class AddCouponCodeModal extends BottomSheetDialogFragment {
    EditText coupon;
    ImageView close;
    Button enter;

    CouponListActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_coupon_code_modal,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(coupon.getText().toString().equals(""))
                        parent.show_error_message(parent.layout,getResources().getString(R.string.add_code_coupon_empty_coupon));
                    else {
                        parent.coupon = coupon.getText().toString();

                        parent.get_data();
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void init(View view){
        coupon = view.findViewById(R.id.coupon);
        enter = view.findViewById(R.id.enter);
        close = view.findViewById(R.id.close);

        parent = (CouponListActivity) getActivity();
    }
}