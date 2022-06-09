package com.qdi.rajapay.api.deserializer;

import com.qdi.rajapay.model.PriceData;
import com.qdi.rajapay.model.PricelistData;
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
import java.util.Arrays;
import java.util.List;

public class PricelistResponseDeserializer implements JsonDeserializer<ResponseData<PricelistData>> {

    private ProductType type;

    public PricelistResponseDeserializer(ProductType type){
        this.type = type;
    }

    @Override
    public ResponseData<PricelistData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        JsonObject response = root.getAsJsonObject("response");
        String key = getArrayKey();
        JsonArray jsonArray = response.getAsJsonArray(key);
        List<PriceData> list = new ArrayList<>();

        ProductType[] arrSinglePrice = { ProductType.BPJS, ProductType.MULTIFINANCE, ProductType.CELLULAR, ProductType.PGN, ProductType.PLN, ProductType.TELKOM, ProductType.TV};
        List<ProductType> singlePrice = Arrays.asList(arrSinglePrice)  ;

        if(singlePrice.contains(type)) {

            PriceData item = context.deserialize(response, PriceData.class);
            item.setProductType(type);
            list.add(item);

        } else {

            for (JsonElement element : jsonArray) {
                PriceData item = context.deserialize(element, PriceData.class);
                item.setProductType(type);
                list.add(item);
            }
        }

        PricelistData pricelistData = context.deserialize(response, PricelistData.class);
        pricelistData.setPriceList(list);
        ResponseData<PricelistData> responseData = new ResponseData<>(pricelistData);

        return responseData;
    }

    private String getArrayKey() {
        switch (type) {
            case DATA: return "arrDataPrice";
            case PULSA: return "arrPulsaPrice";
            case TOKENPLN: return "arrTokenPrice";
            case TOPUPGAMES: return "arrTopupGames";
            case VOUCHERGAMES: return "arrVoucherGames";
            case EMONEY: return "arrEmoney";
            case BANKTRANSFER: return "arrBankTransfer";

            case PDAM: return "arrPdamPrice";
            default: return "";
        }
    }
}
