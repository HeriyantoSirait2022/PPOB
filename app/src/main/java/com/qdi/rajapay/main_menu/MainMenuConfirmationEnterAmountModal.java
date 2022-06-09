package com.qdi.rajapay.main_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.qdi.rajapay.R;

import org.json.JSONException;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainMenuConfirmationEnterAmountModal extends DialogFragment {
    Button submit, cancel;
    EditText amount;

    MainMenuConfirmationActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_menu_confirmation_enter_amount_modal,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(amount.getText().toString().equals(""))
                        parent.show_error_message(view,getResources().getString(R.string.main_menu_enter_amount_modal_empty_amount));
                    else if(Double.parseDouble(amount.getText().toString()) < parent.total_price_min_data)
                        parent.show_error_message(view,getResources().getString(R.string.main_menu_enter_amount_modal_amount_below_min));
                    else if(Double.parseDouble(amount.getText().toString()) > parent.total_price_max_data)
                        parent.show_error_message(view,getResources().getString(R.string.main_menu_enter_amount_modal_amount_above_max));
                    else {
                        parent.amount = Double.parseDouble(amount.getText().toString());
                        parent.post_main_menu();
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void init(View view){
        amount = view.findViewById(R.id.amount);
        submit = view.findViewById(R.id.submit);
        cancel = view.findViewById(R.id.cancel);

        parent = (MainMenuConfirmationActivity) getActivity();
    }
}
