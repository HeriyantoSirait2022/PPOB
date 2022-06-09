package com.qdi.rajapay.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.R;
import com.qdi.rajapay.account.manage_account.ManageAccountIndexActivity;
import com.qdi.rajapay.account.manage_account.change_pin.ChangePinIndexActivity;
import com.qdi.rajapay.auth.login.LoginActivity;
import com.qdi.rajapay.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AccountPinModal extends DialogFragment {
    Button yes;

    MainActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_pin_modal,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, ChangePinIndexActivity.class)
                        .putExtra("reqPinChange", true));
            }
        });
    }

    private void init(View view){
        yes = view.findViewById(R.id.yes);

        parent = (MainActivity) getActivity();
    }
}
