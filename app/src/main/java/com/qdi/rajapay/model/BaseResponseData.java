package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class BaseResponseData {

    @SerializedName("idLogin")
    public String idLogin;

    @SerializedName("idUser")
    public String idUser;

    @SerializedName("token")
    public String token;

    @SerializedName("usedRefCode")
    public Object usedRefCode;

    @SerializedName("code")
    public Integer code;

    @SerializedName("type")
    public String type;

    @SerializedName("message")
    public String message;

    @SerializedName("description")
    public String description;
}
