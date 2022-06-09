package com.qdi.rajapay.model.enums;

import com.google.gson.annotations.SerializedName;

public enum TransactionStatus {
    @SerializedName("W")
    WAITING("W"),

    @SerializedName("S")
    SUCCESS("S"),

    @SerializedName("C")
    CANCEL("C"),

    @SerializedName("F")
    FAILED("F"),

    @SerializedName("E")
    EXPIRED("E"),

    @SerializedName("R")
    REFUND("R"),

    @SerializedName("P")
    PROCESS("P"),

    /**
     * @author Jesslyn
     * @note Add new status for ManualAdvicePrepaid
     */
    // <code>
    @SerializedName("M")
    MANUAL("M"),
    // </code>

    @SerializedName("")
    ALL("");

    private final String text;

    TransactionStatus(final String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }

    /**
     * @author Jesslyn
     * @note parsing from string to enum
     * @param string
     * @return
     */
    // <code>
    public static TransactionStatus fromString(String string){
        if(string.equalsIgnoreCase(WAITING.text))
            return WAITING;
        else if (string.equalsIgnoreCase(SUCCESS.text))
            return SUCCESS;
        else if (string.equalsIgnoreCase(CANCEL.text))
            return CANCEL;
        else if (string.equalsIgnoreCase(FAILED.text))
            return FAILED;
        else if (string.equalsIgnoreCase(EXPIRED.text))
            return EXPIRED;
        else if (string.equalsIgnoreCase(REFUND.text))
            return REFUND;
        else if (string.equalsIgnoreCase(PROCESS.text))
            return PROCESS;
        else if (string.equalsIgnoreCase(MANUAL.text))
            return MANUAL;
        else
            throw new IllegalStateException("Unexpected value: " + string);
    }
    // </code>

    public String toDisplayString() {
        switch (this) {
            case WAITING: return "Menunggu";
            case SUCCESS: return "Berhasil";
            case CANCEL: return "Dibatalkan";
            case FAILED: return "Gagal";
            case EXPIRED: return "Kadaluarsa";
            case REFUND: return "Refund";
            case PROCESS: return "Sedang Proses";
            /**
             * @author Jesslyn
             * @note Add new status for ManualAdvicePrepaid
             */
            // <code>
            case MANUAL: return "Butuh Tindakan";
            // </code>
            case ALL: return "";
            default: return "";
        }
    }

    public static TransactionStatus fromDisplayString(String string){
        switch (string.toLowerCase()){
            case "menunggu" : return WAITING;
            case "berhasil" : return SUCCESS;
            case "dibatalkan" : return CANCEL;
            case "gagal" : return FAILED;
            case "kadaluarsa" : return EXPIRED;
            case "refund" : return REFUND;
            case "sedang proses" : return PROCESS;
            case "butuh tindakan" : return MANUAL;
            case "": return ALL;
            default: return ALL;
        }
    }
}
