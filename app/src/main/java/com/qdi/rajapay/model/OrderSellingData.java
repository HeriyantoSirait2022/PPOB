package com.qdi.rajapay.model;

import com.qdi.rajapay.utils.NumberUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderSellingData extends BaseResponseData implements Serializable {

    @SerializedName("sellingPrice")
    double sellingPrice;

    public String getFormattedTxnSellPrice() {
        return NumberUtils.format(sellingPrice);
    }
}