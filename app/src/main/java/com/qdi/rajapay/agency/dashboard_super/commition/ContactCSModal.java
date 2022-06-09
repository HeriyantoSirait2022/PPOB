package com.qdi.rajapay.agency.dashboard_super.commition;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qdi.rajapay.R;
import com.qdi.rajapay.account.manage_account.ManageAccountIndexActivity;
import com.qdi.rajapay.account.manage_account.ManageAccountIndexAdapter;
import com.qdi.rajapay.account.manage_account.edit_name.EditNameEnterPasswordActivity;
import com.qdi.rajapay.agency.commition_history.CommitionHistoryFilterAdapter;
import com.qdi.rajapay.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lombok.val;

public class ContactCSModal extends BottomSheetDialogFragment {
    RecyclerView list;
    ImageView dialogDismiss;

    ManageAccountIndexAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ArrayList<String> helpMsg = new ArrayList<>();

    MainActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_cs_modal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialogDismiss.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                }
        );

        adapter.setOnItemClickListener(new ManageAccountIndexAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    Context ctx = requireContext();
                    if (isAppInstalled(ctx, "com.whatsapp.w4b") || isAppInstalled(ctx, "com.whatsapp")) {
                        PackageManager packageManager = requireContext().getPackageManager();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String url = "https://api.whatsapp.com/send?phone=6285762222888" + "&text=" + URLEncoder.encode( helpMsg.get(position), "utf-8");
                        i.setData(Uri.parse(url));
                        if (i.resolveActivity(packageManager) != null) {
                            requireContext().startActivity(i);
                        }
                    } else {
                        Toast.makeText(ctx, "whatsApp is not installed", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isAppInstalled(Context ctx, String packageName) {
        PackageManager pm = ctx.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
    
    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);
        dialogDismiss = view.findViewById(R.id.dialog_dismiss);

        parent = (MainActivity) getActivity();

        prepare_data();
        adapter = new ManageAccountIndexAdapter(array,parent);
        layout_manager = new LinearLayoutManager(parent);
        DividerItemDecoration decoration = new DividerItemDecoration(parent,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.contact_cs_pertanyaan_umum));
        jsonObject.put("description", getResources().getString(R.string.contact_cs_pertanyaan_umum_detail));
        jsonObject.put("image", R.drawable.ic_icon_question_mark);
        array.add(jsonObject);
        helpMsg.add("Saya butuh bantuan mengenai aplikasi Rajapay ");

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.contact_cs_pertanyaan_deposit));
        jsonObject.put("description", getResources().getString(R.string.contact_cs_pertanyaan_deposit_detail));
        jsonObject.put("image", R.drawable.ic_icon_saldo);
        array.add(jsonObject);
        helpMsg.add("Hi Admin, saya butuh bantuan terkait saldo saya  ");

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.contact_cs_pertanyaan_transaksi));
        jsonObject.put("description", getResources().getString(R.string.contact_cs_pertanyaan_transaksi_detail));
        jsonObject.put("image", R.drawable.ic_icon_price_tag);
        array.add(jsonObject);
        helpMsg.add("Halo, saya butuh bantuan terkait transaksi saya dengan nomor  ");

        jsonObject = new JSONObject();
        jsonObject.put("title", getResources().getString(R.string.contact_cs_pertanyaan_akun));
        jsonObject.put("description", getResources().getString(R.string.contact_cs_pertanyaan_akun_detail));
        jsonObject.put("image", R.drawable.ic_icon_contact);
        array.add(jsonObject);
        helpMsg.add("Mohon bantuan kenapa akun saya ");
    }
}
