package com.qdi.rajapay.model.enums;

import com.google.gson.annotations.SerializedName;

public enum PaymentMethod {

    @SerializedName("BANK TRANSFER")
    BANK_TRANSFER("BANK TRANSFER"),

    @SerializedName("VIRTUAL ACCOUNT")
    VIRTUAL_ACCOUNT("VIRTUAL ACCOUNT"),

    @SerializedName("DEPOSIT")
    DEPOSIT("DEPOSIT"),

    @SerializedName("TOPUPDEPOSITRETAIL")
    RETAIL("TOPUPDEPOSITRETAIL"),

    ;

    private final String text;

    PaymentMethod(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public String toDisplayString() {
        switch (this) {
            case DEPOSIT: return "Deposit Raja Pay";
            case BANK_TRANSFER: return "Transfer Bank";
            case VIRTUAL_ACCOUNT: return "Virtual Account";
            default: return "";
        }
    }
}
