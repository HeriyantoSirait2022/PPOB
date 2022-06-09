package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderListData extends BaseResponseData {

    @SerializedName("arrTxnRslt")
    List<OrderData> orderDataList;
}
