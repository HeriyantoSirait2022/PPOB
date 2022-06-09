package com.qdi.rajapay.cashier.monthly_report;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.ReportAPI;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.model.BaseResponseData;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.ReportData;
import com.qdi.rajapay.model.ReportListData;
import com.qdi.rajapay.print.PrintOrderOverviewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @module 6.0 Kasir
 * @screen 6.18.1
 */

public class CashierMonthlyReportActivity extends BaseActivity {
    TextView sales, profit, detail_date, date;
    Button download_report, delete_all_report;
    RecyclerView list;

    CashierMonthlyReportAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();
    ReportListData reportListData;

    JSONObject data_profit_sales = new JSONObject(), data = new JSONObject();

    ReportAPI reportApi;
    TransactionAPI transactionApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier_monthly_report);

        init_toolbar(getResources().getString(R.string.cashier_monthly_report));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        download_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exportPdfReport();
            }
        });

        delete_all_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteReport();
            }
        });

        adapter.setOnItemClickListener(new CashierMonthlyReportAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                ReportData reportData = reportListData.getReportDataList().get(position);
                CashierMonthlyReportActivity.this.getOrderDetail(reportData);
            }

            @Override
            public void onDelete(int position) {
                deleteReportDetail(reportListData.getReportDataList().get(position));
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        sales = findViewById(R.id.sales);
        profit = findViewById(R.id.profit);
        date = findViewById(R.id.date);
        detail_date = findViewById(R.id.detail_date);
        download_report = findViewById(R.id.download_report);
        delete_all_report = findViewById(R.id.delete_all_report);

        adapter = new CashierMonthlyReportAdapter(new ArrayList<ReportData>(),this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
        date.setText(simpleDateFormat.format(now));

        transactionApi = new TransactionAPI(this, user_SP);
        reportApi = new ReportAPI(this, user_SP);
        getReport();
    }

    private void getReport() {

        Date now = new Date();
        reportApi.getReport(now, new APICallback.ItemCallback<ReportListData>() {
            @Override
            public void onItemResponseSuccess(ReportListData item, String message) {
                dismiss_wait_modal();
                reportListData = item;
                adapter.setList(item.getReportDataList());
                sales.setText("Rp. " + formatter.format(item.getSales()));
                profit.setText("Rp. " + formatter.format(item.getProfit()));
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                CashierMonthlyReportActivity.this.onAPIResponseFailure(error);
            }
        });
        show_wait_modal();
    }

    private void exportPdfReport() {

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        String filename = "LaporanKasir_" + format.format(now);

        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        String month = monthFormat.format(now);

        String idLogin = user_SP.getString("idLogin","");
        String idUser= user_SP.getString("idUser","");
        String token= user_SP.getString("token","");

        String url = BASE_URL + "/mobile/monthly-report/export-pdf?" +
                "month=" + month;

        downloadPdf(filename, url, idLogin, idUser, token);
    }

    private void downloadPdf(String filename, String url, String idLogin, String idUser, String token) {
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.addRequestHeader("idLogin", idLogin);
        request.addRequestHeader("idUser", idUser);
        request.addRequestHeader("token", token);

        request.setTitle(filename);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename+".pdf");

        downloadmanager.enqueue(request);
    }

    private void deleteReport() {
        Date now = new Date();
        reportApi.deleteReport(now, new APICallback.ItemCallback<BaseResponseData>() {
            @Override
            public void onItemResponseSuccess(BaseResponseData item, String message) {
                dismiss_wait_modal();
                getReport();
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                CashierMonthlyReportActivity.this.onAPIResponseFailure(error);
            }
        });
        show_wait_modal();
    }

    private void deleteReportDetail(ReportData data) {

        reportApi.deleteReportDetail(data, new APICallback.ItemCallback<BaseResponseData>() {
            @Override
            public void onItemResponseSuccess(BaseResponseData item, String message) {
                dismiss_wait_modal();
                getReport();
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                CashierMonthlyReportActivity.this.onAPIResponseFailure(error);
            }
        });
        show_wait_modal();
    }

    private void getOrderDetail(final ReportData data) {
        show_wait_modal();
        // @notes : get order detail to feeding screen 6.18.2
        transactionApi.getOrderDetail(new OrderData(data), new APICallback.ItemCallback<OrderData>() {
            @Override
            public void onItemResponseSuccess(OrderData item, String message) {
                dismiss_wait_modal();

                startActivity(new Intent(CashierMonthlyReportActivity.this, PrintOrderOverviewActivity.class)
                        .putExtra("data",item)
                        .putExtra("class", CashierMonthlyReportActivity.class.toString()));
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                CashierMonthlyReportActivity.this.onAPIResponseFailure(error);
            }
        });
    }
}