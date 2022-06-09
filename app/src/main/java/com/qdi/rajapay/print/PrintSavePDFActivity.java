package com.qdi.rajapay.print;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.BaseAPI;
import com.qdi.rajapay.model.OrderData;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @module 5.1 Transaksi
 * @screen 5.1.5
 *
 * @module 6.18 Laporan Bulanan
 * @screen 6.18.3
 */
public class PrintSavePDFActivity extends BaseActivity {
    PDFView pdfView;
    ImageView imgDownload;
    View btnDownload;
    OrderData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_save_pdf);

        init_toolbar(getResources().getString(R.string.activity_title_save_pdf));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        pdfView = findViewById(R.id.pdfView);
        btnDownload = findViewById(R.id.btn_download);
        imgDownload = findViewById(R.id.img_download);

        data = (OrderData) getIntent().getSerializableExtra("data");
        final String idLogin = user_SP.getString("idLogin","");
        final String idUser= user_SP.getString("idUser","");
        final String token= user_SP.getString("token","");

        final String url = BaseAPI.BASE_URL + "/mobile/transaction-agen/" + data.getTypeTxn().toExportPdf() + "?" +
                "idOrder=" + data.getIdOrder();
        Log.d("url", url);

        URLConnection myURLConnection = null;
        InputStream inputStream = null;
        try {
            URL myURL = new URL(url);
            myURLConnection = (URLConnection)myURL.openConnection();

            myURLConnection.addRequestProperty ("idLogin", idLogin);
            myURLConnection.addRequestProperty ("idUser", idUser);
            myURLConnection.addRequestProperty ("token", token);

            inputStream = myURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfView.fromStream(inputStream).load();
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = data.getIdOrder();
                downloadPdf(filename, url, idLogin, idUser, token);
            }
        });

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = data.getIdOrder();
                downloadPdf(filename, url, idLogin, idUser, token);
            }
        });
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
}