package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CashierDetailData extends BaseResponseData {

    @SerializedName("date")
    public String date;

    @SerializedName("sales")
    public Integer sales;

    @SerializedName("profit")
    public Integer profit;
}