package com.qdi.rajapay.api.deserializer;

import com.qdi.rajapay.model.ProductData;
import com.qdi.rajapay.model.ProductListData;
import com.qdi.rajapay.model.ResponseData;
import com.qdi.rajapay.model.enums.ProductType;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductResponseDeserializer implements JsonDeserializer<ResponseData<ProductListData>> {

    private ProductType type;

    public ProductResponseDeserializer(ProductType type){
        this.type = type;
    }

    @Override
    public ResponseData<ProductListData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        JsonObject response = root.getAsJsonObject("response");
        String key = getArrayKey();
        JsonArray jsonArray = response.getAsJsonArray(key);
        List<ProductData> list = new ArrayList<>();

        for(JsonElement element: jsonArray) {
            ProductData item = context.deserialize(element, ProductData.class);
            item.setType(type);
            list.add(item);
        }

        ProductListData productListData = context.deserialize(response, ProductListData.class);
        productListData.setProductList(list);
        ResponseData<ProductListData> responseData = new ResponseData<>(productListData);

        return responseData;
    }

    private String getArrayKey() {
        switch (type) {
            case PULSA: return "arrPrepaidPulsaProduct";
            case DATA: return "arrPrepaidDataProduct";
            case TOKENPLN: return "arrPrepaidPlnProduct";
            case TOPUPGAMES: return "arrPrepaidTopupGamesProduct";
            case VOUCHERGAMES: return "arrPrepaidVoucherGamesProduct";
            case EMONEY: return "arrPrepaidEmoneyProduct";
            case BANKTRANSFER: return "arrPrepaidBankTransferProduct";

            case CELLULAR: return "arrPostpaidCellProduct";
            case PLN: return "arrPostpaidPlnProduct";
            case PGN: return "arrPostpaidPgnProduct";
            case BPJS: return "arrPostpaidBpjsProduct";
            case TV: return "arrPostpaidTvProduct";
            case PDAM: return "arrPostpaidPdamProduct";
            case TELKOM: return "arrPostpaidTelkomProduct";
            case MULTIFINANCE: return "arrPostpaidMultifinProduct";
            default: return "";
        }
    }
}
