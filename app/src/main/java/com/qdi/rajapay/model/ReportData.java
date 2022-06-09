package com.qdi.rajapay.model;

import com.qdi.rajapay.model.enums.TransactionType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReportData implements Serializable {

    @SerializedName("idOrder")
    String id;

    @SerializedName("dateTxn")
    Date dateTxn;

    @SerializedName("typeTxn")
    TransactionType typeTxn;

    @SerializedName("agenPrice")
    double agenPrice;

    @SerializedName("profit")
    double profit;

    public ReportData(){}

    /**
     * @authors : liao.mei
     * @notes : change format from hh:mm to HH:mm (24 Hours)
     */
    // <code>
    public String getDetail() {
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("HH:mm");
        return displayDateFormat.format(dateTxn) + " | 1 barang";
    }
    // </code>

    public String getFormattedDate() {
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, dd MMMM yyyy");
        return displayDateFormat.format(dateTxn);
    }
}
