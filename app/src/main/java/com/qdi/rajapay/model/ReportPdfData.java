package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReportPdfData extends BaseResponseData{

    @SerializedName("name")
    String name;

    @SerializedName("pdf")
    String pdf;
}
