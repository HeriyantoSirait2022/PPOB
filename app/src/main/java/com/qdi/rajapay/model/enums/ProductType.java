package com.qdi.rajapay.model.enums;

public enum ProductType {
    /**
     * @author Jesslyn
     * @note add new enum to provide function convert String OrderId::OrderData to ProductType
     */
    // <code>
    // tbl_deposit_txn
    DEPOSIT,
    //tbl_prepaid_txn
    PREPAID,
    // tbl_postpaid_txn
    POSTPAID,
    // tbl_upgrade_account_txn
    UPGRADE,
    // tbl_transfer_deposit_txn
    TRANSFER,
    // tbl_manual_deposit_txn
    TOPUP,
    // </code>

    DATA,
    PULSA,
    TOKENPLN,
    COUPON,
    TOPUPGAMES,
    VOUCHERGAMES,
    EMONEY,
    BANKTRANSFER,

    BPJS,
    MULTIFINANCE,
    CELLULAR,
    PDAM,
    PGN,
    PLN,
    TELKOM,
    TV;

    public String getShowProductPath() {
        switch (this) {
            case PULSA: return "prepaid-pulsa";
            case DATA: return "prepaid-data";
            case TOKENPLN: return "prepaid-pln";
            case TOPUPGAMES: return "prepaid-tgames";
            case VOUCHERGAMES: return "prepaid-vgames";
            case EMONEY: return "prepaid-emoney";
            case BANKTRANSFER: return "prepaid-banktransfer";
            // coupon not included because coupon cannot set harga

            case CELLULAR: return "postpaid-cell";
            case PLN: return "postpaid-pln";
            case PGN: return "postpaid-pgn";
            case BPJS: return "postpaid-bpjs";
            case PDAM: return "postpaid-pdam";
            case TV: return "postpaid-tv";
            case TELKOM: return "postpaid-telkom";
            case MULTIFINANCE: return "postpaid-multifin";

            default: return null;
        }
    }

    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes from tbl_product_master.product_type to enum ProductType
     * @param str
     * @return
     */
    // <code>
    public static ProductType fromStrToProductType(String str){
        switch(str.toUpperCase()){
            case "BPJSKES" :
                return BPJS;
            case "CELL" :
                return CELLULAR;
            case "DATA":
                return DATA;
            case "MULTIFIN":
                return MULTIFINANCE;
            case "PDAM":
                return PDAM;
            case "PGN":
                return PGN;
            case "PLN":
                return PLN;
            case "PLNPREPAIDB":
                return TOKENPLN;
            case "PULSA":
                return PULSA;
            case "TELKOM":
                return TELKOM;
            case "TOPUPGAMES":
                return TOPUPGAMES;
            case "TVSUB":
                return TV;
            case "VOUCHERGAMES":
                return VOUCHERGAMES;
            case "EMONEY":
                return EMONEY;
            case "BANKTRANSFER":
                return BANKTRANSFER;
        }
        return null;
    }

    /**
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes from enum ProductType to tbl_prepaid_txn.txn_type
     */
    public String toTxnType(){
        switch (this){
            case DATA:
                return "PREPAIDDATA";
            case PULSA:
                return "PREPAIDPULSA";
            case TOKENPLN:
                return "PREPAIDPLN";
            case COUPON:
                return "PREPAIDCOUPON";
            case TOPUPGAMES:
                return "PREPAIDTOPUPGAMES";
            case VOUCHERGAMES:
                return "PREPAIDVOUCHERGAMES";
            case EMONEY:
                return "PREPAIDEMONEY";
            case BANKTRANSFER:
                return "PREPAIDBANKTRANSFER";

            case CELLULAR:
                return "POSTPAIDCELL";
            case PLN:
                return "POSTPAIDPLN";
            case PGN:
                return "POSTPAIDPGN";
            case BPJS:
                return "POSTPAIDBPJS";
            case PDAM:
                return "POSTPAIDPDAM";
            case TV:
                return "POSTPAIDTV";
            case TELKOM:
                return "POSTPAIDTELKOM";
            case MULTIFINANCE:
                return "POSTPAIDMULTIFINANCE";
        }
        return null;
    }
    // </code>

