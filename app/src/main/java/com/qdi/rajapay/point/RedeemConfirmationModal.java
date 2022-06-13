package com.qdi.rajapay.point;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qdi.rajapay.R;

import org.json.JSONException;


public class RedeemConfirmationModal extends DialogFragment {

    TextView title;
    Button cancel, ok;

    PointIndexActivity parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.redeem_confirmation_modal, container, false);
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
                parent.redeemConfirmed();
                dismiss();
            }
        });
    }

    private void init(View view) throws JSONException {
        cancel = view.findViewById(R.id.cancel);
        ok = view.findViewById(R.id.ok);
        title = view.findViewById(R.id.title);

        parent = (PointIndexActivity) getActivity();

//        String text = getString(R.string.redeem_confirm_label).replace("#", parent.selectedData.name);
//        title.setText(text);
    }
}