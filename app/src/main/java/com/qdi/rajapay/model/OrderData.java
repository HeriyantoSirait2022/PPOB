package com.qdi.rajapay.model;

import android.content.Context;
import android.util.Pair;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.water.WaterInputNoActivity;
import com.qdi.rajapay.model.enums.PaymentMethod;
import com.qdi.rajapay.model.enums.ProductType;
import com.qdi.rajapay.model.enums.ResponseCode;
import com.qdi.rajapay.model.enums.TransactionStatus;
import com.qdi.rajapay.model.enums.TransactionType;
import com.qdi.rajapay.utils.NumberUtils;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderData extends BaseResponseData implements Serializable {

    @SerializedName("idOrder")
    String idOrder;

    @SerializedName("dtOrder")
    Date dtOrder;

    /**
     * @authors : Jacqueline
     * @notes : add refund reason variable
     */
    // <code>
    @SerializedName("refundReas")
    String refundReason;
    // </code>

    /**
     * @authors : liao.mei
     * @notes : new serialized name for handling data from tbl_[postpaid|prepaid]_agent_txn. used in 6.18.2
     */
    // <code>
    @SerializedName("idBill")
    String idBill;

    @SerializedName("dtBill")
    Date dtBill;
    // </code>

    @SerializedName("status")
    TransactionStatus status;

    @SerializedName("idTxnAgen")
    String idTxnAgen;

    @SerializedName("nmTxn")
    String nmTxn;

    @SerializedName("dtlTxn")
    String dtlTxn;

    @SerializedName("typeTxn")
    TransactionType typeTxn;

    @SerializedName("nameProduct")
    String nameProduct;

    @SerializedName("cdeProduct")
    String cdeProduct;

    @SerializedName("image")
    String image;

    @SerializedName("adminFee")
    double adminFee;

    /**
     * @author Jesslyn
     * @note add profit to calculate vendor_admin_fee + profit (admin + selling price from Rajapay)
     */
    // <code>
    @SerializedName("profit")
    double profit;
    // </code>

    @SerializedName("agenFee")
    double agenFee;

    @SerializedName("agenStatus")
    String agenStatus;

    /**
     * @author Jesslyn
     * @note add Lembar Tagih for Postpaid Cellular
     */
    // <code>
    @SerializedName("totBill")
    int totBill;
    // </code>

    @SerializedName("minPayment")
    double minPayment;

    @SerializedName("totBillAmount")
    double totBillAmount;

    @SerializedName("billAmount")
    double billAmount;

    @SerializedName("agenPrice")
    double agenPrice;

    OrderSellingData sellingData;

    @SerializedName("noHp")
    String noHp;

    @SerializedName("couponAmount")
    double couponAmount;

    @SerializedName("cdeCoupon")
    String cdeCoupon;

    @SerializedName("paymentMtd")
    PaymentMethod paymentMtd;

    @SerializedName("noRef")
    String noRef;

    @SerializedName("idRef")
    String idRef;

    @SerializedName("sn")
    String sn;

    @SerializedName("ref")
    String ref;

    @SerializedName("refId")
    String refId;

    @SerializedName("noCust")
    String noCust;

    //prepaid token pln
    @SerializedName("nmCust")
    String nmCust;

    @SerializedName("tarif")
    String tarif;

    @SerializedName("daya")
    String daya;

    @SerializedName("kwh")
    String kwh;

    @SerializedName("tokenNumber")
    String tokenNumber;

    //postpaid pln
    @SerializedName("lembarTagihanTotal")
    String lembarTagihanTotal;

    @SerializedName("lembarTagihan")
    String lembarTagihan;

    @SerializedName("lembarTagihanSisa")
    String lembarTagihanSisa;

    @SerializedName("noPln")
    String noPln;

    /**
     * @author : jesslyn
     * @notes : changes detailTagihan to detilTagihan cause response from server was detilTagihan
     * <code>
     */
    @SerializedName(value = "detailTagihan", alternate = "detilTagihan")
    List<OrderPrintData.DetailTagihan> detilTagihan;
    // </code>

    /**
     * @author Jesslyn
     * @note 1. add ResponseCode for PrepaidPLN
     *       2. add responseMessage for additional info only
     */
    // <code>
    @SerializedName("responseCode")
    ResponseCode responseCode;

    @SerializedName("responseMessage")
    String responseMesssage;
    // </code>

    //postpaid pgn
    @SerializedName("alamat")
    String alamat;

    @SerializedName("standAwal")
    String standAwal;

    @SerializedName("standAkhir")
    String standAkhir;

    @SerializedName("pemakaian")
    String pemakaian;

    @SerializedName("noPgn")
    String noPgn;

    //postpaid bpjs
    @SerializedName("namaCabang")
    String namaCabang;

    @SerializedName("jumlahPeriode")
    String jumlahPeriode;

    @SerializedName("jumlahPeserta")
    String jumlahPeserta;

    @SerializedName("detailPeserta")
    List<OrderPrintData.DetailPeserta> detailPeserta;

    @SerializedName("noBpjs")
    String noBpjs;

    //postpaid pdam
    @SerializedName("jumlahTagihan")
    String jumlahTagihan;

    @SerializedName("reff")
    String reff;


    @SerializedName("noPdam")
    String noPdam;

    //postpaid tv

    @SerializedName("noTv")
    String noTv;

    //postpaid telkom
    @SerializedName("kodeArea")
    String kodeArea;

    @SerializedName("divre")
    String divre;

    @SerializedName("datel")
    String datel;

    @SerializedName("tagihan")
    List<OrderPrintData.Tagihan> tagihan;

    @SerializedName("noTelkom")
    String noTelkom;

    //postpaid multifinance
    @SerializedName("noContract")
    String noContract;

    @SerializedName("jatuhTempo")
    String jatuhTempo;

    @SerializedName("angsuranKe")
    String angsuranKe;

    /**
     * @authors : Jesslyn
     * @notes : new serialable for FNMAF, FNMEGA 6.18.5
     *
     * @updated-by : Cherry
     * @notes : change penalty object from String to Double
     */

    // <code>
    @SerializedName("namaBarang")
    String namaBarang;

    @SerializedName("noPolisi")
    String noPolisi;

    @SerializedName("tenor")
    String tenor;

    @SerializedName("penalty")
    Double penalty;
    // </code>

    @SerializedName("fee")
    double fee;

    //postpaid multifinance MEGA
    @SerializedName("provider")
    String provider;

    @SerializedName("noRangka")
    String noRangka;

    @SerializedName("lastPaidPeriod")
    String lastPaidPeriod;

    @SerializedName("lastPaidDueDate")
    String lastPaidDueDate;

    @SerializedName("itemName")
    String itemName;

    @SerializedName("noPol")
    String noPol;

    /**
     * @author Jesslyn
     * @note Case TDD 12445 - add new data
     *       1. add MSN
     *       2. add $curl_response->data->subscriberID
     *       3. add vendorAdmin
     */
    // <code>
    @SerializedName("msn")
    String msn;

    @SerializedName("subscriberID")
    String subscriberId;

    @SerializedName("biayaMaterai")
    String biayaMaterai;

    @SerializedName("ppn")
    String ppn;

    @SerializedName("ppj")
    String ppj;

    @SerializedName("angsuran")
    String angsuran;

    @SerializedName("infoText")
    String infoText;

    @SerializedName("vendorAdmin")
    double vendorAdmin;

    @SerializedName("rpToken")
    String rpToken;

    @SerializedName("vendorTotalTagih")
    double vendorTotalTagih;

    @SerializedName("hashId")
    String hashId;
    // </

    /**
     * @author Jesslyn
     * @note For POSTPAIDCELL
     */
    // <code>
    @SerializedName("tglBayar")
    String tglBayar;

    @SerializedName("layanan")
    String layanan;

    @SerializedName("noPelanggan")
    String noPelanggan;

    @SerializedName("nama")
    String nama;

    @SerializedName("periode")
    String periode;

    @SerializedName("referensi")
    String referensi;

    @SerializedName("tagihanStr")
    String tagihanStr;

    @SerializedName("dendaStr")
    String dendaStr;

    @SerializedName("tagihanLainStr")
    String tagihanLainStr;

    @SerializedName("adminStr")
    String adminStr;

    @SerializedName("totalStr")
    String totalStr;
    // </code>

    /**
     * @author Jesslyn
     * @note add function for PDAM
     */
    // <code>
    @SerializedName("denda")
    double denda;

    @SerializedName("tagihanLain")
    double tagihanLain;

    @SerializedName("terbilang")
    String terbilang;

    @SerializedName("pdamRef")
    String pdamRef;

    @SerializedName("accountNumber")
    String accountNumber;

    @SerializedName("meterAwal")
    String meterAwal;

    @SerializedName("meterAkhir")
    String meterAkhir;
    // </code>

    public List<OrderPrintData.DetailTagihan> getPostpaidPLNDetailTagihan() {
        return detilTagihan == null ? Collections.<OrderPrintData.DetailTagihan>emptyList() : detilTagihan;
    }

    public List<OrderPrintData.DetailPeserta> getPostpaidBPJSDetailPeserta() {
        return detailPeserta == null ? Collections.<OrderPrintData.DetailPeserta>emptyList() : detailPeserta;
    }

    public List<OrderPrintData.Tagihan> getPostpaidTagihan() {
        return tagihan == null ? Collections.<OrderPrintData.Tagihan>emptyList() : tagihan;
    }

    public OrderData(ReportData data) {
        this.typeTxn = data.getTypeTxn();
        this.idOrder = data.getId();
    }

    public OrderData() {
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("idOrder", idOrder);
        map.put("nmTxn", nmTxn);
        map.put("typeTxn", typeTxn.toString());
        map.put("totBillAmount", ((long) totBillAmount) + "");
        map.put("agenPrice", ((long) agenPrice) + "");
        return map;
    }

    public String getFormattedDateOrder() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        return dateFormat.format(dtOrder);
    }

    public String getFormattedDateOrder2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(dtOrder) + " (" + timeFormat.format(dtOrder) + " WIB)";
    }

    public String getNormalFormattedDateOrder() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(dtOrder);
    }

    /**
     * @authors : liao.mei
     * @notes : Using  dtBill instead of dtOrder. Please take notes, class that using this function are OrderDetailActivity, PrintAddDeviceActivity, PrintChooseDeviceActivity, PrintOrderOverviewActivity
     *          this function possible NPE. please make sure dtBill and idBill are exists (in tbl_[prepaid|postpaid]_agen_txn before calling this function.
     */
    // <code>
    public String getPrintFormattedDateOrder1() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(dtBill) + " (" + timeFormat.format(dtBill) + " WIB)";
    }
    // </code>
    /**
     * @author : jesslyn
     * @notes : important part, required to update. please use this code
     *
     * @updated-by : liao.mei
     * @notes : changing dtOrder to dtBill to support new inject solution
     */
    public String getPrintFormattedDateOrder2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return dateFormat.format(dtBill);
    }

    public String getPrintFormattedTimeOrder() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(dtBill) + " WIB";
    }
    // </code>

    public String getPrintFormattedDateOrder() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE. dd MMMM yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(dtOrder) + " " + timeFormat.format(dtOrder) + " WIB";
    }

    public JSONObject toJsonObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            jsonObject.put("id", "Order ID #" + idOrder);
            jsonObject.put("idOrder", idOrder);
            jsonObject.put("dtOrder", dateFormat.format(dtOrder));
            jsonObject.put("typeTxn", typeTxn.toString());
            jsonObject.put("title", nameProduct);
            jsonObject.put("detail", nmTxn);
            jsonObject.put("date", getFormattedDateOrder());
            jsonObject.put("status", status.toDisplayString());
            jsonObject.put("ref_no", noRef);
            jsonObject.put("agenPrice", agenPrice);
            jsonObject.put("price", totBillAmount);
            jsonObject.put("price_str", "Rp. " + NumberUtils.format(totBillAmount));
            jsonObject.put("image", image);
            jsonObject.put("billAmount", billAmount);
            /**
             * @author Dinda
             * @note add totBillAmount
             */
            // <code>
            jsonObject.put("totBillAmount", totBillAmount);
            // </code>

            jsonObject.put("breakdown_price", new JSONArray(mapBreakdownPrice()));

            String jsonStr = jsonObject.toString();
            jsonObject.put("order_data", new JSONObject(jsonStr));
            jsonObject.put("invoice_data", new JSONObject(jsonStr));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @author Jesslyn
     * @note tdd 12445 - add new data
     *       (change to ref cause detail pesanan required ref)
     *       this function intended for printConfirmationActivity while printin ref
     */
    // <code>
    public String getRefNoTypeTxn(){
        return ref;
    }
    // </code>

    public String getRef() {
        switch (typeTxn) {
            case PREPAID_PULSA:
            case PREPAID_DATA:
            case PREPAID_TOPUPGAMES:
            case PREPAID_VOUCHERGAMES:
            case PREPAID_EMONEY:
            case PREPAID_BANKTRANSFER:
                return idRef;

            /**
             * @author Jesslyn
             * @note case tdd 12445 - add new data (change to refId due detail pesanan need ref)
             */
            // </code>
            case PREPAID_PLN:
                return refId;
            // </code>

            case POSTPAID_CELL:
            case POSTPAID_PGN:
            case POSTPAID_PLN:
            case POSTPAID_BPJS:
            case POSTPAID_TELKOM:
            case POSTPAID_MULTIFINANCE:
                return refId;

            case POSTPAID_PDAM:
            case POSTPAID_TV:
                return reff;

            default:
                return "-";
        }
    }

    /**
     * @authors : Cherry
     * @notes : change function name, simplified function name
     *          using static BaseActivity::isNullOrEmpty function
     * @param string : String object that need to be check
     * @return true : if string is not null or empty or contains "null" (equal ignore case)
     *         false : if string is null, empty string or contains "null" (equal ignore case)
     */
    // <code>
    // function okay if json has is true (json has key, otherwise throw NPE)
    public static boolean isNotNullOrEmptyOrNullString(String string) {
        return !BaseActivity.isNullOrEmpty(string);
    }

    // function okay if json has is true (json has key, otherwise throw NPE)
    public static String isNullThenConvert(String string) {
        return BaseActivity.isNullOrEmpty(string) ? "-" : string;
    }
    // </code>

    public String getPropertyRef() {
        return ref;
    }

    public String getFormattedTotBillAmount() {
        if(totBillAmount != 0)
            return NumberUtils.format(totBillAmount);

        double amount = billAmount;
        /**
         * @author Dinda
         * @note Case CICD 9856 - Remove isPrepaidTxn condition for discount problem (coupon 100% discount)
         */
        // <code-9856>
        amount += adminFee;
        /**
         * @author Dinda
         * @note Case CICD 9844 - Case couponAmount - totbillamout produce total pembayaran in OrderDetail not decreased by couponAmount
         */
        // <code-9844>
        amount -= couponAmount;
        // </code-9844>
        // </code-9856>
        return NumberUtils.format(amount);
    }

    /**
     * @author Jesslyn
     * @note Change to minPayment variable
     *       This function intended to handle
     *       1. Nilai Tagihan issue not equals both on PDF and Print
     *       2. Cause coupon Code which had been set on totBillAmount
     *       3. Alternative, set txnAgenPrice with minPayment @see WS
     */
    // <code>
    public String getFormattedTxnSellPriceHint() {
        /*
        Untuk batas bawah cetak dan catat txn :
        - prepaid : bill_amount
        - postpaid : tot_bill_amount_vendor + profit

        Untuk auto populate cetak dan catat txn :
        - prepaid : agenPrice
        - postpaid : tot_bill_amount_vendor + profit + agenFee
        */
        if (isPrepaidTxn() && agenPrice != 0) {
            if(agenPrice >= getMinPayment())
                return NumberUtils.format(agenPrice);
        }

        if(isPostpaidTxn())
            return NumberUtils.format(getMinPayment() + getAgenFee());

        return NumberUtils.format(getMinPayment());
    }
    // </code>

    public List<JSONObject> getBreakdownPrice() {
        List<JSONObject> arr = new ArrayList<>();
        try {
            arr.add(new JSONObject("{\"title\":\""+nmTxn+"\",\"name\":\""+nmTxn+"\",\"price\":"+billAmount+",\"price_str\":\""+NumberUtils.format(billAmount)+"\"}"));

            if(typeTxn == TransactionType.POSTPAID_PLN) {
                double sumDenda = 0;
                for(OrderPrintData.DetailTagihan item: getPostpaidPLNDetailTagihan()) {
                    sumDenda += item.getDenda();
                }
                arr.add(new JSONObject("{\"title\":\"Denda\",\"name\":\"Denda\",\"price\":"+sumDenda+",\"price_str\":\""+NumberUtils.format(sumDenda)+"\"}"));
            }

            if(typeTxn == TransactionType.POSTPAID_PDAM) {
                double sumDenda = 0;
                for(OrderPrintData.Tagihan item: getPostpaidTagihan()) {
                    sumDenda += item.getPenalty();
                }
                arr.add(new JSONObject("{\"title\":\"Denda\",\"name\":\"Denda\",\"price\":"+sumDenda+",\"price_str\":\""+NumberUtils.format(sumDenda)+"\"}"));
            }

            if(typeTxn == TransactionType.POSTPAID_MULTIFINANCE) {
                /**
                 * @authors : Cherry
                 * @notes : fixing bug multifinance "denda" not appears. because MAF, MCF, and WOM using penalty instead of tagihan
                 */
                // <code>
                double sumDenda = penalty;
                // </code>
                arr.add(new JSONObject("{\"title\":\"Denda\",\"name\":\"Denda\",\"price\":"+penalty+",\"price_str\":\""+NumberUtils.format(sumDenda)+"\"}"));
            }

            /**
             * @uathor Eliza Sutantya
             * @patch FR19022
             * @notes fixing admin fee issue, dispay admin fee at order detail . comment if condition for if
             */
            // if (!isPrepaidTxn()) {
                arr.add(new JSONObject("{\"title\":\"Biaya Admin\",\"name\":\"Biaya Admin\",\"price\":"+adminFee+",\"price_str\":\""+NumberUtils.format(adminFee)+"\"}"));
            // }

            if(typeTxn == TransactionType.POSTPAID_PDAM) {
                double sumTagihanLain = 0;
                for(OrderPrintData.Tagihan item: getPostpaidTagihan()) {
                    sumTagihanLain += item.getTagihanLain();
                }
                arr.add(new JSONObject("{\"title\":\"Tagihan Lain\",\"name\":\"Tagihan Lain\",\"price\":"+sumTagihanLain+",\"price_str\":\""+NumberUtils.format(sumTagihanLain)+"\"}"));
            }

            if(cdeCoupon != null && !cdeCoupon.equals("")) {
                /**
                 * @author Liao Mei
                 * @note add name with "kupon"; couponAmount * -1;
                 */
                // <code>
                arr.add(new JSONObject("{\"title\":\""+cdeCoupon+"\",\"name\":\""+"Kupon : " + cdeCoupon+"\",\"price\":"+couponAmount * -1+",\"price_str\":\""+NumberUtils.format(couponAmount * -1)+"\"}"));
                // </code>
            }
        } catch (JSONException ex){
            ex.printStackTrace();
        }
        return arr;
    }

    public List<JSONObject> getPrintOverviewBreakdownPrice() {
        double billAmount = this.billAmount;
        double adminFee = this.adminFee;

        // TODO : need to verify this logic whether is true or no
        if(sellingData != null) {
            billAmount = totBillAmount;
            adminFee = sellingData.getSellingPrice() - totBillAmount;
        }
        List<JSONObject> arr = new ArrayList<>();
        try {
            /**
             * @author : jesslyn
             * @notes : important part, required to update. please use this code
             * <code>
             */
            arr.add(new JSONObject("{\"name\":\""+nmTxn+"\",\"price\":"+billAmount+",\"price_str\":\""+NumberUtils.format(billAmount)+"\"}"));
            arr.add(new JSONObject("{\"name\":\"Keuntungan\",\"price\":"+adminFee+",\"price_str\":\""+NumberUtils.format(adminFee)+"\"}"));
            // </code>
        } catch (JSONException ex){
            ex.printStackTrace();
        }
        return arr;
    }

    private List<JSONObject> mapBreakdownPrice() throws JSONException {
        List<JSONObject> arr = new ArrayList<>();
        arr.add(new JSONObject("{\"name\":\""+nmTxn+"\",\"price\":"+billAmount+",\"price_str\":\""+NumberUtils.format(billAmount)+"\"}"));
        if(!isPrepaidTxn()) {
            arr.add(new JSONObject("{\"name\":\"Biaya Admin\",\"price\":"+adminFee+",\"price_str\":\""+NumberUtils.format(adminFee)+"\"}"));
        }
        return arr;
    }

    /**
     * @author Jesslyn
     * @note check product type based on order id
     * @param orderId
     * @return
     */
    // <code>
    public static ProductType orderIdToProductType(String orderId){
        switch (orderId.charAt(0)){
            case '1' :
                return ProductType.DEPOSIT;
            case '2' :
                return ProductType.PREPAID;
            case '3' :
                return ProductType.POSTPAID;
            case '4' :
                return ProductType.UPGRADE;
            case '5' :
                return ProductType.TRANSFER;
            case '6' :
                return ProductType.TOPUP;
            default:
                return null;
        }
    }

    public ProductType getProductTypeData(){
        if(typeTxn == null){
            return orderIdToProductType(idOrder);
        }else{
            return typeTxn.toProductType();
        }
    }

    public static ProductType getProductTypeData(String idOrder){
        return orderIdToProductType(idOrder);
    }

    /**
     * @note assume all typeTxn null data and product type deposit is TOPUP_DEPOSIT_BT
     *       this required to call if the product type is deposit and typeTxn is null.
     *       its simulate typeTxn for handling ref section layout at OrderDetailActivity and continue payment (for waiting status)
     */
    public void simulateTypeTxnForDeposit(){
        if(typeTxn == null && getProductTypeData() == ProductType.DEPOSIT){
            typeTxn = TransactionType.TOPUP_GENERAL;
        }
    }
    // </code>

    public boolean isPrepaidTxn() {
        return typeTxn == TransactionType.PREPAID_DATA || typeTxn == TransactionType.PREPAID_PULSA || typeTxn == TransactionType.PREPAID_PLN
                || typeTxn == TransactionType.PREPAID_COUPON || typeTxn == TransactionType.PREPAID_TOPUPGAMES || typeTxn == TransactionType.PREPAID_VOUCHERGAMES ||
                typeTxn == TransactionType.PREPAID_EMONEY || typeTxn == TransactionType.PREPAID_BANKTRANSFER;
    }

    public boolean isPostpaidTxn() {
        return !isPrepaidTxn() && !isDepositTxn();
    }

    public boolean isDepositTxn() {
        return typeTxn == TransactionType.TOPUP_GENERAL
                || typeTxn == TransactionType.TOPUP_DEPOSIT_BT
                || typeTxn == TransactionType.TOPUP_DEPOSIT_VA
                || typeTxn == TransactionType.TOPUP_DEPOSIT_QRIS
                || typeTxn == TransactionType.TOPUP_DEPOSIT_RETAIL;
    }

    public List<Pair<String, String>> getDetailInfo(Context context) {

        if(typeTxn == null )return Collections.emptyList();

        switch (typeTxn) {
            case TOPUP_DEPOSIT_BT:
            case TOPUP_DEPOSIT_QRIS:
            case TOPUP_DEPOSIT_RETAIL:
            case TOPUP_DEPOSIT_VA:
                return Collections.emptyList();
            case PREPAID_PLN:
                return getDetailPrepaidPLN(context);
            case PREPAID_TOPUPGAMES:
            case PREPAID_VOUCHERGAMES:
                return Collections.emptyList();
            case PREPAID_BANKTRANSFER:
                return getDetailPrepaidBankTransfer(context);
            case POSTPAID_CELL:
                return getDetailPostpaidCellular(context);
            case POSTPAID_PLN:
                return getDetailPostpaidPLN(context);
            case POSTPAID_PGN:
                return getDetailPostpaidPGN(context);
            case POSTPAID_BPJS:
                return getDetailPostpaidBPJS(context);
            case POSTPAID_PDAM:
                return getDetailPostpaidPDAM(context);
            case POSTPAID_TV:
                return getDetailPostpaidTV(context);
            case POSTPAID_TELKOM:
                return getDetailPostpaidTelkom(context);
            case POSTPAID_MULTIFINANCE:
                return getDetailPostpaidMultifinance(context);
            default:
                return Collections.emptyList();
        }
    }

    private List<Pair<String, String>> getDetailPrepaidBankTransfer(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        if(isNotNullOrEmptyOrNullString(getNameProduct()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_banktransfer_bank_name), getNameProduct()));

        if(isNotNullOrEmptyOrNullString(getNoCust()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_banktransfer_account_no), getNoCust()));

        if(isNotNullOrEmptyOrNullString(getNmCust()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_banktransfer_account_name), getNmCust()));

        if(isNotNullOrEmptyOrNullString(getInfoText()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_banktransfer_info), getInfoText()));
        return list;
    }

    private List<Pair<String, String>> getDetailPrepaidPLN(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        /**
         * @author Eliza Sutantya
         * @patch FR19022 - P12511
         * @notes upgrading otomax v1 to v2, remove unused detail
         */
        // <code>
//        if(isNotNullOrEmptyOrNullString(getMsn()))
//            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_no_meter), getMsn()));

        if(isNotNullOrEmptyOrNullString(getSubscriberId()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_id_pel), getSubscriberId()));

        if(isNotNullOrEmptyOrNullString(getNmCust()))
            list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));
        /**
         * @author : jesslyn
         * @notes : important part, required to update. please use this code
         *
         * @updated-by : cherry
         * @notes : changing behavior of if condition statement, see detail at the function. Adding asterik-mark (!)
         * <code>
         */
        // <code>
        if(isNotNullOrEmptyOrNullString(getTarif()) && isNotNullOrEmptyOrNullString(getDaya()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_tarif_daya), getTarif() + " / " + getDaya() + " VA"));
        // </code>
