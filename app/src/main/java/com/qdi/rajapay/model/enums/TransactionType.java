package com.qdi.rajapay.model.enums;

import com.google.gson.annotations.SerializedName;

public enum TransactionType {

    /**
     * @author Jesslyn
     * @note add new transaction type to support deposit transaction with txntype null to ticket question
     */
    // <code>
    @SerializedName("TOPUPGENERAL")
    TOPUP_GENERAL("TOPUPGENERAL"),
    // </code>

    @SerializedName("TOPUPDEPOSITVA")
    TOPUP_DEPOSIT_VA("TOPUPDEPOSITVA"),

    @SerializedName("TOPUPDEPOSITBT")
    TOPUP_DEPOSIT_BT("TOPUPDEPOSITBT"),

    @SerializedName("TOPUPDEPOSITRETAIL")
    TOPUP_DEPOSIT_RETAIL("TOPUPDEPOSITRETAIL"),

    @SerializedName("TOPUPDEPOSITQRIS")
    TOPUP_DEPOSIT_QRIS("TOPUPDEPOSITQRIS"),

    @SerializedName("PREPAIDDATA")
    PREPAID_DATA("PREPAIDDATA"),

    @SerializedName("PREPAIDPULSA")
    PREPAID_PULSA("PREPAIDPULSA"),

    @SerializedName("PREPAIDPLN")
    PREPAID_PLN("PREPAIDPLN"),

    @SerializedName("PREPAIDCOUPON")
    PREPAID_COUPON("PREPAIDCOUPON"),

    @SerializedName("PREPAIDTOPUPGAMES")
    PREPAID_TOPUPGAMES("PREPAIDTOPUPGAMES"),

    @SerializedName("PREPAIDVOUCHERGAMES")
    PREPAID_VOUCHERGAMES("PREPAIDVOUCHERGAMES"),

    @SerializedName("PREPAIDEMONEY")
    PREPAID_EMONEY("PREPAIDEMONEY"),

    @SerializedName("PREPAIDBANKTRANSFER")
    PREPAID_BANKTRANSFER("PREPAIDBANKTRANSFER"),

    @SerializedName("POSTPAIDCELL")
    POSTPAID_CELL("POSTPAIDCELL"),

    @SerializedName("POSTPAIDPLN")
    POSTPAID_PLN("POSTPAIDPLN"),

    @SerializedName("POSTPAIDPGN")
    POSTPAID_PGN("POSTPAIDPGN"),

    @SerializedName("POSTPAIDBPJS")
    POSTPAID_BPJS("POSTPAIDBPJS"),

    @SerializedName("POSTPAIDPDAM")
    POSTPAID_PDAM("POSTPAIDPDAM"),

    @SerializedName("POSTPAIDTV")
    POSTPAID_TV("POSTPAIDTV"),

    @SerializedName("POSTPAIDTELKOM")
    POSTPAID_TELKOM("POSTPAIDTELKOM"),

    @SerializedName("POSTPAIDMULTIFINANCE")
    POSTPAID_MULTIFINANCE("POSTPAIDMULTIFINANCE");

    private final String text;

    TransactionType(final String text) {
        this.text = text;
    }
    public static TransactionType fromString(String text) {
        if(text.toUpperCase().equals(TOPUP_DEPOSIT_VA.text)) return TOPUP_DEPOSIT_VA;
        else if(text.toUpperCase().equals(TOPUP_DEPOSIT_BT.text)) return TOPUP_DEPOSIT_BT;
        else if(text.toUpperCase().equals(TOPUP_DEPOSIT_QRIS.text)) return TOPUP_DEPOSIT_QRIS;
        else if(text.toUpperCase().equals(TOPUP_DEPOSIT_RETAIL.text)) return TOPUP_DEPOSIT_RETAIL;
        else if(text.toUpperCase().equals(PREPAID_DATA.text)) return PREPAID_DATA;
        else if(text.toUpperCase().equals(PREPAID_PULSA.text)) return PREPAID_PULSA;
        else if(text.toUpperCase().equals(PREPAID_PLN.text)) return PREPAID_PLN;
        else if(text.toUpperCase().equals(PREPAID_COUPON.text)) return PREPAID_COUPON;
        else if(text.toUpperCase().equals(PREPAID_TOPUPGAMES.text)) return PREPAID_TOPUPGAMES;
        else if(text.toUpperCase().equals(PREPAID_VOUCHERGAMES.text)) return PREPAID_VOUCHERGAMES;
        else if(text.toUpperCase().equals(PREPAID_EMONEY.text)) return PREPAID_EMONEY;
        else if(text.toUpperCase().equals(PREPAID_BANKTRANSFER.text)) return PREPAID_BANKTRANSFER;
        else if(text.toUpperCase().equals(POSTPAID_CELL.text)) return POSTPAID_CELL;
        else if(text.toUpperCase().equals(POSTPAID_PLN.text)) return POSTPAID_PLN;
        else if(text.toUpperCase().equals(POSTPAID_PGN.text)) return POSTPAID_PGN;
        else if(text.toUpperCase().equals(POSTPAID_BPJS.text)) return POSTPAID_BPJS;
        else if(text.toUpperCase().equals(POSTPAID_PDAM.text)) return POSTPAID_PDAM;
        else if(text.toUpperCase().equals(POSTPAID_TV.text)) return POSTPAID_TV;
        else if(text.toUpperCase().equals(POSTPAID_TELKOM.text)) return POSTPAID_TELKOM;
        else if(text.toUpperCase().equals(POSTPAID_MULTIFINANCE.text)) return POSTPAID_MULTIFINANCE;
        else return null;
    }

