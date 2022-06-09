package com.qdi.rajapay.order;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.qdi.rajapay.model.DepositMutationData;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.qdi.rajapay.api.BaseAPI.BASE_URL;

/**
 * @module 5.2 Mutasi Deposit
 * @screen 5.2.1
 * @author qreatiq.liaomei
 */

public class DepositMutationFragment extends Fragment implements APICallback.ItemCallback<DepositMutationData> {
    TextView final_deposit;
    MaterialButton print;
    RecyclerView list;

    DepositMutationAdapter adapter;
    RecyclerView.LayoutManager layout_manager;

    MainActivity parent;

    TransactionAPI api;
    DepositMutationData depositMutationData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_deposit_mutation_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCsvReport();
            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);
        final_deposit = view.findViewById(R.id.final_deposit);
        print = view.findViewById(R.id.print);

        parent = (MainActivity) getActivity();

        adapter = new DepositMutationAdapter(parent);
        layout_manager = new LinearLayoutManager(parent);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        api = new TransactionAPI(parent, parent.user_SP);

        Date today = new Date();
        api.getDepositMutation(today, this);
        parent.show_wait_modal();
    }

    @Override
    public void onItemResponseSuccess(DepositMutationData item, String message) {

        parent.dismiss_wait_modal();

        this.depositMutationData = item;
        this.adapter.setData(item);
        final_deposit.setText("Rp. "+parent.formatter.format(item.getDepositFinal()));
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

    private void exportCsvReport() {

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        String filename = "MutasiDeposit_" + format.format(now);

        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        String month = monthFormat.format(now);

        String idLogin = parent.user_SP.getString("idLogin","");
        String idUser = parent.user_SP.getString("idUser","");
        String token = parent.user_SP.getString("token","");

        String url = BASE_URL + "/mobile/deposit-mutation/export-csv?" +
                "month=" + month;

        downloadCsv(filename, url, idLogin, idUser, token);
    }

    private void downloadCsv(String filename, String url, String idLogin, String idUser, String token) {
        parent.show_error_message(parent.layout, parent.getString(R.string.order_deposit_mutation_download));

        DownloadManager downloadmanager = (DownloadManager) parent.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.addRequestHeader("idLogin", idLogin);
        request.addRequestHeader("idUser", idUser);
        request.addRequestHeader("token", token);

        request.setTitle(filename);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename+".csv");

        downloadmanager.enqueue(request);
    }
}
