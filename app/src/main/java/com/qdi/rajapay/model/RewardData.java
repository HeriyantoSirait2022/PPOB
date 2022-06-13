package com.qdi.rajapay.model;

import com.google.gson.annotations.SerializedName;
import com.qdi.rajapay.model.enums.ProductType;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RewardData {

    @SerializedName("id")
    public Integer id;

    @SerializedName(value = "name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName(value = "point")
    public Integer point;

    @SerializedName("image")
    public String image;

    @SerializedName("is_pending")
    public Boolean is_pending;
}