//        if(isNotNullOrEmptyOrNullString(ref))
//            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_no_ref), ref));

        /**
         * @author Jesslyn
         * @note remove condition check cause vendorAdmin and profit never null (if null, double set to 0.0)
         *       Also, totBillAmount never null because totBillAmount already saved when transaction-payment
         *       totBillAmount also possible had deduction from coupon code
         *       case - BDD 11022
         */
        // <code>
//        list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_rp_bayar), "Rp. " + NumberUtils.format(getTotBillAmount())  ));
        // </code>

//        if(isNotNullOrEmptyOrNullString(getBiayaMaterai()))
//            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_meterai), "Rp. " + getBiayaMaterai() ));
//
//        if(isNotNullOrEmptyOrNullString(getPpn()))
//            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_ppn), "Rp. " + getPpn() ));
//
//        if(isNotNullOrEmptyOrNullString(getPpj()))
//            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_ppj), "Rp. " + getPpj() ));
//
//        if(isNotNullOrEmptyOrNullString(getAngsuran()))
//            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_angsuran), "Rp. " + getAngsuran() ));
//
//        if(isNotNullOrEmptyOrNullString(getRpToken()))
//            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_rp_stroom), "Rp. " + getRpToken() ));

        if(isNotNullOrEmptyOrNullString(getKwh()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_jml_kwh), getKwh()));

        /**
         * @author Jesslyn
         * @note remove condition check cause vendorAdmin and profit never null (if null, double set to 0.0)
         *       Vendor admin contains profit (if payment successful) or estimation profit (if continue payment)
         *       @see case BDD 11023 / Modul5Controller
         */
        // <code>