    @Override
    public String toString() {
        return text;
    }

    public ProductType toProductType() {
        switch (this) {
            case TOPUP_DEPOSIT_BT: return ProductType.DEPOSIT;
            case TOPUP_DEPOSIT_VA: return ProductType.DEPOSIT;
            case TOPUP_DEPOSIT_QRIS: return ProductType.DEPOSIT;
            case TOPUP_DEPOSIT_RETAIL: return ProductType.DEPOSIT;
            case PREPAID_PULSA: return ProductType.PULSA;
            case PREPAID_DATA: return ProductType.DATA;
            case PREPAID_PLN: return ProductType.TOKENPLN;
            case PREPAID_COUPON: return ProductType.COUPON;
            case PREPAID_TOPUPGAMES: return ProductType.TOPUPGAMES;
            case PREPAID_VOUCHERGAMES: return ProductType.VOUCHERGAMES;
            case PREPAID_EMONEY: return ProductType.EMONEY;
            case PREPAID_BANKTRANSFER: return ProductType.BANKTRANSFER;
            case POSTPAID_CELL: return ProductType.CELLULAR;
            case POSTPAID_PLN: return ProductType.PLN;
            case POSTPAID_PGN: return ProductType.PGN;
            case POSTPAID_BPJS: return ProductType.BPJS;
            case POSTPAID_PDAM: return ProductType.PDAM;
            case POSTPAID_TV: return ProductType.TV;
            case POSTPAID_TELKOM: return ProductType.TELKOM;
            case POSTPAID_MULTIFINANCE: return ProductType.MULTIFINANCE;
            default: return null;
        }
    }

    public String toExportPdf() {
        switch (this) {
            case PREPAID_PULSA: return "prepaid-pulsa-exportpdf";
            case PREPAID_DATA: return "prepaid-data-exportpdf";
            case PREPAID_PLN: return "prepaid-tokenpln-exportpdf";
            case PREPAID_TOPUPGAMES: return "prepaid-tgames-exportpdf";
            case PREPAID_VOUCHERGAMES: return "prepaid-vgames-exportpdf";
            case PREPAID_EMONEY: return "prepaid-emoney-exportpdf";
            case PREPAID_BANKTRANSFER: return "prepaid-banktransfer-exportpdf";
            case POSTPAID_CELL: return "postpaid-cellular-exportpdf";
            case POSTPAID_PLN: return "postpaid-pln-exportpdf";
            case POSTPAID_PGN: return "postpaid-pgn-exportpdf";
            case POSTPAID_BPJS: return "postpaid-bpjs-exportpdf";
            case POSTPAID_PDAM: return "postpaid-pdam-exportpdf";
            case POSTPAID_TV: return "postpaid-tv-exportpdf";
            case POSTPAID_TELKOM: return "postpaid-telkom-exportpdf";
            case POSTPAID_MULTIFINANCE: return "postpaid-multifinance-exportpdf";
            default: return null;
        }
    }

    public String toAgenDetail() {
        switch (this) {
            case PREPAID_PULSA: return "prepaid-pulsa";
            case PREPAID_DATA: return "prepaid-data";
            case PREPAID_PLN: return "prepaid-tokenpln";
            case PREPAID_TOPUPGAMES: return "prepaid-tgames";
            case PREPAID_VOUCHERGAMES: return "prepaid-vgames";
            case PREPAID_EMONEY: return "prepaid-emoney";
            case PREPAID_BANKTRANSFER: return "prepaid-banktransfer";
            case POSTPAID_CELL: return "postpaid-cellular";
            case POSTPAID_PLN: return "postpaid-pln";
            case POSTPAID_PGN: return "postpaid-pgn";
            case POSTPAID_BPJS: return "postpaid-bpjs";
            case POSTPAID_PDAM: return "postpaid-pdam";
            case POSTPAID_TV: return "postpaid-tv";
            case POSTPAID_TELKOM: return "postpaid-telkom";
            case POSTPAID_MULTIFINANCE: return "postpaid-multifinance";
            default: return null;
        }
    }

