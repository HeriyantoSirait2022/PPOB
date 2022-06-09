package com.qdi.rajapay.model;

import com.qdi.rajapay.model.enums.ProductType;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PriceData {

    @SerializedName("id")
    public String id;

    @SerializedName(value = "idProductDetail", alternate = {"idProductMaster"})
    public String idProductDetail;

    @SerializedName("code")
    public String code;

    @SerializedName(value = "name", alternate = {"nameProduct"})
    public String name;

    @SerializedName("detail")
    public String detail;

    @SerializedName(value = "price", alternate = {"adminFee"})
    public Double price;

    @SerializedName(value = "agenPrice", alternate = {"agenFee"})
    public Double agenPrice;

    @SerializedName("type")
    public String productTypeStr;

    @SerializedName("image")
    public String image;

    public ProductType productType;
}
