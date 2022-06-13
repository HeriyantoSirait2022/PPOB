package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RewardListData extends BaseResponseData{
    @SerializedName("arrRewardRslt")
    public List<RewardData> rewardData;

    @SerializedName("arrHistoryRslt")
    public List<RewardHistoryData> historyData;

    @SerializedName("baseUrl")
    public String baseUrl;
}
