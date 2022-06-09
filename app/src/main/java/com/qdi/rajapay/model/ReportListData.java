package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReportListData extends BaseResponseData {

    @SerializedName("sales")
    double sales;

    @SerializedName("profit")
    double profit;

    @SerializedName("month")
    String month;

    @SerializedName("arrReportDtl")
    List<ReportData> reportDataList;
}
