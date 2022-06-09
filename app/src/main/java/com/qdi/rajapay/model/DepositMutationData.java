package com.qdi.rajapay.model;

import android.content.Context;

import com.qdi.rajapay.R;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositMutationData extends BaseResponseData implements Serializable {

    @SerializedName("initDeposit")
    double initDeposit;

    @SerializedName("depositIn")
    double depositIn;

    @SerializedName("depositOut")
    double depositOut;

    @SerializedName("depositFinal")
    double depositFinal;

    public JSONArray toJsonArray(Context context) throws JSONException {

        List<JSONObject> array = new ArrayList<>();
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator(',');
        formatSymbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###,###",formatSymbols);

        JSONObject beginingJsonObject = new JSONObject();
        beginingJsonObject.put("name", context.getResources().getString(R.string.order_deposit_mutation_beginning));
        beginingJsonObject.put("price", initDeposit);
        beginingJsonObject.put("price_str", "Rp. " + formatter.format(initDeposit));
        array.add(beginingJsonObject);

        JSONObject mutationInJsonObject = new JSONObject();
        mutationInJsonObject.put("name", context.getResources().getString(R.string.order_deposit_mutation_in));
        mutationInJsonObject.put("price", depositIn);
        mutationInJsonObject.put("price_str", "Rp. " + formatter.format(depositOut));
        array.add(mutationInJsonObject);

        JSONObject mutationOutJsonObject = new JSONObject();
        mutationOutJsonObject.put("name", context.getResources().getString(R.string.order_deposit_mutation_out));
        mutationOutJsonObject.put("price", depositOut);
        mutationOutJsonObject.put("price_str", "Rp. " + formatter.format(depositOut));
        array.add(mutationOutJsonObject);

        return new JSONArray(array.toString());
    }
}
