package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RewardHistoryData {
    @SerializedName("id")
    public Integer id;

    @SerializedName(value = "cre_dtm")
    public String cre_dtm;

    @SerializedName("reward_name")
    public String reward_name;

    @SerializedName(value = "reward_point")
    public String reward_point;

    @SerializedName("reward_description")
    public String reward_description;

    @SerializedName("image")
    public String image;

    @SerializedName("status")
    public String status;

    @SerializedName("status_name")
    public String status_name;

    @SerializedName("review_note")
    public String review_note;

    @SerializedName("review_date")
    public String review_date;
}
