package com.qdi.rajapay.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.qdi.rajapay.model.BaseResponseData;
import com.qdi.rajapay.model.ReportData;
import com.qdi.rajapay.model.ReportListData;
import com.qdi.rajapay.model.ReportPdfData;
import com.qdi.rajapay.model.ResponseData;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportAPI extends BaseAPI {

    public ReportAPI(Context context, SharedPreferences user_SP) {
        super(context, user_SP);
    }

    public void getReport(Date date, APICallback.ItemCallback<ReportListData> callback) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String month = simpleDateFormat.format(date);

        String url = BASE_URL + "/mobile/monthly-report/list";
        Type type = new TypeToken<ResponseData<ReportListData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("month", month);
        GsonRequest<ResponseData<ReportListData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetReportSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<ReportListData>> onGetReportSuccess(final APICallback.ItemCallback<ReportListData> callback) {
        return new Response.Listener<ResponseData<ReportListData>>() {
            @Override
            public void onResponse(ResponseData<ReportListData> response) {
                ReportListData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    private Response.Listener<ResponseData<ReportPdfData>> onExportPdfReportSuccess(final APICallback.ItemCallback<ReportPdfData> callback) {
        return new Response.Listener<ResponseData<ReportPdfData>>() {
            @Override
            public void onResponse(ResponseData<ReportPdfData> response) {
                ReportPdfData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    public void deleteReport(Date date, final APICallback.ItemCallback<BaseResponseData> callback) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String month = simpleDateFormat.format(date);

        String url = BASE_URL + "/mobile/monthly-report/delete";
        Type type = new TypeToken<ResponseData<BaseResponseData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("month", month);
        GsonRequest<ResponseData<BaseResponseData>> request = new GsonRequest<>(
                Request.Method.DELETE, url, param, type, null,
                onDeleteReportSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<BaseResponseData>> onDeleteReportSuccess(final APICallback.ItemCallback<BaseResponseData> callback) {
        return new Response.Listener<ResponseData<BaseResponseData>>() {
            @Override
            public void onResponse(ResponseData<BaseResponseData> response) {
                BaseResponseData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    public void deleteReportDetail(ReportData data, final APICallback.ItemCallback<BaseResponseData> callback) {

        String url = BASE_URL + "/mobile/monthly-report/delete-detail";
        Type type = new TypeToken<ResponseData<BaseResponseData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("idBill", data.getId());

        GsonRequest<ResponseData<BaseResponseData>> request = new GsonRequest<>(
                Request.Method.DELETE, url, param, type, null,
                onDeleteReportDetailSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<BaseResponseData>> onDeleteReportDetailSuccess(final APICallback.ItemCallback<BaseResponseData> callback) {
        return new Response.Listener<ResponseData<BaseResponseData>>() {
            @Override
            public void onResponse(ResponseData<BaseResponseData> response) {
                BaseResponseData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

}
