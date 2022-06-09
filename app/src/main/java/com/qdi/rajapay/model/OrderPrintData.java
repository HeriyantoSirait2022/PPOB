package com.qdi.rajapay.model;

import com.qdi.rajapay.utils.NumberUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderPrintData extends OrderData {

    @SerializedName("sellingPrice")
    double sellingPrice;

    //prepaid data
    @SerializedName("typeData")
    String typeData;

    /**
     * @authors : Liao Mei
     * @notes : 1. get date from tbl_postpaid_txn, tbl_prepaid_txn
     *          2. create new function for print date (invoice)
     */
    // <code>
    @SerializedName("date")
    Date createdDate;

    public String getPrintFormattedCreatedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE. dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(createdDate) + " " + timeFormat.format(createdDate) + " WIB";
    }
    // </code>

    @SerializedName("detailData")
    String detailData;

    //prepaid pulsa
    @SerializedName("typePulsa")
    String typePulsa;

    @SerializedName("detailPulsa")
    String detailPulsa;



    public OrderPrintData(ReportData data) {
        super(data);
    }

    public OrderPrintData() {
        super();
    }

    public String getFormattedFee() {
        return NumberUtils.format(fee);
    }

    public String getFormattedSellingPrice() {
        return NumberUtils.format(sellingPrice);
    }

    @Data
    public class DetailTagihan implements Serializable {

        @SerializedName("meterAwal")
        String meterAwal;

        @SerializedName("meterAkhir")
        String meterAkhir;

        @SerializedName("periode")
        String periode;

        @SerializedName("nilaiTagihan")
        double nilaiTagihan;

        @SerializedName("denda")
        double denda;

        @SerializedName("admin")
        double admin;

        @SerializedName("total")
        double total;

        public String getFormattedNilaiTagihan() {
            return NumberUtils.format(nilaiTagihan);
        }

        public String getFormattedDenda() {
            return NumberUtils.format(denda);
        }
    }

    @Data
    public class DetailPeserta implements Serializable {
        @SerializedName("noPeserta")
        String noPeserta;

        @SerializedName("nama")
        String nama;

        @SerializedName("premi")
        double premi;

        @SerializedName("saldo")
        double saldo;

        public String getFormattedPremi() {
            return NumberUtils.format(premi);
        }

        public String getFormattedSaldo() {
            return NumberUtils.format(saldo);
        }
    }

    @Data
    public class Tagihan implements Serializable {
        @SerializedName("periode")
        String periode;

        @SerializedName("pemakaian")
        double pemakaian;

        @SerializedName("meterAwal")
        double meterAwal;

        @SerializedName("meterAkhir")
        double meterAkhir;

        @SerializedName("nilaiTagihan")
        double nilaiTagihan;

        @SerializedName("penalty")
        double penalty;

        @SerializedName("admin")
        double admin;

        @SerializedName("total")
        double total;

        @SerializedName("tagihanLain")
        double tagihanLain;

        /**
         * @author : jesslyn
         * @notes : important part, required to update. please use this code.
         * change all <b> tariff</b> to <b> tarif</b>. There were 25 occurence files had changed
         * <code>
         */
        @SerializedName("tarif")
        String tarif;
        // </code>

        /**
         * @author Jesslyn
         * @note Case TDD 12445 - add new data
         *       1. add MSN
         *       2. add $curl_response->data->subscriberID
         *       3. add vendorAdmin
         */
        // <code>
                // extends OrderData
        // </code>

        @SerializedName("alamat")
        String alamat;

        @SerializedName("fee")
        double fee;

        @SerializedName("jatuhTempo")
        String jatuhTempo;

        @SerializedName("referensi")
        String referensi;

        @SerializedName("noref1")
        String noref1;

        @SerializedName("noref2")
        String noref2;

        @SerializedName("paket")
        String paket;

        public String getFormattedPemakaian() {
            return NumberUtils.format(pemakaian);
        }

        public String getFormattedMeterAwal() {
            return NumberUtils.format(meterAwal);
        }

        public String getFormattedMeterAkhir() {
            return NumberUtils.format(meterAkhir);
        }

        public String getFormattedNilaiTagihan() {
            return NumberUtils.format(nilaiTagihan);
        }

        public String getFormattedPenalty() {
            return NumberUtils.format(penalty);
        }

        public String getFormattedTagihanLain() {
            return NumberUtils.format(tagihanLain);
        }

        public String getFormattedFee() {
            return NumberUtils.format(fee);
        }
    }
}
