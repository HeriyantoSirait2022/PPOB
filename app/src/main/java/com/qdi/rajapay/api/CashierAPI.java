package com.qdi.rajapay.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.qdi.rajapay.api.deserializer.PricelistResponseDeserializer;
import com.qdi.rajapay.api.deserializer.ProductResponseDeserializer;
import com.qdi.rajapay.model.BaseResponseData;
import com.qdi.rajapay.model.CashierDetailData;
import com.qdi.rajapay.model.PriceData;
import com.qdi.rajapay.model.PricelistData;
import com.qdi.rajapay.model.ProductData;
import com.qdi.rajapay.model.ProductListData;
import com.qdi.rajapay.model.ResponseData;
import com.qdi.rajapay.model.enums.ProductType;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CashierAPI extends BaseAPI {

    public CashierAPI(Context context, SharedPreferences user_SP) {
        super(context, user_SP);
    }

    public void getCashierDetail(APICallback.ItemCallback<CashierDetailData> callback) {

        String url = BASE_URL + "/mobile/cashier/detail";
        Type type = new TypeToken<ResponseData<CashierDetailData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        GsonRequest<ResponseData<CashierDetailData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetCashierDetailSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Response.Listener<ResponseData<CashierDetailData>> onGetCashierDetailSuccess(final APICallback.ItemCallback<CashierDetailData> callback) {
        return new Response.Listener<ResponseData<CashierDetailData>>() {
            @Override
            public void onResponse(ResponseData<CashierDetailData> response) {
                CashierDetailData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }

    public void getCashierProduct(ProductType productType, APICallback.ItemListCallback<ProductData> callback) {
        String url = BASE_URL + "/mobile/" + productType.getShowProductPath() + "/show-product";
        Type type = new TypeToken<ResponseData<ProductListData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        GsonRequest<ResponseData<ProductListData>> request = new GsonRequest<>(Request.Method.POST,
                url, param, type, null, onGetCashierProductSuccess(callback), onAPICallError(callback),
                new ProductResponseDeserializer(productType));

        queue.add(request);
    }

    private Response.Listener<ResponseData<ProductListData>> onGetCashierProductSuccess(final APICallback.ItemListCallback<ProductData> callback) {
        return new Response.Listener<ResponseData<ProductListData>>() {
            @Override
            public void onResponse(ResponseData<ProductListData> response) {
                ProductListData data = response.getResponse();
                callback.onListResponseSuccess(data.getProductList(), data.getMessage());
            }
        };
    }

    public void getCashierProductPricelist(ProductData product, APICallback.ItemListCallback<PriceData> callback) {
        ProductType productType = product.getType();
        String url = BASE_URL + "/mobile/" + productType.getPricelistPath();
        Type type = new TypeToken<ResponseData<ProductListData>>() {}.getType();

        Map<String, String> param = getCredentialParam();
        param.put("cdeProduct", product.getCdeProduct());
        param.put("productType", product.getProductTypeStr());

        GsonRequest<ResponseData<PricelistData>> request = new GsonRequest<>(Request.Method.POST,
                url, param, type, null, onGetCashierProductPricelistSuccess(callback), onAPICallError(callback),
                new PricelistResponseDeserializer(productType));

        queue.add(request);
    }

    private Response.Listener<ResponseData<PricelistData>> onGetCashierProductPricelistSuccess(final APICallback.ItemListCallback<PriceData> callback) {
        return new Response.Listener<ResponseData<PricelistData>>() {
            @Override
            public void onResponse(ResponseData<PricelistData> response) {
                PricelistData data = response.getResponse();
                callback.onListResponseSuccess(data.getPriceList(), data.getMessage());
            }
        };
    }

    public void updateCashierProductPrice(PriceData price, double agenPrice, final APICallback.ItemCallback<BaseResponseData> callback) {
        ProductType productType = price.getProductType();
        String url = BASE_URL + "/mobile/" + productType.getUpdatePricePath();
        Type type = new TypeToken<ResponseData<BaseResponseData>>() {}.getType();

        Map<String, String> param = getCredentialParam();
        if(price.getId()!=null)
            param.put("id", price.getId());
        param.putAll(getUpdateProductPriceParam(productType, price.getIdProductDetail(), agenPrice));

        GsonRequest<ResponseData<BaseResponseData>> request = new GsonRequest<>(
                Request.Method.PATCH, url, param, type, null,
                onUpdateCashierProductPriceSuccess(callback), onAPICallError(callback));

        queue.add(request);
    }

    private Map<String, String> getUpdateProductPriceParam(ProductType productType, String idProductDetail, double agenPrice) {

        Map<String, String> param = new HashMap<>();
        switch (productType) {
            case PULSA:
            case DATA:
            case TOPUPGAMES:
            case VOUCHERGAMES:
            case EMONEY:
            case TOKENPLN: {
                param.put("idProductDetail", idProductDetail);
                param.put("agenPrice", ((int)agenPrice)+"");
                break;
            }
            case BANKTRANSFER:
                param.put("idProductDetail", idProductDetail);
                param.put("agenPrice", ((int)agenPrice)+"");
                break;
            case TV:
            case TELKOM:
            case PLN:
            case PGN:
            case PDAM:
            case CELLULAR:
            case MULTIFINANCE:
            case BPJS: {
                param.put("idProductMaster", idProductDetail);
                param.put("agenFee", ((int)agenPrice)+"");
                break;
            }
        }
        return param;
    }

    private Response.Listener<ResponseData<BaseResponseData>> onUpdateCashierProductPriceSuccess(final APICallback.ItemCallback<BaseResponseData> callback) {
        return new Response.Listener<ResponseData<BaseResponseData>>() {
            @Override
            public void onResponse(ResponseData<BaseResponseData> response) {
                BaseResponseData data = response.getResponse();
                callback.onItemResponseSuccess(data, data.getMessage());
            }
        };
    }
}
