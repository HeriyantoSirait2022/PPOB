package com.qdi.rajapay.print;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.main_menu.water.WaterInputNoActivity;
import com.qdi.rajapay.model.BaseResponseData;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.OrderPrintData;
import com.qdi.rajapay.model.UserData;
import com.qdi.rajapay.utils.NumberUtils;
import com.google.gson.Gson;
import com.qid.objx.PrintObj;
import com.qid.objx.TwoCols;
import com.qid.printtooth.PrintTooth;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * @module 6.0 Kasir
 * @screen 6.18.5
 *
 * @module 5.1 Transaksi
 * @screen 5.1.7*
 */
public class PrintConfirmationActivity extends BaseActivity {
    Button print_now, change_sell_price;
    RecyclerView rv_preview;

    PrintDevicePreviewAdapter adapter;

    OrderData data;
    OrderPrintData printData;
    BluetoothDevice device_data;
    final int CHANGE_SELL_PRICE = 1;

    TransactionAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_confirmation);

        init_toolbar(getResources().getString(R.string.activity_title_confirmation_bill));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        print_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapPrint(printData);
            }
        });

        change_sell_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrintConfirmationActivity.this, PrintManageSellPriceActivity.class);
                intent.putExtra("data", data);
                startActivityForResult(intent,CHANGE_SELL_PRICE);
            }
        });
    }

    private void init() throws JSONException {
        print_now = findViewById(R.id.print_now);
        change_sell_price = findViewById(R.id.change_sell_price);

        rv_preview = findViewById(R.id.list_print);
        adapter = new PrintDevicePreviewAdapter();
        rv_preview.setAdapter(adapter);
        rv_preview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        data = (OrderData) getIntent().getSerializableExtra("data");
        device_data = (BluetoothDevice) getIntent().getParcelableExtra("device");

        api = new TransactionAPI(this, user_SP);
        getPrintData(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == CHANGE_SELL_PRICE){
            double agenPrice = data.getDoubleExtra("sell_price", 0);
            setAgenPriceTxn(agenPrice);
        }
    }

    private void getPrintData(boolean showModal) {
        if(showModal)
            show_wait_modal();

        api.getOrderDetailPrint(data, new APICallback.ItemCallback<OrderPrintData>() {
            @Override
            public void onItemResponseSuccess(OrderPrintData item, String message) {
                dismiss_wait_modal();
                printData = item;

                /**
                 * @authors : Jesslyn
                 * @notes : fixing issue pricing at the first time not updated after update harga
                 */
                // <code>
                data.getSellingData().setSellingPrice(printData.getSellingPrice());
                mapPreview(printData);
                // </code>
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                PrintConfirmationActivity.this.onAPIResponseFailure(error);
            }
        });
    }

    private void setAgenPriceTxn(double price) {
        /**
         * @authors : Jesslyn
         * @notes : fixing issue selling price not updated at invoice list
         *          fixing issue selling price not updated at PrintManageSellPriceActivity
         */
        // <code>
        // setup ManageSellPrice activity data
        data.getSellingData().setSellingPrice(price);

        // setup request data to server
        data.setAgenPrice(price);

        // setup print data
        printData.setSellingPrice(price);
        // </code>
        show_wait_modal();
        api.updateAgenPrice(data, new APICallback.ItemCallback<BaseResponseData>() {
            @Override
            public void onItemResponseSuccess(BaseResponseData item, String message) {
                dismiss_wait_modal();
                /**
                 * @author jesslyn
                 * @note fixing issue Admin and RP bayar not refresh after set harga jual
                 *       commenting setupPreview();
                 */
                // <code>
                getPrintData(false);
                // </code>
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                PrintConfirmationActivity.this.onAPIResponseFailure(error);
            }
        });
    }

    private void previewSingle() {
        Gson gson = new Gson();
        String userStr = user_SP.getString("user", "");
        UserData user =  gson.fromJson(userStr, UserData.class);

        List<TwoCols> list = new ArrayList<>();
        list.add(oneCols("", "LEFT"));
        list.add(oneCols(user.getShopName(), "CENTER"));
        list.add(oneCols("===================================", "CENTER"));
        /**
         * @authors : liao.mei
         * @notes : change to use cre_dtm on tbl_*_agen_txn
         */
        // <code>
        list.add(oneCols(shortDayToBahasa(monthToBahasa(printData.getPrintFormattedCreatedDate())), "LEFT"));
        // </code>
        list.add(oneCols("Order ID #" + data.getIdOrder(), "LEFT"));
        list.add(oneCols("===================================", "CENTER"));
        list.add(oneCols("TRANSAKSI:", "LEFT"));
        list.add(oneCols(data.getNmTxn() + " - " + mapTransactionDetail(printData), "LEFT"));
        list.add(oneCols(printData.getFormattedSellingPrice(), "RIGHT"));
        list.add(oneCols("-----------------------------------", "CENTER"));
        list.add(twoCols(R.string.print_device_total, printData.getFormattedSellingPrice()));
        list.add(oneCols("", "LEFT"));
        list.add(oneCols("RINCIAN", "LEFT"));
        // Rincian Pemesanan
        list.addAll(mapDetailList(printData));
        list.add(oneCols("===================================", "CENTER"));
        list.add(oneCols(getString(R.string.company_name), "CENTER"));

        adapter.setList(list);
    }

    private void previewMultiple() {
        Gson gson = new Gson();
        String userStr = user_SP.getString("user", "");
        UserData user =  gson.fromJson(userStr, UserData.class);
        List<TwoCols> list = new ArrayList<>();

        int i = 1;
        for(OrderPrintData.DetailTagihan item: printData.getPostpaidPLNDetailTagihan()) {
            list.add(oneCols("", "LEFT"));
            list.add(oneCols(user.getShopName(), "CENTER"));
            list.add(oneCols("===================================", "CENTER"));

            list.add(oneCols(shortDayToBahasa(monthToBahasa(printData.getPrintFormattedCreatedDate())), "LEFT"));

            list.add(oneCols("Order ID #" + data.getIdOrder(), "LEFT"));
            list.add(oneCols("===================================", "CENTER"));
            list.add(oneCols("TRANSAKSI:", "LEFT"));
            list.add(oneCols(data.getNmTxn() + " - " + mapTransactionDetail(printData), "LEFT"));
            list.add(oneCols(OrderData.toIdr(item.getTotal()), "RIGHT"));
            list.add(oneCols("-----------------------------------", "CENTER"));
            list.add(twoCols(R.string.print_device_total, OrderData.toIdr(item.getTotal()) ));
            list.add(oneCols("", "LEFT"));
            list.add(oneCols("RINCIAN", "LEFT"));
            // Rincian Pemesanan
            list.addAll(mapMultipleDetailList(printData, item));
            list.add(oneCols("===================================", "CENTER"));
            list.add(oneCols(getString(R.string.company_name), "CENTER"));

            if(i < data.getPostpaidPLNDetailTagihan().size()){
                list.add(oneCols("", "LEFT"));
                list.add(oneCols("", "LEFT"));
            }

            i++;
        }

        adapter.setList(list);
    }

    /**
     * @author Jesslyn
     * @note add exception to change error message
     */
    // <code>
    public void executePrint(PrintTooth printTooth){
        try{
            printTooth.execute();
        }catch (Exception e){
            displayToast(this, "Koneksi ke bluetooth gagal, silahkan coba lagi");
        }
    }
    // </code>

    private void printSingle() {
        show_wait_modal();

        String deviceName = device_data.getName();
        PrintObj printObj = printSingleData();
        PrintTooth printTooth = new PrintTooth(this, printObj, 32, deviceName);
        executePrint(printTooth);

        dismiss_wait_modal();
        startActivity(new Intent(PrintConfirmationActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void printMultiple() {
        show_wait_modal();

        String deviceName = device_data.getName();

        int i = 1;
        for(OrderPrintData.DetailTagihan item: printData.getPostpaidPLNDetailTagihan()) {
            PrintObj printObj = printMultipleData(item);
            final PrintTooth printTooth = new PrintTooth(getApplicationContext(), printObj, 32, deviceName);
            final int index = i;

            if(i == 1){
                executePrint(printTooth);
            }else{
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                executePrint(printTooth);

                                if(index == printData.getPostpaidPLNDetailTagihan().size()){
                                    dismiss_wait_modal();
                                    startActivity(new Intent(PrintConfirmationActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            }
                        },
                delay_time_for_print * (i - 1));
            }

            i++;
        }
    }

    private PrintObj printMultipleData(OrderPrintData.DetailTagihan item) {
        PrintObj printObj = new PrintObj();

        printObj.companyName = getString(R.string.company_name);

        Gson gson = new Gson();
        String userStr = user_SP.getString("user", "");
        UserData user =  gson.fromJson(userStr, UserData.class);

        printObj.agentName = user.getShopName();
        printObj.orderDate = shortDayToBahasa(monthToBahasa(data.getPrintFormattedDateOrder()));
        printObj.orderId = "Order ID #" + data.getIdOrder();
        printObj.total = OrderData.toIdr(item.getTotal());

        List<TwoCols> transList = new ArrayList<>();
        TwoCols twoCols = new TwoCols();
        twoCols.cols1 = data.getNmTxn() + " - " + mapTransactionDetail(printData);
        twoCols.cols2 = "";
        transList.add(twoCols);

        twoCols = new TwoCols();
        twoCols.cols1 = "";
        twoCols.cols2 = OrderData.toIdr(item.getTotal());;
        transList.add(twoCols);

        List<TwoCols> detailList = mapMultipleDetailList(printData, item);

        printObj.transList = transList;
        printObj.detailList = detailList;

        return printObj;
    }

    private PrintObj printSingleData() {
        PrintObj printObj = new PrintObj();

        printObj.companyName = getString(R.string.company_name);

        Gson gson = new Gson();
        String userStr = user_SP.getString("user", "");
        UserData user =  gson.fromJson(userStr, UserData.class);

        printObj.agentName = user.getShopName();
        printObj.orderDate = shortDayToBahasa(monthToBahasa(data.getPrintFormattedDateOrder()));
        printObj.orderId = "Order ID #" + data.getIdOrder();
        printObj.total = printData.getFormattedSellingPrice();

        List<TwoCols> transList = new ArrayList<>();
        TwoCols twoCols = new TwoCols();
        twoCols.cols1 = data.getNmTxn() + " - " + mapTransactionDetail(printData);
        twoCols.cols2 = "";
        transList.add(twoCols);

        twoCols = new TwoCols();
        twoCols.cols1 = "";
        twoCols.cols2 = printData.getFormattedSellingPrice();
        transList.add(twoCols);

        List<TwoCols> detailList = mapDetailList(printData);

        printObj.transList = transList;
        printObj.detailList = detailList;

        return printObj;
    }

    private String mapTransactionDetail(OrderPrintData data) {
        switch (data.getTypeTxn()) {
            case PREPAID_DATA: return data.getNoHp();
            case PREPAID_PULSA: return data.getNoHp();
            case PREPAID_PLN: return data.getNoCust();
            case PREPAID_TOPUPGAMES: return data.getNoHp();
            case PREPAID_VOUCHERGAMES: return data.getNoHp();
            case PREPAID_EMONEY: return data.getNoHp();
            case PREPAID_BANKTRANSFER: return data.getNoCust();
            case POSTPAID_CELL: return data.getNoHp();
            case POSTPAID_PLN: return data.getNoPln();
            case POSTPAID_PGN: return data.getNoPgn();
            case POSTPAID_BPJS: return data.getNoBpjs();
            case POSTPAID_PDAM: return data.getNoPdam();
            case POSTPAID_TV: return data.getNoTv();
            case POSTPAID_TELKOM: return data.getNoTelkom();
            case POSTPAID_MULTIFINANCE: return data.getNoContract();
            default: return "";
        }
    }

    private void mapPreview(OrderPrintData data) {
        switch (data.getTypeTxn()) {
            case POSTPAID_PLN: previewMultiple();
                break;
            case PREPAID_DATA:
            case PREPAID_PULSA:
            case PREPAID_PLN:
            case POSTPAID_CELL:
            case POSTPAID_PGN:
            case POSTPAID_BPJS:
            case POSTPAID_PDAM:
            case POSTPAID_TV:
            case POSTPAID_TELKOM:
            case POSTPAID_MULTIFINANCE:
            case PREPAID_TOPUPGAMES:
            case PREPAID_VOUCHERGAMES:
            case PREPAID_EMONEY:
            default: previewSingle(); break;
        }
    }

    private void mapPrint(OrderPrintData data) {
        switch (data.getTypeTxn()) {
            case POSTPAID_PLN: printMultiple();
                break;
            case PREPAID_DATA:
            case PREPAID_PULSA:
            case PREPAID_PLN:
            case POSTPAID_CELL:
            case POSTPAID_PGN:
            case POSTPAID_BPJS:
            case POSTPAID_PDAM:
            case POSTPAID_TV:
            case POSTPAID_TELKOM:
            case POSTPAID_MULTIFINANCE:
            case PREPAID_TOPUPGAMES:
            case PREPAID_VOUCHERGAMES:
            case PREPAID_EMONEY:
            default: printSingle(); break;
        }
    }

    private List<TwoCols> mapDetailList(OrderPrintData data) {
        switch (data.getTypeTxn()) {
            case PREPAID_DATA: return mapPrepaidData(data);
            case PREPAID_PULSA: return mapPrepaidPulsa(data);
            case PREPAID_PLN: return mapPrepaidTokenPLN(data);
            case PREPAID_TOPUPGAMES: return mapPrepaidTopupGames(data);
            case PREPAID_VOUCHERGAMES: return mapPrepaidVoucherGames(data);
            case PREPAID_EMONEY: return mapPrepaidEmoney(data);
            case PREPAID_BANKTRANSFER: return mapPrepaidBankTransfer(data);
            case POSTPAID_CELL: return mapPostpaidCellular(data);
            case POSTPAID_PGN: return mapPostpaidPGN(data);
            case POSTPAID_BPJS: return mapPostpaidBPJS(data);
            case POSTPAID_PDAM: return mapPostpaidPDAM(data);
            case POSTPAID_TV: return mapPostpaidTV(data);
            case POSTPAID_TELKOM: return mapPostpaidTelkom(data);
            case POSTPAID_MULTIFINANCE: return mapPostpaidMultifinance(data);
            default: return Collections.emptyList();
        }
    }

    private List<TwoCols> mapMultipleDetailList(OrderPrintData data, OrderPrintData.DetailTagihan item ) {
        switch (data.getTypeTxn()) {
            case POSTPAID_PLN: return mapPostpaidPLN(data, item);
            default: return Collections.emptyList();
        }
    }

    private List<TwoCols> mapPrepaidData(OrderPrintData data) {
        String typeData = data.getTypeData()==null ? "Regular" : data.getTypeData();
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(typeData)) result.add(twoCols(R.string.print_device_prepaid_data_type, typeData));
        if(!isNullOrEmpty(data.getDetailData())) result.add(twoCols(R.string.print_device_prepaid_data_detail, data.getDetailData().equals("null") ? "-" : data.getDetailData()));
        if(!isNullOrEmpty(data.getRef())) result.add(twoCols(R.string.print_device_no_reference, data.getRef()));

        return result;
    }

    private List<TwoCols> mapPrepaidPulsa(OrderPrintData data) {
        String typePulsa = data.getTypePulsa()==null ? "Regular" : data.getTypePulsa();
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(typePulsa)) result.add(twoCols(R.string.print_device_prepaid_pulsa_type, typePulsa));
        if(!isNullOrEmpty(data.getDetailPulsa())) result.add(twoCols(R.string.print_device_prepaid_pulsa_detail, data.getDetailPulsa()));
        if(!isNullOrEmpty(data.getRef())) result.add(twoCols(R.string.print_device_no_reference, data.getRef()));

        return result;
    }

    private List<TwoCols> mapPrepaidTokenPLN(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();
        /**
         * @author Dinda
         * @note case tdd 12445 - add new data
         *       1. add MSN
         *       2. add $curl_response->data->subscriberID
         *       3. add vendorAdmin
         */
        // <code-tdd-12445>


        /**
         * @author Eliza Sutantya
         * @patch FR19022 - P12511
         * @notes upgrading otomax v1 to v2, remove unused detail
         */
        // <code>
//        if(!isNullOrEmpty(data.getMsn()))
//            result.add(twoCols(R.string.print_device_prepaid_pln_no_meter, data.getMsn()));

        if(!isNullOrEmpty(data.getSubscriberId()))
            result.add(twoCols(R.string.print_device_prepaid_pln_id_pel, data.getSubscriberId()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getTarif()) || !isNullOrEmpty(data.getDaya()))
            result.add(twoCols(R.string.print_device_prepaid_pln_tarif_daya, data.getTarif() + " / " + data.getDaya() + " VA"));

//        if(!isNullOrEmpty(data.getRefNoTypeTxn()))
//            result.add(twoCols(R.string.print_device_prepaid_pln_no_ref, data.getRefNoTypeTxn()));

        /**
         * @author Jesslyn
         * @note remove condition check cause vendorAdmin and profit never null (if null, double set to 0.0)
         *       Also, totBullAmount never null because totBillAmount already saved when transaction-payment
         *       totBillAmount also possible had deduction from coupon code
         *       case - BDD 11022
         */
        // <code>
//        result.add(twoCols(R.string.print_device_prepaid_pln_rp_bayar, "Rp. " + NumberUtils.format(data.getTotBillAmount())));
        // </code>

//        if(!isNullOrEmpty(data.getBiayaMaterai()))
//            result.add(twoCols(R.string.print_device_prepaid_pln_meterai, "Rp. " + data.getBiayaMaterai()));

//        if(!isNullOrEmpty(data.getPpn()))
//            result.add(twoCols(R.string.print_device_prepaid_pln_ppn, "Rp. " + data.getPpn()));

//        if(!isNullOrEmpty(data.getPpj()))
//            result.add(twoCols(R.string.print_device_prepaid_pln_ppj, "Rp. " + data.getPpj()));

//        if(!isNullOrEmpty(data.getAngsuran()))
//            result.add(twoCols(R.string.print_device_prepaid_pln_angsuran, "Rp. " + data.getAngsuran()));

//        if(!isNullOrEmpty(data.getRpToken()))
//            result.add(twoCols(R.string.print_device_prepaid_pln_rp_stroom, "Rp. " + data.getRpToken()));

        if(!isNullOrEmpty(data.getKwh()))
            result.add(twoCols(R.string.print_device_prepaid_pln_jml_kwh, data.getKwh()));

        /**
         * @author Jesslyn
         * @note remove condition check cause vendorAdmin and profit never null (if null, double set to 0.0)
         *       Vendor admin contains profit (if payment successful) or estimation profit (if continue payment)
         *       @see case BDD 11023 / Modul5Controller
         */
        // <code>
//        result.add(twoCols(R.string.print_device_prepaid_pln_admin, "Rp. " + NumberUtils.format(data.getVendorAdmin())));
        // </code>

        if(!isNullOrEmpty(data.getTokenNumber()))
            result.add(twoCols(R.string.print_device_prepaid_pln_stroom_token, data.getTokenNumber()));

        if(!isNullOrEmpty(data.getInfoText())){
            result.add(twoCols(R.string.print_device_prepaid_pln_info, data.getInfoText()));
        }

        /**
         * @author : jesslyn
         * @notes : important part, required to update. please use this code
         * <code>
         */
        if(!isNullOrEmpty(data.getRef())) result.add(twoCols(R.string.print_device_no_reference, data.getRef()));
        // </code>

        // </code-tdd-12445>
        return result;
    }

    private List<TwoCols> mapPrepaidTopupGames(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getRef()))
            result.add(twoCols(R.string.print_device_sn_ref, data.getRef()));

        return result;
    }

    private List<TwoCols> mapPrepaidVoucherGames(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getRef()))
            result.add(twoCols(R.string.print_device_sn_ref, data.getRef()));

        return result;
    }

    private List<TwoCols> mapPrepaidEmoney(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getRef()))
            result.add(twoCols(R.string.print_device_sn_ref, data.getRef()));

        return result;
    }

    private List<TwoCols> mapPrepaidBankTransfer(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getNameProduct()))
            result.add(twoCols(R.string.print_device_prepaid_banktransfer_bank_name, data.getNameProduct()));

        if(!isNullOrEmpty(data.getNoCust()))
            result.add(twoCols(R.string.print_device_prepaid_banktransfer_account_no, data.getNoCust()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_prepaid_banktransfer_account_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getInfoText()))
            result.add(twoCols(R.string.print_device_prepaid_banktransfer_info, data.getInfoText()));

        return result;
    }

    private List<TwoCols> mapPostpaidCellular(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();
        /**
         * @author Jesslyn
         * @note Change format of print
         */
        // <code>
        if(!isNullOrEmpty(data.getTglBayar()))
            result.add(twoCols(R.string.print_device_tanggal_bayar, data.getTglBayar()));

        if(!isNullOrEmpty(data.getLayanan()))
            result.add(twoCols(R.string.print_device_layanan, data.getLayanan()));

        if(!isNullOrEmpty(data.getNoPelanggan()))
            result.add(twoCols(R.string.print_device_no_hp, data.getNoPelanggan()));

        if(!isNullOrEmpty(data.getNama()))
            result.add(twoCols(R.string.print_device_name, data.getNama()));

        if(!isNullOrEmpty(data.getReferensi()))
            result.add(twoCols(R.string.print_device_referensi, data.getReferensi()));

        if(!isNullOrEmpty(data.getPeriode()))
            result.add(twoCols(R.string.print_device_period, data.getPeriode()));

        if(!isNullOrEmpty(data.getLembarTagihan()))
            result.add(twoCols(R.string.print_device_lembar_tagih, data.getLembarTagihan()));

        if(!isNullOrEmpty(data.getTagihanStr()))
            result.add(twoCols(R.string.print_device_tagihan, data.getTagihanStr()));

        if(!isNullOrEmpty(data.getAdminStr()))
            result.add(twoCols(R.string.print_device_biaya_admin, data.getAdminStr()));

        if(!isNullOrEmpty(data.getTotalStr()))
            result.add(twoCols(R.string.print_device_total_detail, data.getTotalStr()));

        if(!isNullOrEmpty(data.getInfoText())){
            result.add(twoCols(R.string.print_device_prepaid_info, data.getInfoText()));
        }
        // </code>
        return result;
    }

    private List<TwoCols> mapPostpaidPLN(OrderPrintData data, OrderPrintData.DetailTagihan item) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getNoPln() ))
            result.add(twoCols(R.string.print_device_postpaid_pln_id_pel, data.getNoPln()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getTarif()) || !isNullOrEmpty(data.getDaya()))
            result.add(twoCols(R.string.print_device_prepaid_pln_tarif_daya, data.getTarif() + " / " + data.getDaya() + " VA"));

        if(!isNullOrEmpty(data.getRefNoTypeTxn()))
            result.add(twoCols(R.string.print_device_prepaid_pln_no_ref, data.getRefNoTypeTxn()));

        result.add(twoCols(R.string.print_device_postpaid_pln_rp_tag_pln, OrderData.toIdr(item.getNilaiTagihan()) ));

        result.add(twoCols(R.string.print_device_denda, OrderData.toIdr(item.getDenda()) ));

        result.add(twoCols(R.string.print_device_prepaid_pln_admin, OrderData.toIdr(item.getAdmin()) ));

        result.add(twoCols(R.string.print_device_postpaid_pln_total, OrderData.toIdr(item.getTotal()) ));

        if(!isNullOrEmpty(item.getPeriode()))
            result.add(twoCols(R.string.print_device_postpaid_pln_bl_th, item.getPeriode()));

        if(!isNullOrEmpty(item.getMeterAwal()) && !isNullOrEmpty(item.getMeterAkhir()))
            result.add(twoCols(R.string.print_device_postpaid_pln_stand_meter, item.getMeterAwal() + " - " + item.getMeterAkhir()));

        result.add(twoCols(R.string.print_device_postpaid_info_1, getStr(R.string.print_device_postpaid_pln_sah) ));

        if(!isNullOrEmpty(data.getLembarTagihanSisa())){
            Integer tagSisa = Integer.parseInt(data.getLembarTagihanSisa());

            if(tagSisa > 0)
                result.add(twoCols(R.string.print_device_postpaid_info_2, "Anda masih memiliki sisa tunggakan " + data.getLembarTagihanSisa() + " bulan"  ));
        }

        if(!isNullOrEmpty(data.getInfoText()))
            result.add(twoCols(R.string.print_device_postpaid_pln_info, data.getInfoText() ));

        return result;
    }

    private List<TwoCols> mapPostpaidPGN(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_idpel, data.getNoPgn()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getAlamat()))
            result.add(twoCols(R.string.print_device_address, data.getAlamat()));

        if(!isNullOrEmpty(data.getPeriode()))
            result.add(twoCols(R.string.print_device_period, data.getPeriode()));

        if(!isNullOrEmpty(data.getStandAwal()))
            result.add(twoCols(R.string.print_device_postpaid_pgn_stand_awal, data.getStandAwal()));

        if(!isNullOrEmpty(data.getStandAkhir()))
            result.add(twoCols(R.string.print_device_postpaid_pgn_stand_akhir, data.getStandAkhir()));

        if(!isNullOrEmpty(data.getPemakaian()))
            result.add(twoCols(R.string.print_device_pemakaian, data.getPemakaian()));

        if(!isNullOrEmpty(data.getTglBayar()))
            result.add(twoCols(R.string.print_device_tanggal_bayar, data.getTglBayar()));

        if(!isNullOrEmpty(data.getPropertyRef()))
            result.add(twoCols(R.string.print_device_prepaid_pln_no_ref, data.getPropertyRef()));

        if(!isNullOrEmpty(data.getTagihanStr()))
            result.add(twoCols(R.string.print_device_tagihan, data.getTagihanStr()));

        if(!isNullOrEmpty(data.getAdminStr()))
            result.add(twoCols(R.string.print_device_biaya_admin, data.getAdminStr()));

        if(!isNullOrEmpty(data.getTotalStr()))
            result.add(twoCols(R.string.print_device_total_detail, data.getTotalStr()));

        return result;
    }

    private List<TwoCols> mapPostpaidBPJS(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getNoRef()))
            result.add(twoCols(R.string.print_device_no_ref, data.getNoRef()));

        if(data.getDtOrder() != null)
            result.add(twoCols(R.string.print_device_tanggal, data.getNormalFormattedDateOrder() ));

        if(!isNullOrEmpty(data.getNoBpjs()))
            result.add(twoCols(R.string.print_device_no_bpjs, data.getNoBpjs()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getJumlahPeriode()))
            result.add(twoCols(R.string.print_device_postpaid_bpjs_jumlah_periode, data.getJumlahPeriode() + " Bulan"));

        if(!isNullOrEmpty(data.getJumlahPeserta()))
            result.add(twoCols(R.string.print_device_postpaid_bpjs_jumlah_peserta, data.getJumlahPeserta() + " Orang"));

        if(!isNullOrEmpty(data.getTagihanStr()))
            result.add(twoCols(R.string.print_device_tagihan, data.getTagihanStr()));

        if(!isNullOrEmpty(data.getAdminStr()))
            result.add(twoCols(R.string.print_device_biaya_admin, data.getAdminStr()));

        if(!isNullOrEmpty(data.getTotalStr()))
            result.add(twoCols(R.string.print_device_total_detail, data.getTotalStr()));

        if(!isNullOrEmpty(data.getInfoText()))
            result.add(twoCols(R.string.print_device_prepaid_info, data.getInfoText()));

        /*
        for(OrderPrintData.DetailPeserta item: data.getPostpaidBPJSDetailPeserta()) {
            if(!isNullOrEmpty(item.getNoPeserta())) result.add(twoCols(R.string.print_device_postpaid_bpjs_no_peserta, item.getNoPeserta()));
            if(!isNullOrEmpty(item.getNama())) result.add(twoCols(R.string.print_device_name, item.getNama()));
            /**
             * @author : Maria Florencia CC Jesslyn
             * @notes : remove premi approve Mr. Yo
             *
            // <code>
            // if(!isNullOrEmpty(item.getFormattedPremi())) result.add(twoCols(R.string.print_device_postpaid_bpjs_premi, item.getFormattedPremi()));
            // </code>
            if(!isNullOrEmpty(item.getFormattedSaldo())) result.add(twoCols(R.string.print_device_postpaid_bpjs_saldo, item.getFormattedSaldo()));
        }
        */

        return result;
    }

    private List<TwoCols> mapPostpaidPDAM(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(data.getDtOrder() != null)
            result.add(twoCols(R.string.print_device_tanggal, data.getNormalFormattedDateOrder() ));

        if(!isNullOrEmpty(data.getNoPdam()))
            result.add(twoCols(R.string.print_device_no_pdam, data.getNoPdam()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getAlamat()))
            result.add(twoCols(R.string.print_device_address, data.getAlamat()));

        if(!isNullOrEmpty(data.getLayanan()))
            result.add(twoCols(R.string.print_device_layanan, data.getLayanan()));

        if(!isNullOrEmpty(data.getTarif()))
            result.add(twoCols(R.string.print_device_tarif, data.getTarif()));

        if(!isNullOrEmpty(data.getPeriode()))
            result.add(twoCols(R.string.print_device_period, data.getPeriode()));

        if(!isNullOrEmpty(data.getMeterAwal()) && !isNullOrEmpty(data.getMeterAkhir() ))
            result.add(twoCols(R.string.print_device_postpaid_pdam_stand_meter, data.getMeterAwal() + " - " + data.getMeterAkhir() ));

        if(!isNullOrEmpty(data.getPemakaian()))
            result.add(twoCols(R.string.print_device_postpaid_pdam_pemakaian, data.getPemakaian()));

        if(!WaterInputNoActivity.isPdamCh1(data.getCdeProduct())){
            // Channel 2
            if(!isNullOrEmpty(data.getAccountNumber()))
                result.add(twoCols(R.string.print_device_nomor_account, data.getAccountNumber()));

            if(!isNullOrEmpty(data.getRefNoTypeTxn()))
                result.add(twoCols(R.string.print_device_no_ref, data.getRefNoTypeTxn() ));

            if(!isNullOrEmpty(data.getPdamRef()))
                result.add(twoCols(R.string.print_device_postpaid_pdam_ref, data.getPdamRef() ));
        }

        if(!isNullOrEmpty(data.getTagihanStr()))
            result.add(twoCols(R.string.print_device_tagihan, data.getTagihanStr()));

        if(!isNullOrEmpty(data.getDendaStr()))
            result.add(twoCols(R.string.print_device_denda, data.getDendaStr()));

        if(!isNullOrEmpty(data.getTagihanLainStr()))
            result.add(twoCols(R.string.print_device_postpaid_pdam_tagihan_lain, data.getTagihanLainStr()));

        if(!isNullOrEmpty(data.getAdminStr()))
            result.add(twoCols(R.string.print_device_biaya_admin, data.getAdminStr()));

        if(!isNullOrEmpty(data.getTotalStr()))
            result.add(twoCols(R.string.print_device_total_detail, data.getTotalStr()));

        if(!isNullOrEmpty(data.getTerbilang()))
            result.add(twoCols(R.string.print_device_terbilang, data.getTerbilang()));

        if(!isNullOrEmpty(data.getInfoText())){
            result.add(twoCols(R.string.print_device_prepaid_info, data.getInfoText()));
        }

        /*
        for(OrderPrintData.Tagihan item: data.getPostpaidTagihan()) {
            if(!isNullOrEmpty(item.getPeriode())) result.add(twoCols(R.string.print_device_period, item.getPeriode()));
            if(!isNullOrEmpty(item.getFormattedPemakaian())) result.add(twoCols(R.string.print_device_pemakaian, item.getFormattedPemakaian()));
            if(!isNullOrEmpty(item.getFormattedMeterAwal())) result.add(twoCols(R.string.print_device_postpaid_pdam_meter_awal, item.getFormattedMeterAwal()));
            if(!isNullOrEmpty(item.getFormattedMeterAkhir())) result.add(twoCols(R.string.print_device_postpaid_pdam_meter_akhir, item.getFormattedMeterAkhir()));
            /**
             * @author : Maria Florencia CC Jesslyn
             * @notes : remove Tagihan
             *          remove Tagihan lain
             *          remove Fee
             *          confirm Mr. Yo
            // <code>
            // if(!isNullOrEmpty(item.getFormattedNilaiTagihan())) result.add(twoCols(R.string.print_device_nilai_tagihan, item.getFormattedNilaiTagihan()));
            // </code>
            if(!isNullOrEmpty(item.getFormattedPenalty())) result.add(twoCols(R.string.print_device_postpaid_pdam_penalty, item.getFormattedPenalty()));
            // <code>
            // if(!isNullOrEmpty(item.getFormattedTagihanLain())) result.add(twoCols(R.string.print_device_postpaid_pdam_tagihan_lain, item.getFormattedTagihanLain()));
            // </code>
            if(!isNullOrEmpty(item.getTarif())) result.add(twoCols(R.string.print_device_tarif, item.getTarif()));
            if(!isNullOrEmpty(item.getAlamat())) result.add(twoCols(R.string.print_device_address, item.getAlamat()));
            // <code>
            // if(!isNullOrEmpty(item.getFormattedFee())) result.add(twoCols(R.string.print_device_fee, item.getFormattedFee()));
            // </code>
        }
        */

        return result;
    }

    private List<TwoCols> mapPostpaidTV(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_id_pel, data.getNoTv()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getJumlahTagihan()))
            result.add(twoCols(R.string.print_device_jumlah_tagihan, data.getJumlahTagihan()));

        if(!isNullOrEmpty(data.getTagihanStr()))
            result.add(twoCols(R.string.print_device_tagihan, data.getTagihanStr()));

        if(!isNullOrEmpty(data.getAdminStr()))
            result.add(twoCols(R.string.print_device_biaya_admin, data.getAdminStr()));

        if(!isNullOrEmpty(data.getTotalStr()))
            result.add(twoCols(R.string.print_device_total_detail, data.getTotalStr()));

        if(data.getPostpaidTagihan().size() > 0) {
            int i = 1;

            for(OrderPrintData.Tagihan item: data.getPostpaidTagihan()) {
                result.add(twoCols(R.string.print_device_period, OrderData.isNullThenConvert( item.getPeriode() ), " " + i ));
                result.add(twoCols(R.string.print_device_jatuh_tempo, OrderData.isNullThenConvert(item.getJatuhTempo() )));

                /**
                 * @authors : Maria Florencia CC Jesslyn
                 * @notes : remove NilaiTagihan
                 *          remove Fee
                 *          confirm Mr. Yo
                 */
                // <code>
                // if(!isNullOrEmpty(item.getFormattedNilaiTagihan())) result.add(twoCols(R.string.print_device_nilai_tagihan, item.getFormattedNilaiTagihan()));
                // </code>
                result.add(twoCols(R.string.print_device_postpaid_tv_no_referensi, OrderData.isNullThenConvert(item.getReferensi()) ));
                result.add(twoCols(R.string.print_device_postpaid_tv_no_referensi_1, OrderData.isNullThenConvert(item.getNoref1()) ));
                result.add(twoCols(R.string.print_device_postpaid_tv_no_referensi_2, OrderData.isNullThenConvert(item.getNoref2()) ));
                result.add(twoCols(R.string.print_device_postpaid_tv_paket, OrderData.isNullThenConvert(item.getPaket()) ));
                // <code>
                // if(!isNullOrEmpty(item.getFormattedFee())) result.add(twoCols(R.string.print_device_fee, item.getFormattedFee()));
                // </code>
                i++;
            }
        }

        return result;
    }

    private List<TwoCols> mapPostpaidTelkom(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(data.getDtOrder() != null)
            result.add(twoCols(R.string.print_device_tanggal, data.getNormalFormattedDateOrder() ));

        if(!isNullOrEmpty(data.getNoTelkom()))
            result.add(twoCols(R.string.print_device_no_pdam, data.getNoTelkom()));

        if(!isNullOrEmpty(data.getNmCust()))
            result.add(twoCols(R.string.print_device_name, data.getNmCust()));

        if(!isNullOrEmpty(data.getNmTxn()))
            result.add(twoCols(R.string.print_device_layanan, data.getNmTxn()));

        result.add(twoCols(R.string.print_device_postpaid_telkom_divre_datel,  OrderData.isNullThenConvert(data.getDivre()) + " / " +  OrderData.isNullThenConvert(data.getDatel()) ));

        if(!isNullOrEmpty(data.getReff()))
            result.add(twoCols(R.string.print_device_ref, data.getReff()));

        /*
        if(!isNullOrEmpty(data.getPeriode()))
            result.add(twoCols(R.string.print_device_period, data.getPeriode()));
         */

        if(!isNullOrEmpty(data.getTagihanStr()))
            result.add(twoCols(R.string.print_device_tagihan, data.getTagihanStr()));

        if(!isNullOrEmpty(data.getAdminStr()))
            result.add(twoCols(R.string.print_device_biaya_admin, data.getAdminStr()));

        if(!isNullOrEmpty(data.getTotalStr()))
            result.add(twoCols(R.string.print_device_total_detail, data.getTotalStr()));

        int i = 1;
        for(OrderPrintData.Tagihan item: data.getPostpaidTagihan()) {
            if(!isNullOrEmpty(item.getPeriode()))
                result.add(twoCols(R.string.print_device_period, item.getPeriode(), " " + i));
            /**
             * @author : Jesslyn
             * @notes : remove Nilai Tagihan
             *          remove Fee
             *          Confirm Mr. Yo
             */
            // <code>
            // if(!isNullOrEmpty(item.getFormattedNilaiTagihan())) result.add(twoCols(R.string.print_device_nilai_tagihan, item.getFormattedNilaiTagihan()));
            // if(!isNullOrEmpty(item.getFormattedFee())) result.add(twoCols(R.string.print_device_fee, item.getFormattedFee()));
            // </code>
            i++;
        }

        if(!isNullOrEmpty(data.getInfoText())){
            result.add(twoCols(R.string.print_device_prepaid_info, data.getInfoText()));
        }
        return result;
    }

    /**
     * @authors : Jesslyn
     * @notes : Modified detail list for printing (following FSD)
     */

    // <code>
    private List<TwoCols> mapPostpaidMultifinance(OrderPrintData data) {
        List<TwoCols> result = new ArrayList<>();

        if(!isNullOrEmpty(data.getNmTxn()))
            result.add(twoCols(R.string.print_device_layanan, data.getNmTxn()));

        if(data.getCdeProduct().toUpperCase().equals("FNMEGA") || data.getCdeProduct().toUpperCase().equals("FNMAF") ){
            if(!isNullOrEmpty(data.getProvider()))
                result.add(twoCols(R.string.print_device_postpaid_multifinance_provider, data.getProvider()));

            if(!isNullOrEmpty(data.getNamaCabang()))
                result.add(twoCols(R.string.print_device_postpaid_multifinance_nama_cabang, data.getNamaCabang()));

            if(!isNullOrEmpty(data.getNoContract()))
                result.add(twoCols(R.string.print_device_id_pel, data.getNoContract()));

            if(!isNullOrEmpty(data.getNmCust()))
                result.add(twoCols(R.string.print_device_name, data.getNmCust()));

            result.add(twoCols(R.string.print_device_address, OrderData.isNullThenConvert(data.getAlamat()) ));

            result.add(twoCols(R.string.print_device_postpaid_multifinance_nama_barang, OrderData.isNullThenConvert(data.getNamaBarang()) ));

            result.add(twoCols(R.string.print_device_postpaid_multifinance_no_rangka, OrderData.isNullThenConvert(data.getNoRangka()) ));

            result.add(twoCols(R.string.print_device_postpaid_multifinance_no_polisi, OrderData.isNullThenConvert(data.getNoPolisi()) ));

            result.add(twoCols(R.string.print_device_postpaid_multifinance_tenor, OrderData.isNullThenConvert(data.getTenor()) ));
        }else{
            // FNWOMD
            if(!isNullOrEmpty(data.getNoContract()))
                result.add(twoCols(R.string.print_device_id_pel, data.getNoContract()));

            if(!isNullOrEmpty(data.getNmCust()))
                result.add(twoCols(R.string.print_device_name, data.getNmCust()));

            if(!isNullOrEmpty(data.getJatuhTempo()))
                result.add(twoCols(R.string.print_device_jatuh_tempo, data.getJatuhTempo()));

            if(!isNullOrEmpty(data.getAngsuranKe()))
                result.add(twoCols(R.string.print_device_postpaid_multifinance_angsuran_ke, data.getAngsuranKe()));
        }

        if(!isNullOrEmpty(data.getNoRef()))
            result.add(twoCols(R.string.print_device_no_ref, data.getNoRef()));

        if(!isNullOrEmpty(data.getTagihanStr()))
            result.add(twoCols(R.string.print_device_tagihan, data.getTagihanStr()));

        if(!isNullOrEmpty(data.getDendaStr()))
            result.add(twoCols(R.string.print_device_denda, data.getDendaStr()));

        if(!isNullOrEmpty(data.getAdminStr()))
            result.add(twoCols(R.string.print_device_biaya_admin, data.getAdminStr()));

        if(!isNullOrEmpty(data.getTotalStr()))
            result.add(twoCols(R.string.print_device_total_detail, data.getTotalStr()));

        /*
        if(!isNullOrEmpty(data.getNamaCabang())) result.add(twoCols(R.string.print_device_postpaid_multifinance_nama_cabang, data.getNamaCabang()));
        if(!isNullOrEmpty(data.getProvider())) result.add(twoCols(R.string.print_device_postpaid_multifinance_provider, data.getProvider()));
        if(!isNullOrEmpty(data.getNamaBarang())) result.add(twoCols(R.string.print_device_postpaid_multifinance_nama_barang, data.getNamaBarang()));
        if(!isNullOrEmpty(data.getNoPolisi())) result.add(twoCols(R.string.print_device_postpaid_multifinance_no_polisi, data.getNoPolisi()));

        if(!isNullOrEmpty(data.getTenor())) result.add(twoCols(R.string.print_device_postpaid_multifinance_tenor, data.getTenor()));
        /**
         * @authors : Cherry
         * @notes : Change penalty to double cause getPenalty required to be converted to String to make it compatible with isNullOrEmpty function at BaseActivity
         */
        // <code>
        // if( data.getPenalty() != null ) result.add(twoCols(R.string.print_device_postpaid_multifinance_penalty, data.getPenalty().toString() ));
        // </code>

        /**
         * @author : Jesslyn
         * @notes :  remove Fee
         *           Confirm Mr. Yo
         */
        // <code>
        // if(!isNullOrEmpty(data.getFormattedFee())) result.add(twoCols(R.string.print_device_fee, data.getFormattedFee()));
        // </code>
        // if(!isNullOrEmpty(data.getPropertyRef())) result.add(twoCols(R.string.print_device_no_reference, data.getPropertyRef()));

        return result;
    }
    // </code>

    private TwoCols oneCols(String cols, String alignment) {
        TwoCols twoCols = new TwoCols();
        twoCols.cols1 = cols;
        twoCols.cols2 = alignment;
        return twoCols;
    }

    private TwoCols twoCols(int stringId, String cols2) {
        TwoCols twoCols = new TwoCols();
        twoCols.cols1 = getString(stringId);
        twoCols.cols2 = "" + cols2;
        return twoCols;
    }

    private TwoCols twoCols(int stringId, String cols2, String cols1) {
        TwoCols twoCols = new TwoCols();
        twoCols.cols1 = getString(stringId) + cols1;
        twoCols.cols2 = "" + cols2;
        return twoCols;
    }
}