//        list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_admin), "Rp. " + NumberUtils.format( getVendorAdmin()) ));
        // </code>

        if(isNotNullOrEmptyOrNullString(getTokenNumber()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_stroom_token), getTokenNumber()));

        if(isNotNullOrEmptyOrNullString(getInfoText()))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_info), getInfoText()));
        // </code>

        return list;
    }

    private List<Pair<String, String>> getDetailPostpaidCellular(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        if(isNotNullOrEmptyOrNullString(getNmTxn()))
            list.add(Pair.create(context.getString(R.string.print_device_layanan), getNmTxn()));

        if(isNotNullOrEmptyOrNullString(getNoHp()))
            list.add(Pair.create(context.getString(R.string.print_device_no_hp), getNoHp()));

        if(isNotNullOrEmptyOrNullString(getNmCust()))
            list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));

        if(isNotNullOrEmptyOrNullString(getPeriode()))
            list.add(Pair.create(context.getString(R.string.print_device_period), getPeriode()));

        list.add(Pair.create(context.getString(R.string.print_device_lembar_tagih), NumberUtils.format(getTotBill()) ));

        list.add(Pair.create(context.getString(R.string.print_device_tagihan), "Rp. " + NumberUtils.format(getBillAmount()) ));

        list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), "Rp. " + NumberUtils.format(getAdminFee()) ));

        list.add(Pair.create(context.getString(R.string.print_device_total_detail), "Rp. " + NumberUtils.format(getTotBillAmount()) ));

        return list;
    }

    public static String toIdr(Double val){
        return "Rp. " + NumberUtils.format(val);
    }

    //No Meter, Nama, Tarif / Daya, Jumlah Tagihan
    private List<Pair<String, String>> getDetailPostpaidPLN(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        if(isNotNullOrEmptyOrNullString(getNoPln() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_id_pel), getNoPln()));

        if(isNotNullOrEmptyOrNullString(getNmCust() ))
            list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));
        /**
         * @authors : Cherry
         * @notes : changing behavior of if condition statement, see detail at the function. Adding asterik-mark (!)
         */
        // <code>
        if(isNotNullOrEmptyOrNullString(getTarif() ) && isNotNullOrEmptyOrNullString(getDaya() ))
            list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_tarif_daya), (isNotNullOrEmptyOrNullString(getTarif()) ? getTarif() : "-" ) + " / " + getDaya() + " VA"));
        // </code>
        if(isNotNullOrEmptyOrNullString(getLembarTagihanTotal()) && isNotNullOrEmptyOrNullString(getLembarTagihan() )) {
            int lbrTagTot = Integer.parseInt(getLembarTagihanTotal());
            int lbrTag = Integer.parseInt(getLembarTagihan());

            String additionalInfo = "";
            if (lbrTagTot > lbrTag)
                additionalInfo = "(Maks. 4 per transaksi)";
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_lembar_tagihan), lbrTag + " / " + lbrTagTot + " " + additionalInfo));
        }

        if(isNotNullOrEmptyOrNullString(getLembarTagihanSisa() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_sisa_tagihan), getLembarTagihanSisa() ));

        if(isNotNullOrEmptyOrNullString(getNoRef() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_no_ref), getNoRef()));

        list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_rp_tag_pln), toIdr(getBillAmount()) ));

        list.add(Pair.create(context.getString(R.string.print_device_denda), toIdr(getDenda()) ));

        list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_admin), toIdr(getAdminFee()) ));

        list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_total_bayar), toIdr(getTotBillAmount()) ));

        if(getPostpaidPLNDetailTagihan().size() > 0){
            int i = 1;

            list.add(Pair.create("", "" ));
            list.add(Pair.create("Detail Tagihan", "" ));

            for(OrderPrintData.DetailTagihan item: getPostpaidPLNDetailTagihan()) {
                if(isNotNullOrEmptyOrNullString(item.getPeriode() ))
                    list.add(Pair.create( context.getString(R.string.print_device_period) + " " + i, item.getPeriode()));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_tagihan), toIdr(item.getNilaiTagihan())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_denda), toIdr(item.getDenda())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_admin), toIdr(item.getAdmin())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_total), toIdr(item.getTotal())));

                if(isNotNullOrEmptyOrNullString(item.getMeterAwal() ) && isNotNullOrEmptyOrNullString(item.getMeterAkhir() ))
                    list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_stand_meter), item.getMeterAwal() + " - " + item.getMeterAkhir()));

                if(i < (getPostpaidPLNDetailTagihan().size()))
                    list.add(Pair.create("", "" ));
                i++;
            }
        }

        if(isNotNullOrEmptyOrNullString(getInfoText() )){
            list.add(Pair.create("", "" ));
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pln_info), getInfoText() ));
        }

        return list;
    }

    private List<Pair<String, String>> getDetailPostpaidPGN(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        if(isNotNullOrEmptyOrNullString(getNoPgn() ))
            list.add(Pair.create(context.getString(R.string.print_device_no_pgn), getNoPgn()));

        if(isNotNullOrEmptyOrNullString(getNmCust() ))
            list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));

        if(isNotNullOrEmptyOrNullString(getAlamat() ))
            list.add(Pair.create(context.getString(R.string.print_device_address), getAlamat()));

        if(isNotNullOrEmptyOrNullString(getPeriode() ))
            list.add(Pair.create(context.getString(R.string.print_device_period), getPeriode()));

        if(isNotNullOrEmptyOrNullString(getStandAwal() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pgn_stand_awal), getStandAwal()));

        if(isNotNullOrEmptyOrNullString(getStandAkhir() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pgn_stand_akhir), getStandAkhir()));

        if(isNotNullOrEmptyOrNullString(getPemakaian() ))
            list.add(Pair.create(context.getString(R.string.print_device_pemakaian), getPemakaian()));

        if(isNotNullOrEmptyOrNullString(ref))
            list.add(Pair.create(context.getString(R.string.print_device_no_ref), ref));

        if(isNotNullOrEmptyOrNullString(getTglBayar() ))
            list.add(Pair.create(context.getString(R.string.print_device_tanggal_bayar), getTglBayar() ));

        list.add(Pair.create(context.getString(R.string.print_device_tagihan), toIdr(getBillAmount() )));

        list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), toIdr(getAdminFee() )));

        list.add(Pair.create(context.getString(R.string.print_device_total_detail), toIdr(getTotBillAmount() )));

        return list;
    }

    private List<Pair<String, String>> getDetailPostpaidBPJS(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        if(isNotNullOrEmptyOrNullString(getNoRef() ))
            list.add(Pair.create(context.getString(R.string.print_device_no_ref), getNoRef()));

        if(getDtOrder() != null )
            list.add(Pair.create(context.getString(R.string.print_device_tanggal), getNormalFormattedDateOrder() ));

        if(isNotNullOrEmptyOrNullString(getNoBpjs() ))
            list.add(Pair.create(context.getString(R.string.print_device_no_bpjs), getNoBpjs()));

        if(isNotNullOrEmptyOrNullString(getNmCust() ))
            list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));

        if(isNotNullOrEmptyOrNullString(getJumlahPeriode() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_bpjs_jumlah_periode), getJumlahPeriode() + " Bulan"));

        if(isNotNullOrEmptyOrNullString(getJumlahPeserta() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_bpjs_jumlah_peserta), getJumlahPeserta() + " Orang"));

        list.add(Pair.create(context.getString(R.string.print_device_tagihan), toIdr(getBillAmount() )));

        list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), toIdr(getAdminFee() )));

        list.add(Pair.create(context.getString(R.string.print_device_total_detail), toIdr(getTotBillAmount() )));

        /*
        if(getPostpaidBPJSDetailPeserta().size() > 0) {
            int i = 1;

            list.add(Pair.create("", ""));
            list.add(Pair.create("Detail Peserta", ""));

            for(OrderPrintData.DetailPeserta item: getPostpaidBPJSDetailPeserta()) {
                if(isNotNullOrEmptyOrNullString(item.getNoPeserta() ))
                    list.add(Pair.create(context.getString(R.string.print_device_no_bpjs), item.getNoPeserta()));

                if(isNotNullOrEmptyOrNullString(item.getNama() ))
                    list.add(Pair.create(context.getString(R.string.print_device_name), item.getNama()));

                list.add(Pair.create(context.getString(R.string.print_device_postpaid_bpjs_premi), toIdr(item.getPremi()) ));

                list.add(Pair.create(context.getString(R.string.print_device_postpaid_bpjs_saldo), toIdr(item.getSaldo()) ));

                if(i < (getPostpaidBPJSDetailPeserta().size()))
                    list.add(Pair.create("", "" ));
                i++;
            }
        }
        */
        return list;
    }

    private List<Pair<String, String>> getDetailPostpaidPDAM(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        String cdeProduct = getCdeProduct();

        if(WaterInputNoActivity.isPdamCh1(cdeProduct) && getDtOrder()  != null)
            list.add(Pair.create(context.getString(R.string.print_device_tanggal), getNormalFormattedDateOrder()));

        if(isNotNullOrEmptyOrNullString(getNoPdam() ))
            list.add( Pair.create(context.getString(R.string.print_device_no_pdam), getNoPdam() ));

        if(isNotNullOrEmptyOrNullString(getNmCust() ))
            list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust() ));

        if(isNotNullOrEmptyOrNullString(getAlamat() ))
            list.add(Pair.create(context.getString(R.string.print_device_address), getAlamat() ));

        if(isNotNullOrEmptyOrNullString(getNmTxn() ))
            list.add(Pair.create(context.getString(R.string.print_device_nama_layanan), getNmTxn() ));

        if(isNotNullOrEmptyOrNullString(getJumlahTagihan() ))
            list.add(Pair.create(context.getString(R.string.print_device_jumlah_tagihan), getJumlahTagihan() + " Lembar" ));

        if(isNotNullOrEmptyOrNullString(getTarif() ))
            list.add(Pair.create(context.getString(R.string.print_device_tarif), getTarif() ));

        if(isNotNullOrEmptyOrNullString(getMeterAwal() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pdam_meter_awal), getMeterAwal() ));

        if(isNotNullOrEmptyOrNullString(getMeterAkhir() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pdam_meter_akhir), getMeterAkhir() ));

        if(isNotNullOrEmptyOrNullString(getPemakaian() ))
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pdam_pemakaian), getPemakaian() ));

        if(!WaterInputNoActivity.isPdamCh1(cdeProduct)){
            if(isNotNullOrEmptyOrNullString(getAccountNumber() ))
                list.add(Pair.create(context.getString(R.string.print_device_nomor_account), getAccountNumber() ));

            if(isNotNullOrEmptyOrNullString(ref) )
                list.add(Pair.create(context.getString(R.string.print_device_no_ref), ref ));

            // Struk format required PDAM ref need to display if the data contains empyty
            if(isNotNullOrEmptyOrNullString(getPdamRef() ))
                list.add(Pair.create(context.getString(R.string.print_device_postpaid_pdam_ref), getPdamRef() ));
            else
                list.add(Pair.create(context.getString(R.string.print_device_postpaid_pdam_ref), "-" ));
        }

        list.add(Pair.create(context.getString(R.string.print_device_tagihan), toIdr(getBillAmount()) ));

        list.add(Pair.create(context.getString(R.string.print_device_denda), toIdr(getDenda()) ));

        list.add(Pair.create(context.getString(R.string.print_device_postpaid_pdam_tagihan_lain), toIdr(getTagihanLain()) ));

        list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), toIdr(getAdminFee()) ));

        list.add(Pair.create(context.getString(R.string.print_device_total_detail), toIdr(getTotBillAmount()) ));

        if(isNotNullOrEmptyOrNullString(getTerbilang() ))
            list.add(Pair.create(context.getString(R.string.print_device_terbilang), getTerbilang() ));

        if(getPostpaidTagihan().size() > 0) {
            int i = 1;

            list.add(Pair.create("", ""));
            list.add(Pair.create("Detail Tagihan", ""));

            for(OrderPrintData.Tagihan item: getPostpaidTagihan()) {
                if(isNotNullOrEmptyOrNullString(item.getPeriode() ))
                    list.add(Pair.create(context.getString(R.string.print_device_period) + " " + i, item.getPeriode()) );

                if(isNotNullOrEmptyOrNullString(item.getFormattedPemakaian() ))
                    list.add(Pair.create("- " + context.getString(R.string.print_device_pemakaian), item.getFormattedPemakaian() + " Liter" ));

                if(isNotNullOrEmptyOrNullString(item.getFormattedMeterAkhir() ))
                    list.add(Pair.create("- " + context.getString(R.string.print_device_postpaid_pdam_meter_awal), item.getFormattedMeterAwal()));

                if(isNotNullOrEmptyOrNullString(item.getFormattedMeterAkhir() ))
                    list.add(Pair.create("- " + context.getString(R.string.print_device_postpaid_pdam_meter_akhir), item.getFormattedMeterAkhir()));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_tagihan), toIdr(item.getNilaiTagihan())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_denda), toIdr(item.getPenalty())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pdam_tagihan_lain), toIdr(item.getTagihanLain())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_admin), toIdr(item.getAdmin())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_total), toIdr(item.getTotal())));


                if(i < (getPostpaidTagihan().size()))
                    list.add(Pair.create("", "" ));
                i++;
            }
        }

        if(isNotNullOrEmptyOrNullString(getInfoText() )){
            list.add(Pair.create("", "" ));
            list.add(Pair.create(context.getString(R.string.print_device_postpaid_pdam_info), getInfoText() ));
        }

        return list;
    }

    private List<Pair<String, String>> getDetailPostpaidTV(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        list.add(Pair.create(context.getString(R.string.print_device_id_pel), getNoTv()));
        list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));
        list.add(Pair.create(context.getString(R.string.print_device_jumlah_tagihan), getJumlahTagihan()));

        list.add(Pair.create(context.getString(R.string.print_device_tagihan), toIdr(getBillAmount()) ));

        list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), toIdr(getAdminFee()) ));

        list.add(Pair.create(context.getString(R.string.print_device_total_detail), toIdr(getTotBillAmount()) ));

        if(getPostpaidTagihan().size() > 0) {
            int i = 1;

            list.add(Pair.create("", ""));
            list.add(Pair.create("Detail Tagihan", ""));

            for(OrderPrintData.Tagihan item: getPostpaidTagihan()) {
                /**
                 * @authors : Jesslyn
                 * @notes : add validation function for null, empty or contains empty string
                 *
                 * @update : remove validation, replace to convertToDash, see function
                 */
                // <code>
                list.add(Pair.create(context.getString(R.string.print_device_period) + " " + i, isNullThenConvert(item.getPeriode()) ));

                list.add(Pair.create("- " + context.getString(R.string.print_device_jatuh_tempo), isNullThenConvert(item.getJatuhTempo()) ));

                list.add(Pair.create("- " + context.getString(R.string.print_device_ref), isNullThenConvert(item.getReferensi()) ));

                list.add(Pair.create("- " + context.getString(R.string.print_device_no_ref_1), isNullThenConvert(item.getNoref1()) ));

                list.add(Pair.create("- " + context.getString(R.string.print_device_no_ref_2), isNullThenConvert(item.getNoref2()) ));

                list.add(Pair.create("- " + context.getString(R.string.print_device_postpaid_tv_paket), isNullThenConvert(item.getPaket()) ));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_tagihan), toIdr(item.getNilaiTagihan())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_admin), toIdr(item.getAdmin())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_total), toIdr(item.getTotal())));
                // </code>

                if(i < (getPostpaidTagihan().size()))
                    list.add(Pair.create("", "" ));
                i++;
            }
        }

        return list;
    }

    private List<Pair<String, String>> getDetailPostpaidTelkom(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        if(getDtOrder()  != null)
            list.add(Pair.create(context.getString(R.string.print_device_tanggal), getNormalFormattedDateOrder()));

        if(isNotNullOrEmptyOrNullString(getNoTelkom() ))
            list.add(Pair.create(context.getString(R.string.print_device_id_pel), getNoTelkom()));

        if(isNotNullOrEmptyOrNullString(getNmCust() ))
            list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));

        if(isNotNullOrEmptyOrNullString(getNmTxn() ))
            list.add(Pair.create(context.getString(R.string.print_device_layanan), getNmTxn() ));

        list.add(Pair.create(context.getString(R.string.print_device_postpaid_telkom_divre_datel), isNullThenConvert(getDivre()) + " / " +  isNullThenConvert(getDatel()) ));

        if(isNotNullOrEmptyOrNullString(getNmTxn() ))
            list.add(Pair.create(context.getString(R.string.print_device_ref), isNullThenConvert(getIdRef()) ));

        list.add(Pair.create(context.getString(R.string.print_device_jumlah_tagihan), getJumlahTagihan()));

        list.add(Pair.create(context.getString(R.string.print_device_tagihan), toIdr(getBillAmount()) ));

        list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), toIdr(getAdminFee()) ));

        list.add(Pair.create(context.getString(R.string.print_device_total_detail), toIdr(getTotBillAmount()) ));

        if(getPostpaidTagihan().size() > 0) {
            int i = 1;

            list.add(Pair.create("", ""));
            list.add(Pair.create("Detail Tagihan", ""));

            for(OrderPrintData.Tagihan item: getPostpaidTagihan()) {
                list.add(Pair.create(context.getString(R.string.print_device_period) + " " + i, isNullThenConvert(item.getPeriode()) ));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_tagihan), toIdr(item.getNilaiTagihan())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_admin), toIdr(item.getAdmin())));

                list.add(Pair.create( "- " + context.getString(R.string.print_device_postpaid_pln_total), toIdr(item.getTotal())));

                if(i < (getPostpaidTagihan().size()))
                    list.add(Pair.create("", "" ));
                i++;
            }
        }

        return list;
    }

    private List<Pair<String, String>> getDetailPostpaidMultifinance(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();

        if(cdeProduct == null) return list;

        if(isNotNullOrEmptyOrNullString(getNmTxn() ))
            list.add(Pair.create(context.getString(R.string.print_device_layanan), getNmTxn() ));

        if(cdeProduct.toUpperCase().equals("FNWOMD") || cdeProduct.toUpperCase().equals("FNCOLUMD")) {
            if(isNotNullOrEmptyOrNullString(getNoContract() ))
                list.add(Pair.create(context.getString(R.string.print_device_id_pel), getNoContract()));

            if(isNotNullOrEmptyOrNullString(getNmCust() ))
                list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));

            if(isNotNullOrEmptyOrNullString(getJatuhTempo() ))
                list.add(Pair.create(context.getString(R.string.print_device_jatuh_tempo), getJatuhTempo()));

            if(isNotNullOrEmptyOrNullString(getAngsuranKe() ))
                list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_angsuran_ke), getAngsuranKe()));

            if(ref != null)
                list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_no_ref), ref));

            list.add(Pair.create(context.getString(R.string.print_device_tagihan), toIdr(getBillAmount()) ));

            list.add(Pair.create(context.getString(R.string.print_device_denda), toIdr(getPenalty()) ));

            list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), toIdr(getAdminFee()) ));

            list.add(Pair.create(context.getString(R.string.print_device_total_detail), toIdr(getTotBillAmount()) ));

        } else if(cdeProduct.toUpperCase().equals("FNMAF") || cdeProduct.toUpperCase().equals("FNMEGA")) {
            if(isNotNullOrEmptyOrNullString(getProvider() ))
                list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_provider), getProvider()));

            if(isNotNullOrEmptyOrNullString(getNamaCabang() ))
                list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_nama_cabang), getNamaCabang()));

            if(isNotNullOrEmptyOrNullString(getNoContract() ))
                list.add(Pair.create(context.getString(R.string.print_device_id_pel), getNoContract()));

            if(isNotNullOrEmptyOrNullString(getNmCust() ))
                list.add(Pair.create(context.getString(R.string.print_device_name), getNmCust()));

            list.add(Pair.create(context.getString(R.string.print_device_address), isNullThenConvert(getAlamat()) ));

            list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_nama_barang), isNullThenConvert(getItemName()) ));

            list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_no_rangka), isNullThenConvert(getNoRangka()) ));

            list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_no_polisi), isNullThenConvert(getNoPol()) ));

            list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_periode_pembayaran_terakhir), isNullThenConvert((getLastPaidPeriod() )) ));

            list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_batas_waktu_pembayaran_terakhir), isNullThenConvert((getLastPaidDueDate() )) ));

            list.add(Pair.create(context.getString(R.string.print_device_postpaid_multifinance_tenor), isNullThenConvert((getTenor() )) ));

            if(ref != null)
                list.add(Pair.create(context.getString(R.string.print_device_prepaid_pln_no_ref), ref));

            list.add(Pair.create(context.getString(R.string.print_device_tagihan), toIdr(getBillAmount()) ));

            list.add(Pair.create(context.getString(R.string.print_device_denda), toIdr(getPenalty()) ));

            list.add(Pair.create(context.getString(R.string.print_device_biaya_admin), toIdr(getAdminFee()) ));

            list.add(Pair.create(context.getString(R.string.print_device_total_detail), toIdr(getTotBillAmount()) ));
        }
        return list;
    }
}
