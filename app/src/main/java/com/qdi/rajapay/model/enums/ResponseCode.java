package com.qdi.rajapay.model.enums;

import com.google.gson.annotations.SerializedName;

public enum ResponseCode {
    @SerializedName("")
    UNIDENTIFIED(""),

    @SerializedName("00")
    SUCCESS("00"),

    @SerializedName("89")
    PROCESS_89("89"),

    @SerializedName("90")
    PROCESS_90("90"),

    @SerializedName("9983")
    MANUAL_ADVICE("9983"),

    @SerializedName("0063")
    MANUAL_REFUND("0063"),

    /**
     * @author Jesslyn
     * @patch FR19022
     * @notes add response code for otomax
     */
    // <code>
    @SerializedName("P")
    OTOMAX_PENDING("P"),
    // </code>

    /**
     * @author Jesslyn
     * @note add code 68 for PDAM
     */
    @SerializedName("68")
    TRANSACTION_SUSPECTED("68");

    private final String text;

    ResponseCode(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ResponseCode fromString(String text){
        if(text.toUpperCase().equalsIgnoreCase(SUCCESS.text))
            return SUCCESS;
        else if(text.toUpperCase().equalsIgnoreCase(MANUAL_ADVICE.text))
            return MANUAL_ADVICE;
        else
            return UNIDENTIFIED;
    }

    public String getMsg(){
        switch (this){
            case SUCCESS:
                return "Transaksi anda sudah diterima!";
            case MANUAL_ADVICE:
                return "Transaksi perlu tindakan!";
            case MANUAL_REFUND:
                return "Transaksi dibatalkan!";
            case PROCESS_89:
                return "Transaksi sedang di proses!";
            case PROCESS_90:
                return "Transaksi sedang di proses!";
            case TRANSACTION_SUSPECTED:
                return "TRANSAKSI SUSPECT";
            case UNIDENTIFIED:
                return "Transaksi sudah terupdate";
            default:
                return "Transaksi sudah terupdate";
        }
    }
}