    public String getPricelistPath() {
        switch (this) {
            case BPJS: return "postpaid-pricelist/bpjs";
            case CELLULAR: return "postpaid-pricelist/cellular";
            case DATA: return "prepaid-pricelist/data";
            case TOPUPGAMES: return "prepaid-pricelist/tgames";
            case VOUCHERGAMES: return "prepaid-pricelist/vgames";
            case EMONEY: return "prepaid-pricelist/emoney";
            case BANKTRANSFER: return "prepaid-pricelist/banktransfer";
            case MULTIFINANCE: return "postpaid-pricelist/multifinance";
            case PDAM: return "postpaid-pricelist/pdam";
            case PGN: return "postpaid-pricelist/pgn";
            case PLN: return "postpaid-pricelist/pln";
            case PULSA: return "prepaid-pricelist/pulsa";
            case TELKOM: return "postpaid-pricelist/telkom";
            case TOKENPLN: return "prepaid-pricelist/tokenpln";
            case TV: return "postpaid-pricelist/tv";
            default: return "";
        }
    }

    public String getUpdatePricePath() {
        switch (this) {
            case BPJS: return "postpaid-pricelist/update-bpjs";
            case CELLULAR: return "postpaid-pricelist/update-cellular";
            case DATA: return "prepaid-pricelist/update-data";
            case TOPUPGAMES: return "prepaid-pricelist/update-tgames";
            case VOUCHERGAMES: return "prepaid-pricelist/update-vgames";
            case EMONEY: return "prepaid-pricelist/update-emoney";
            case BANKTRANSFER: return "prepaid-pricelist/update-banktransfer";
            case MULTIFINANCE: return "postpaid-pricelist/update-multifinance";
            case PDAM: return "postpaid-pricelist/update-pdam";
            case PGN: return "postpaid-pricelist/update-pgn";
            case PLN: return "postpaid-pricelist/update-pln";
            case PULSA: return "prepaid-pricelist/update-pulsa";
            case TELKOM: return "postpaid-pricelist/update-telkom";
            case TOKENPLN: return "prepaid-pricelist/update-tokenpln";
            case TV: return "postpaid-pricelist/update-tv";
            default: return "";
        }
    }

    public String getOrderDetailPath() {
        switch (this) {
            case DEPOSIT: return "deposit";
            case BPJS: return "postpaid-bpjs";
            case CELLULAR: return "postpaid-cellular";
            case DATA: return "prepaid-data";
            case COUPON: return "prepaid-coupon";
            case TOPUPGAMES: return "prepaid-tgames";
            case VOUCHERGAMES: return "prepaid-vgames";
            case EMONEY: return "prepaid-emoney";
            case BANKTRANSFER: return "prepaid-banktransfer";
            case MULTIFINANCE: return "postpaid-multifinance";
            case PDAM: return "postpaid-pdam";
            case PGN: return "postpaid-pgn";
            case PLN: return "postpaid-pln";
            case PULSA: return "prepaid-pulsa";
            case TELKOM: return "postpaid-telkom";
            case TOKENPLN: return "prepaid-tokenpln";
            case TV: return "postpaid-tv";
            default: return "";
        }
    }

    public String getOrderDetailPrintPath() {
        switch (this) {
            case BPJS: return "postpaid-bpjs-print";
            case CELLULAR: return "postpaid-cellular-print";
            case DATA: return "prepaid-data-print";
            case TOPUPGAMES: return "prepaid-tgames-print";
            case VOUCHERGAMES: return "prepaid-vgames-print";
            case EMONEY: return "prepaid-emoney-print";
            case BANKTRANSFER: return "prepaid-banktransfer-print";
            case MULTIFINANCE: return "postpaid-multifinance-print";
            case PDAM: return "postpaid-pdam-print";
            case PGN: return "postpaid-pgn-print";
            case PLN: return "postpaid-pln-print";
            case PULSA: return "prepaid-pulsa-print";
            case TELKOM: return "postpaid-telkom-print";
            case TOKENPLN: return "prepaid-tokenpln-print";
            case TV: return "postpaid-tv-print";
            default: return "";
        }
    }
}
