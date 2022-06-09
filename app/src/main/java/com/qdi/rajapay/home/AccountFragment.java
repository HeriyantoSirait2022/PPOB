package com.qdi.rajapay.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.R;
import com.qdi.rajapay.account.AccountLogoutModal;
import com.qdi.rajapay.account.information.InformationIndexActivity;
import com.qdi.rajapay.account.manage_account.ManageAccountIndexActivity;
import com.qdi.rajapay.account.verification.VerificationIntroActivity;
import com.qdi.rajapay.account.your_qr.YourQrIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @module 8.0 Akun
 * @screen 8.0.1
 */
public class AccountFragment extends Fragment {
    ImageView image, edit_account;
    TextView name,role;
    RecyclerView list;

    AccountAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ArrayList<Class> array_class = new ArrayList<>();

    MainActivity parent;
    JSONObject data;

    /**
     * @author Dinda
     * @note 0721531311-177 E03 Add phone number and edit button at 8.0.1 (Ref: Payfazz App)
     */
    // <code>
    Button editAccount;
    // </code>

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_account_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new AccountAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    if(!array.get(position).getString("title").equals(getResources().getString(R.string.account_logout_title)))
                        startActivity(new Intent(parent,array_class.get(position)));
                    else{
                        AccountLogoutModal modal = new AccountLogoutModal();
                        modal.show(getChildFragmentManager(),"modal");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        edit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, ManageAccountIndexActivity.class));
            }
        });

        /**
         * @author Dinda
         * @note 0721531311-177 E03 Add phone number and edit button at 8.0.1 (Ref: Payfazz App)
         */
        // <code>
        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(parent, ManageAccountIndexActivity.class));
            }
        });
        // </code>
    }

    @Override
    public void onResume() {
        get_data();
        super.onResume();
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);
        image = view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        role = view.findViewById(R.id.role);
        edit_account = view.findViewById(R.id.edit_account);
        editAccount = view.findViewById(R.id.editAccount);

        parent = (MainActivity) getActivity();

        prepare_data();
        adapter = new AccountAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        DividerItemDecoration decoration = new DividerItemDecoration(parent,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_verification_title));
        jsonObject.put("description", getResources().getString(R.string.account_verification_subtitle));
        jsonObject.put("image", R.drawable.ic_unshieldmdpi);
        array.add(jsonObject);
        array_class.add(VerificationIntroActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_your_qr_title));
        jsonObject.put("description", getResources().getString(R.string.account_your_qr_subtitle));
        jsonObject.put("image", R.drawable.icon_qr);
        array.add(jsonObject);
        array_class.add(YourQrIndexActivity.class);

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_information_title));
        jsonObject.put("description", getResources().getString(R.string.account_information_subtitle));
        jsonObject.put("image", R.drawable.ic_info_black_24);
        array.add(jsonObject);
        array_class.add(InformationIndexActivity.class);

        /*
        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_term_condition_title));
        jsonObject.put("description", getResources().getString(R.string.account_term_condition_subtitle));
        jsonObject.put("image", R.drawable.icon_building);
        array.add(jsonObject);
        array_class.add(VerificationIntroActivity.class);
        */

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.account_logout_title));
        jsonObject.put("description", "");
        jsonObject.put("image", R.drawable.ic_exit_to_app_black_24);
        array.add(jsonObject);
        array_class.add(VerificationIntroActivity.class);
    }

    public static String formatPhoneNumber(String phoneNoStr){
        String phoneNo = "+62 ";
        phoneNoStr = phoneNoStr.replace("62", "");

        phoneNo += phoneNoStr.substring(0,3) + "-";
        if(phoneNoStr.length() > 7){
            phoneNo += phoneNoStr.substring(3,7) + "-";
            phoneNo += phoneNoStr.substring(7).replaceAll(".", "X");
        }else{
            phoneNo += phoneNoStr.substring(3).replaceAll(".", "X");
        }

        return phoneNo;
    }

    private void get_data() {
        parent.url = parent.BASE_URL+"/mobile/account/user-info";
        parent.show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, parent.getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    parent.dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        parent.user_edit_SP.putString("user",response_data.toString());
                        parent.user_edit_SP.commit();

                        name.setText(response_data.getString("shopName"));
                        role.setText(formatPhoneNumber(response_data.getString("noHp")));
                    } else {
                        parent.show_error_message(parent.layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    parent.dismiss_wait_modal();
                    parent.error_handling(error, parent.layout);
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
}
