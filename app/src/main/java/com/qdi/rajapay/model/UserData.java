package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserData extends BaseResponseData {

    @SerializedName("noHp")
    String noHp;

    @SerializedName("email")
    String email;

    @SerializedName("shopName")
    String shopName;

    @SerializedName("accountType")
    String accountType;

    @SerializedName("numOfMembers")
    String numOfMembers;

    @SerializedName("role")
    String role;
}
