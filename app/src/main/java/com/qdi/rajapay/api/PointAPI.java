package com.qdi.rajapay.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.OrderListData;
import com.qdi.rajapay.model.RewardData;
import com.qdi.rajapay.model.RewardHistoryData;
import com.qdi.rajapay.model.RewardListData;
import com.qdi.rajapay.model.ResponseData;
import com.qdi.rajapay.model.enums.TransactionStatus;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PointAPI extends BaseAPI{

    public PointAPI(Context context, SharedPreferences user_SP) {
        super(context, user_SP);
    }

    /**
     * Get list reward
     * @param callback
     */
    public void getList(APICallback.ItemMultipleListCallback<RewardData, RewardHistoryData> callback) {
        String url = BASE_URL + "/mobile/reward/list";
        Type type = new TypeToken<ResponseData<RewardListData>>() {}.getType();
        Map<String, String> param = getCredentialParam();
        GsonRequest<ResponseData<RewardListData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onGetRewardListSuccess(callback), onAPICallError(callback));
        queue.add(request);
    }

    private Response.Listener<ResponseData<RewardListData>> onGetRewardListSuccess(final APICallback.ItemMultipleListCallback<RewardData, RewardHistoryData> callback) {
        return new Response.Listener<ResponseData<RewardListData>>() {
            @Override
            public void onResponse(ResponseData<RewardListData> response) {
                RewardListData data = response.getResponse();
                callback.onMulitpleListResponseSuccess(data.rewardData, data.historyData, data.baseUrl);
            }
        };
    }

    public void requestReward(RewardData data, APICallback.ItemCallback<RewardData> callback) {
        String url = BASE_URL + "/mobile/reward/request";

        Type type = new TypeToken<ResponseData<RewardData>>() {}.getType();

        Map<String, String> param = getCredentialParam();
        param.put("idReward", data.id.toString());

        GsonRequest<ResponseData<RewardData>> request = new GsonRequest<>(Request.Method.POST, url, param, type, null, onRequestResultOk(callback), onAPICallError(callback));
        queue.add(request);
    }
    // </code>

    private Response.Listener<ResponseData<RewardData>> onRequestResultOk(final APICallback.ItemCallback<RewardData> callback) {
        return new Response.Listener<ResponseData<RewardData>>() {
            @Override
            public void onResponse(ResponseData<RewardData> response) {
                RewardData data = response.getResponse();
                callback.onItemResponseSuccess(data, null);
            }
        };
    }
}