    public String toChoosePaymentType() {
        switch (this) {
            case PREPAID_PULSA: return "mobile_credit";
            case PREPAID_DATA: return "mobile_data";
            case PREPAID_PLN: return "electrical_token";
            case PREPAID_COUPON: return "coupon";
            case PREPAID_TOPUPGAMES: return "tgames";
            case PREPAID_VOUCHERGAMES: return "vgames";
            case PREPAID_EMONEY: return "emoney";
            case PREPAID_BANKTRANSFER: return "banktransfer";
            case POSTPAID_CELL: return "postpaid_data";
            case POSTPAID_PLN: return "electrical";
            case POSTPAID_PGN: return "gas";
            case POSTPAID_BPJS: return "insurance";
            case POSTPAID_PDAM: return "water";
            case POSTPAID_TV: return "tv";
            case POSTPAID_TELKOM: return "phone";
            case POSTPAID_MULTIFINANCE: return "multifinance";
            default: return null;
        }
    }

    public String toUrlPost(String cdeProduct) {
        switch (this) {
            case PREPAID_PULSA: return "/mobile/prepaid/pulsa-transaction";
            case PREPAID_DATA: return "/mobile/prepaid/data-transaction";
            case PREPAID_PLN: return "/mobile/prepaid/tokenpln-transaction";
            case PREPAID_COUPON: return "/mobile/prepaid/coupon-transaction";
            case PREPAID_TOPUPGAMES: return "/mobile/prepaid/tgames-transaction";
            case PREPAID_VOUCHERGAMES: return "/mobile/prepaid/vgames-transaction";
            case PREPAID_EMONEY: return "/mobile/prepaid/emoney-transaction";
            case PREPAID_BANKTRANSFER: return "/mobile/prepaid/banktransfer-transaction";
            case POSTPAID_CELL: return "/mobile/postpaid/cellular-transaction";
            case POSTPAID_PLN: return "/mobile/postpaid/pln-transaction";
            case POSTPAID_PGN: return "/mobile/postpaid/pgn-transaction";
            case POSTPAID_BPJS: return "/mobile/postpaid/bpjs-transaction";
            case POSTPAID_PDAM: return "/mobile/postpaid/pdam-transaction";
            case POSTPAID_TV: return "/mobile/postpaid/tv-transaction";
            case POSTPAID_TELKOM: return "/mobile/postpaid/telkom-transaction";
            case POSTPAID_MULTIFINANCE: {
                if(cdeProduct.equals("FNCOLUMD") || cdeProduct.equals("FNWOMD"))
                    return "/mobile/postpaid/multifinance-transaction-wom";
                else if(cdeProduct.equals("FNMAF") || cdeProduct.equals("FNMEGA"))
                    return "/mobile/postpaid/multifinance-transaction-maf";
                else return null;
            }
            default: return null;
        }
    }

    public String toUrlPay(String cdeProduct) {
        switch (this) {
            case PREPAID_PULSA: return "/mobile/prepaid/topup-pulsa";
            case PREPAID_DATA: return "/mobile/prepaid/topup-data";
            case PREPAID_PLN: return "/mobile/prepaid/topup-tokenpln";
            case PREPAID_COUPON: return "/mobile/prepaid/topup-coupon";
            case PREPAID_TOPUPGAMES: return "/mobile/prepaid/topup-tgames";
            case PREPAID_VOUCHERGAMES: return "/mobile/prepaid/topup-vgames";
            case PREPAID_EMONEY: return "/mobile/prepaid/topup-emoney";
            case PREPAID_BANKTRANSFER: return "/mobile/prepaid/topup-banktransfer";
            case POSTPAID_CELL: return "/mobile/postpaid/cellular-payment";
            case POSTPAID_PLN: return "/mobile/postpaid/pln-payment";
            case POSTPAID_PGN: return "/mobile/postpaid/pgn-payment";
            case POSTPAID_BPJS: return "/mobile/postpaid/bpjs-payment";
            case POSTPAID_PDAM: return "/mobile/postpaid/pdam-payment";
            case POSTPAID_TV: return "/mobile/postpaid/tv-payment";
            case POSTPAID_TELKOM: return "/mobile/postpaid/telkom-payment";
            case POSTPAID_MULTIFINANCE: {
                if(cdeProduct.equals("FNCOLUMD") || cdeProduct.equals("FNWOMD"))
                    return "/mobile/postpaid/multifinance-payment-wom";
                else if(cdeProduct.equals("FNMAF") || cdeProduct.equals("FNMEGA"))
                    return "/mobile/postpaid/multifinance-payment-maf";
                else return null;
            }
            default: return null;
        }
    }
}