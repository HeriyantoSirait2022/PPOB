package com.qdi.rajapay.model;

import com.qdi.rajapay.model.enums.ProductType;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductData implements Serializable {

    @SerializedName("cdeProduct")
    public String cdeProduct;

    @SerializedName("nameProduct")
    public String nameProduct;

    @SerializedName("productType")
    public String productTypeStr;

    @SerializedName("image")
    public String image;

    public ProductType type;
}
