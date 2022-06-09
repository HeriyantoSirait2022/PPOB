package com.qdi.rajapay.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.model.BaseResponseData;
import com.qdi.rajapay.model.DepositMutationCsvData;
import com.qdi.rajapay.model.DepositMutationData;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.OrderListData;
import com.qdi.rajapay.model.OrderPdfData;
import com.qdi.rajapay.model.OrderPrintData;
import com.qdi.rajapay.model.OrderSellingData;
import com.qdi.rajapay.model.ResponseData;
import com.qdi.rajapay.model.enums.ProductType;
import com.qdi.rajapay.model.enums.TransactionStatus;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TransactionAPI extends BaseAPI {

    public TransactionAPI(Context context, SharedPreferences user_SP) {
        super(context, user_SP);
    }

    public void getList(TransactionStatus status, String query, APICallback.ItemListCallback<OrderData> callback) {
        String url = BASE_URL + "/mobile/transaction/list";
        Type type = new TypeToken<ResponseData<OrderListData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("status", status.toString());
        if(!query.equals("")) param.put("search", query);
        GsonRequest<ResponseData<OrderListData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetTransactionListSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<OrderListData>> onGetTransactionListSuccess(final APICallback.ItemListCallback<OrderData> callback) {
        return new Response.Listener<ResponseData<OrderListData>>() {
            @Override
            public void onResponse(ResponseData<OrderListData> response) {
                OrderListData data = response.getResponse();
                List<OrderData> orderDataList = data.getOrderDataList();
                Collections.sort(orderDataList, new Comparator<OrderData>() {
                    public int compare(OrderData o1, OrderData o2) {
                        return o2.getDtOrder().compareTo(o1.getDtOrder());
                    }
                });

                callback.onListResponseSuccess(orderDataList, data.getMessage());
            }
        };
    }

    public void getOrderDetail(OrderData data, APICallback.ItemCallback<OrderData> callback) {
        /**
         * @author Jesslyn
         * @note change function to handling txnType null
         */
        // <code>
        ProductType productType = data.getProductTypeData();
        // </code>
        String detailPath = productType != null ? productType.getOrderDetailPath() : "";
        String url = BASE_URL + "/mobile/transaction/" + detailPath;
        Type type = new TypeToken<ResponseData<OrderData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("idOrder", data.getIdOrder());
        GsonRequest<ResponseData<OrderData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetOrderDetailSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    /**
     * @author Jesslyn
     * @note case TDD 12491 - add function for manualAdvice
     * @param data
     * @param hashId
     * @param callback
     */
    // <code>
    private Response.Listener<ResponseData<OrderData>> onManualAdviceResultOk(final APICallback.ItemCallback<OrderData> callback) {
        return new Response.Listener<ResponseData<OrderData>>() {
            @Override
            public void onResponse(ResponseData<OrderData> response) {
                OrderData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    public void manualAdvice(OrderData data, APICallback.ItemCallback<OrderData> callback) {
        String url = BASE_URL + "/mobile/transaction/manual-advice";

        Type type = new TypeToken<ResponseData<OrderData>>() {}.getType();

        Map<String, String> param = getCredentialParam();
        param.put("idOrder", data.getIdOrder());
        param.put("hashId", data.getHashId());

        GsonRequest<ResponseData<OrderData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onManualAdviceResultOk(callback), onAPICallError(callback));
        queue.add(request);
    }
    // </code>

    private Response.Listener<ResponseData<OrderData>> onGetOrderDetailSuccess(final APICallback.ItemCallback<OrderData> callback) {
        return new Response.Listener<ResponseData<OrderData>>() {
            @Override
            public void onResponse(ResponseData<OrderData> response) {
                OrderData data = response.getResponse();
                if(data.getIdTxnAgen()!=null) {
                    getOrderSelling(data, callback);
                } else {
                    callback.onItemResponseSuccess(data, data.getMessage());
                }

            }
        };
    }

    // function to get selling price. https://rajapay.xyz/public/api/mobile/transaction-agen/prepaid-tokenpln
    // there were plenty of data that unused, please refers to api modul5controller
    // only used selling price, this api intended to get only selling price (perhaps agent already set the price at module 6) thus agent doesn't need to set price every "Cetak dan Catat"
    public void getOrderSelling(OrderData data, APICallback.ItemCallback<OrderData> callback) {
        String url = BASE_URL + "/mobile/transaction-agen/" + data.getTypeTxn().toAgenDetail();
        Type type = new TypeToken<ResponseData<OrderSellingData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("idOrder", data.getIdOrder());
        GsonRequest<ResponseData<OrderSellingData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetOrderSellingSuccess(data, callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<OrderSellingData>> onGetOrderSellingSuccess(final OrderData orderData, final APICallback.ItemCallback<OrderData> callback) {
        return new Response.Listener<ResponseData<OrderSellingData>>() {
            @Override
            public void onResponse(ResponseData<OrderSellingData> response) {
                OrderSellingData data = response.getResponse();
                orderData.setSellingData(data);
                callback.onItemResponseSuccess(orderData, data.getMessage());
            }
        };
    }

    public void cancelOrder(String idOrder, final APICallback.ItemCallback<BaseResponseData> callback) {
        String url = BASE_URL + "/mobile/transaction/cancel";
        Type type = new TypeToken<ResponseData<BaseResponseData>>() {}.getType();

        Map<String, String> param = getCredentialParam();
        param.put("idOrder", idOrder);

        GsonRequest<ResponseData<BaseResponseData>> request = new GsonRequest<>(
                Request.Method.PATCH, url, param, type, null,
                onCancelOrderSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<BaseResponseData>> onCancelOrderSuccess(final APICallback.ItemCallback<BaseResponseData> callback) {
        return new Response.Listener<ResponseData<BaseResponseData>>() {
            @Override
            public void onResponse(ResponseData<BaseResponseData> response) {
                BaseResponseData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    public void insertAgenPrice(OrderData data, final APICallback.ItemCallback<BaseResponseData> callback) {
        String url = BASE_URL + "/mobile/transaction/agen";
        Type type = new TypeToken<ResponseData<BaseResponseData>>() {}.getType();

        Map<String, String> param = getCredentialParam();
        param.putAll(data.toMap());

        GsonRequest<ResponseData<BaseResponseData>> request = new GsonRequest<>(
                Request.Method.POST, url, param, type, null,
                onUpdateAgenPriceSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    public void updateAgenPrice(OrderData data, final APICallback.ItemCallback<BaseResponseData> callback) {
        String url = BASE_URL + "/mobile/transaction/agen-edit";
        Type type = new TypeToken<ResponseData<BaseResponseData>>() {}.getType();

        Map<String, String> param = getCredentialParam();
        param.putAll(data.toMap());

        /**
         * @authors : Jesslyn
         * @notes : fixing issue when updating price at print preview. change patch to post method
         */
        // <code>
        GsonRequest<ResponseData<BaseResponseData>> request = new GsonRequest<>(
                Request.Method.POST, url, param, type, null,
                onUpdateAgenPriceSuccess(callback), onAPICallError(callback));
        // </code>

        queue.add(request);
    }

    private Response.Listener<ResponseData<BaseResponseData>> onUpdateAgenPriceSuccess(final APICallback.ItemCallback<BaseResponseData> callback) {
        return new Response.Listener<ResponseData<BaseResponseData>>() {
            @Override
            public void onResponse(ResponseData<BaseResponseData> response) {
                BaseResponseData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    private Response.Listener<ResponseData<OrderPdfData>> onExportOrderDetailSuccess(final APICallback.ItemCallback<OrderPdfData> callback) {
        return new Response.Listener<ResponseData<OrderPdfData>>() {
            @Override
            public void onResponse(ResponseData<OrderPdfData> response) {
                OrderPdfData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    public void getOrderDetailPrint(OrderData data, APICallback.ItemCallback<OrderPrintData> callback) {
        String url = BASE_URL + "/mobile/transaction-agen/" + data.getTypeTxn().toProductType().getOrderDetailPrintPath();
        Type type = new TypeToken<ResponseData<OrderPrintData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("idOrder", data.getIdOrder());
        GsonRequest<ResponseData<OrderPrintData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetOrderDetailPrintSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<OrderPrintData>> onGetOrderDetailPrintSuccess(final APICallback.ItemCallback<OrderPrintData> callback) {
        return new Response.Listener<ResponseData<OrderPrintData>>() {
            @Override
            public void onResponse(ResponseData<OrderPrintData> response) {
                OrderPrintData dataResponse = response.getResponse();
                callback.onItemResponseSuccess(dataResponse, dataResponse.getMessage());
            }
        };
    }

    public void getDepositMutation(Date date, APICallback.ItemCallback<DepositMutationData> callback) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String month = simpleDateFormat.format(date);

        String url = BASE_URL + "/mobile/deposit-mutation";
        Type type = new TypeToken<ResponseData<DepositMutationData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        param.put("month", month);
        GsonRequest<ResponseData<DepositMutationData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetDepositMutationSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<DepositMutationData>> onGetDepositMutationSuccess(final APICallback.ItemCallback<DepositMutationData> callback) {
        return new Response.Listener<ResponseData<DepositMutationData>>() {
            @Override
            public void onResponse(ResponseData<DepositMutationData> response) {
                DepositMutationData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    private Response.Listener<ResponseData<DepositMutationCsvData>> onExportDepositMutationSuccess(final APICallback.ItemCallback<DepositMutationCsvData> callback) {
        return new Response.Listener<ResponseData<DepositMutationCsvData>>() {
            @Override
            public void onResponse(ResponseData<DepositMutationCsvData> response) {
                DepositMutationCsvData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    public void continuePayment(OrderData data, final APICallback.ItemCallback<JSONObject> callback) {
        final String url = BaseAPI.BASE_URL + "/mobile/transaction/continue-payment";

        Map<String, String> param = getCredentialParam();
        param.put("idOrder", data.getIdOrder() );

        if(data.getTypeTxn() != null) {
            param.put("typeTxn", data.getTypeTxn().toString() );
        }

        if(data.getImage() != null) {
            param.put("image", data.getImage() );
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), onContinuePaymentSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<JSONObject> onContinuePaymentSuccess(final APICallback.ItemCallback<JSONObject> callback) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    callback.onItemResponseSuccess(response.getJSONObject("response"), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onAPIResponseFailure(null);
                }
            }
        };
    }
}
