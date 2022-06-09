package com.qdi.rajapay.model.enums;

import com.google.gson.annotations.SerializedName;

public enum NotificationType {
    @SerializedName("NOTIFICATION_TRANSACTION")
    TRANSACTION("NOTIFICATION_TRANSACTION"),

    @SerializedName("NOTIFICATION_TICKET")
    TICKET("NOTIFICATION_TICKET"),

    @SerializedName("NOTIFICATION_NEWS")
    NEWS("NOTIFICATION_NEWS");

    private final String text;

    NotificationType(final String text){
        this.text = text;
    }

    public static NotificationType fromString(String text){
        if(text.equalsIgnoreCase(TRANSACTION.text))
            return TRANSACTION;
        else if(text.equalsIgnoreCase(TICKET.text))
            return TICKET;
        else if(text.equalsIgnoreCase(NEWS.text))
            return NEWS;

        return null;
    }
}
