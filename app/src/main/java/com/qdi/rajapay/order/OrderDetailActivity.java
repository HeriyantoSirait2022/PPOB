package com.qdi.rajapay.order;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.TransactionAPI;
import com.qdi.rajapay.contact_us.ContactUsListActivity;
import com.qdi.rajapay.coupon.home.CouponHomeActivity;
import com.qdi.rajapay.deposit.TopUpActivity;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.main_menu.bank_transfer.BankTransferSelectActivity;
import com.qdi.rajapay.main_menu.electrical.ElectricalInputNoActivity;
import com.qdi.rajapay.main_menu.electrical_token.ElectricalTokenInputNoActivity;
import com.qdi.rajapay.main_menu.emoney.EmoneySelectActivity;
import com.qdi.rajapay.main_menu.games.PrepaidGamesSelectActivity;
import com.qdi.rajapay.main_menu.gas.GasInputNoActivity;
import com.qdi.rajapay.main_menu.insurance.InsuranceInputNoActivity;
import com.qdi.rajapay.main_menu.multifinance.MultifinanceChooseProviderActivity;
import com.qdi.rajapay.main_menu.phone.PhoneChooseProviderActivity;
import com.qdi.rajapay.main_menu.postpaid_data.PostpaidDataChooseProviderActivity;
import com.qdi.rajapay.main_menu.prepaid_data.PrepaidDataInputPhoneNoActivity;
import com.qdi.rajapay.main_menu.prepaid_mobile_credit.PrepaidMobileCreditInputPhoneNoActivity;
import com.qdi.rajapay.main_menu.tv.TvChooseProviderActivity;
import com.qdi.rajapay.main_menu.water.WaterChooseAreaActivity;
import com.qdi.rajapay.model.BaseResponseData;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.model.OrderPrintData;
import com.qdi.rajapay.model.PriceData;
import com.qdi.rajapay.model.enums.ProductType;
import com.qdi.rajapay.model.enums.ResponseCode;
import com.qdi.rajapay.model.enums.TransactionStatus;
import com.qdi.rajapay.model.enums.TransactionType;
import com.qdi.rajapay.payment.ChoosePaymentActivity;
import com.qdi.rajapay.payment.SuccessActivity;
import com.qdi.rajapay.payment.alfamart.AlfamartPayContActivity;
import com.qdi.rajapay.payment.bank_transfer.BankTransferConfirmPayModal;
import com.qdi.rajapay.payment.bank_transfer.BankTransferPayContActivity;
import com.qdi.rajapay.payment.qris.QrisPayContActivity;
import com.qdi.rajapay.payment.virtual_account.VirtualAccountPayContActivity;
import com.qdi.rajapay.print.PrintManageSellPriceActivity;
import com.qdi.rajapay.print.PrintOrderOverviewActivity;
import com.qdi.rajapay.utils.NumberUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 5.1 Transaksi
 * @screen 5.1.2
 */
public class OrderDetailActivity extends BaseActivity implements APICallback.ItemCallback<BaseResponseData> {
    RecyclerView list;
    // <code>
    TextView total_price, ref_no, order_id, order_date, order_status, payment_method, refund_reason;
    // </code>
    // <code>
    Button print, order_again, order_again_only, contact_cs;
    // </code>
    // <code>
    Button cancel_order, cancel_order_deposit, confirm_payment, continue_payment, detail_payment;
    // <code>

    ImageView img, copy_transaction_id, copy_ref_no;
    RecyclerView rvDetail;

    OrderDetailAdapter adapter;
    OrderDetailTxnAdapter detailAdapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    OrderData data;
    final int CHANGE_SELL_PRICE = 1;
    /**
     * @author Dinda
     * @note Case CICD 10244 - Handling back at order detail (refresh transaction fragment if needed)
     */
    // <code>
    boolean requiredRefresh = false;
    // </code>

    // <code>
    LinearLayout bottom_print, bottom_cancel, bottom_continue, ref, detail, bottom_order_again, refund_detail;
    // </code>

    /**
     * @author Jesslyn
     * @note Add new status for ManualAdvicePrepaid
     */
    // <code>
    LinearLayout bottom_manual;
    Button manualAdvice;
    // </code>

    TransactionAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        init_toolbar(getResources().getString(R.string.activity_title_detail_transaction));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(data.getAgenStatus()==null && !data.isDepositTxn()) {
                    navigateToSetAgenPrice();
                } else {
                    navigateToPrintOrder();
                }
            }
        });

        order_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorder();
            }
        });

        order_again_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @author Jesslyn
                 * @note if product type is deposit, check txnType. if null simulate txnType with topUpDepositBt for continue payment process
                 *       (for status waiting; its only to pass redirection requirement process at reorder function)
                 */
                // <code>
                data.simulateTypeTxnForDeposit();
                // </code>
                reorder();
            }
        });

        contact_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @author Jesslyn
                 * @note simulate null txnType for deposit transaction if txn type = null
                 *       simulation for handling toString function (txntype = null)
                 */
                // <code>
                data.simulateTypeTxnForDeposit();
                // </code>
                startActivity(new Intent(OrderDetailActivity.this, ContactUsListActivity.class)
                        .putExtra("data",data.toJsonObject().toString()));
            }
        });

        /**
         * @author Jesslyn
         * @note Add new status for ManualAdvicePrepaid
         */
        // <code>
        manualAdvice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                manualAdvice();
            }
        });
        // </code>

        confirm_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankTransferConfirmPayModal modal = new BankTransferConfirmPayModal();
                modal.setOrder_data(data.toJsonObject());
                modal.setShouldNavigateToMainOnDismiss(false);
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        continue_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continuePayment(data);
            }
        });

        detail_payment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(data.getTypeTxn() == TransactionType.TOPUP_DEPOSIT_VA){
                    startActivity(new Intent(OrderDetailActivity.this, VirtualAccountPayContActivity.class)
                        .putExtra("data", data)
                    );
                }else if(data.getTypeTxn() == TransactionType.TOPUP_DEPOSIT_BT){
                    startActivity(new Intent(OrderDetailActivity.this, BankTransferPayContActivity.class)
                            .putExtra("data", data)
                    );
                }else if(data.getTypeTxn() == TransactionType.TOPUP_DEPOSIT_RETAIL){
                    startActivity(new Intent(OrderDetailActivity.this, AlfamartPayContActivity.class)
                            .putExtra("data", data)
                    );
                }else if(data.getTypeTxn() == TransactionType.TOPUP_DEPOSIT_QRIS){
                    startActivity(new Intent(OrderDetailActivity.this, QrisPayContActivity.class)
                            .putExtra("data", data)
                    );
                }
            }
        });

        /**
         * @author Jesslyn
         * @note add new cancel order deposit (only for txntype = null)
         */
        // <code>
        cancel_order_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });
        // </code>

        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });

        copy_transaction_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clip = ClipData.newPlainText("", String.valueOf(data.getIdOrder()));
                clipboard.setPrimaryClip(clip);
                show_error_message(layout,getResources().getString(R.string.copied_clipboard_label));
            }
        });

        copy_ref_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clip = ClipData.newPlainText("", String.valueOf(data.getRef()));
                clipboard.setPrimaryClip(clip);
                show_error_message(layout,getResources().getString(R.string.copied_clipboard_label));
            }
        });
    }

    private void cancelOrder(){
        show_wait_modal();
        api.cancelOrder(data.getIdOrder(), OrderDetailActivity.this);
    }

    /**
     * @author Dinda
     * @note Case CICD 10251 - Bank Transfer confirmation bad info
     *       Case TDD 1085 - Change method behaviour to handling response.
     *       ... success : isError = false; isValid = true;
     */
    // <code>
    public void show_success(JSONObject data) throws JSONException{
        if(data.has("isError") && data.getBoolean("isError") == false){
            if(data.has("isValid") && data.getBoolean("isValid") == true){
                show_error_message(layout,getStr(R.string.s_payment_accepted));
                requiredRefresh = true;

                // Trannsaction success, change status and UI
                onStatusChange(TransactionStatus.SUCCESS);
            }
            return;
        }
        show_error_message(layout,getStr(R.string.f_payment_accepted));
    }
    // </code>

    /**
     * @author liao mei
     * @note this function intended for manual confirmation which is in android status waiting but
     *       actually it already success cause callback from bank transfer
     */
    // <code>
    public void show_already_success(String message){
        show_error_message(layout,message);
        requiredRefresh = true;

        // Trannsaction success, change status and UI
        onStatusChange(TransactionStatus.SUCCESS);

        return;
    }
    // </code>

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        total_price = findViewById(R.id.total_price);
        ref_no = findViewById(R.id.ref_no);
        // <code>
        refund_reason = findViewById(R.id.refund_reason);
        // </code>
        print = findViewById(R.id.print);
        order_again = findViewById(R.id.order_again);
        // <code>
        order_again_only = findViewById(R.id.order_again_only);
        // </code>
        order_id = findViewById(R.id.order_id);
        order_status = findViewById(R.id.order_status);
        order_date = findViewById(R.id.order_date);
        contact_cs = findViewById(R.id.contact_cs);
        img = findViewById(R.id.product_image);
        ref = findViewById(R.id.ref);
        // <code>
        refund_detail = findViewById(R.id.refund_detail);
        // </code>
        detail = findViewById(R.id.detail);
        copy_transaction_id = findViewById(R.id.copy_transaction_id);
        copy_ref_no = findViewById(R.id.copy_ref_no);
        payment_method = findViewById(R.id.payment_method);
        rvDetail = findViewById(R.id.rv_detail);

        bottom_cancel = findViewById(R.id.bottom_cancel);
        bottom_print = findViewById(R.id.bottom_print);
        bottom_continue = findViewById(R.id.bottom_continue_payment);
        // <code>
        bottom_order_again = findViewById(R.id.bottom_order_again);
        // </code>

        /**
         * @author Jesslyn
         * @note Add new status for ManualAdvicePrepaid
         */
        // <code>
        bottom_manual = findViewById(R.id.bottom_manual);
        manualAdvice = findViewById(R.id.manual_advice);
        // <code>

        cancel_order = findViewById(R.id.cancel_order);
        /**
         * @author Jesslyn
         * @note add new cancel order for deposit txn only for txntype = null (special case)
         */
        // <code>
        cancel_order_deposit = findViewById(R.id.cancel_order_deposit);
        // </code>
        confirm_payment = findViewById(R.id.confirm_payment);
        continue_payment = findViewById(R.id.continue_payment);

        /**
         * @author Jesslyn
         * @note 0911254321-214 D01 Add "detail pembayaran" screen at 5.1.2 when payment transaction status "Menunggu Pembayaran" and payment type "Deposit"
         */
        // <code>
        detail_payment = findViewById(R.id.detail_payment);
        // </code>

        data = (OrderData) getIntent().getSerializableExtra("data");

        // Detail Pembayaran
        adapter = new OrderDetailAdapter(data.getBreakdownPrice(),this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        // Detail Pesanan
        List<Pair<String, String>> detailInfo = data.getDetailInfo(this);
        detailAdapter = new OrderDetailTxnAdapter(detailInfo);
        rvDetail.setAdapter(detailAdapter);
        rvDetail.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        detail.setVisibility(detailInfo.size()>0?View.VISIBLE:View.GONE);
        total_price.setText("Rp. "+data.getFormattedTotBillAmount());
        /**
         * @author Jesslyn
         * @note  Deposit transaction no need set up ref_no.
         *        This Condition also for handling NPE when deposit data with txnType = null.
         *
         */
        // <code>
        if(data.getProductTypeData() != ProductType.DEPOSIT){
            // set ref_no if productType != deposit
            ref_no.setText(data.getRef());
        }
        // </code>
        order_id.setText("Order ID #" + data.getIdOrder());
        order_status.setText(data.getStatus().toDisplayString());
        order_date.setText(data.getFormattedDateOrder2());

        String paymentMethod = data.getPaymentMtd() == null ? "" : data.getPaymentMtd().toDisplayString();
        payment_method.setText(paymentMethod);

        /**
         * @authors : Jacqueline
         * @notes : 1. change code using base class function
         *          2. set label refund reason if transaction status is refund
         */
        // <code>
        int statusColor = getStatusColor(data.getStatus());
        if(data.getStatus() == TransactionStatus.REFUND){
            refund_reason.setText(data.getRefundReason());
        }
        // </code>
        order_status.setBackgroundColor(getResources().getColor(statusColor));

        /**
         * @author Jesslyn
         * @note 1. if data not contains any string of image link then set default image
         *       2. optimizing UI
         */
        // <code>
        if(!isNullOrEmpty(data.getImage()))
            Picasso.get().load(
                    data.getImage())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(img);
        else
            img.setImageDrawable(getDrawable(R.drawable.ic_icon_detail_transaksihdpi));
        // </code>

        setupBottomBar(data.getStatus());

        api = new TransactionAPI(this, user_SP);
    }

    /**
     * @author Jesslyn
     * @note beware for using bottom continue set visible, default cancel order deposit is gone, always call this function if you new to set bottom_continue set visible
     */
    // <code>
    private void setBottomContinueCancelOrderForDeposit(){
        if(bottom_continue.getVisibility() == View.VISIBLE){
            if(data.getProductTypeData() == ProductType.DEPOSIT){
                cancel_order_deposit.setVisibility(View.VISIBLE);
            }else{
                cancel_order_deposit.setVisibility(View.GONE);
            }
        }
    }
    // </code>

    /**
     * @author Jesslyn
     * @note beware for set bottom cancel visible, default for confirm_payment is gone. its only appears for txn type Bank Transfer
     */
    // <code>
    private void setBottomCancelForBtTransfer(){
        if(bottom_cancel.getVisibility() == View.VISIBLE){
            /**
             * @author Jesslyn
             * @note uncomment this section if wanna set confirm payment button visible
             */
            // <code>
            /*
            if(data.getTypeTxn() != null && data.getTypeTxn() == TransactionType.TOPUP_DEPOSIT_BT){
                confirm_payment.setVisibility(View.VISIBLE);
            }else{
                confirm_payment.setVisibility(View.GONE);
            }
            */
            // </code>
        }
    }
    // </code>

    private void setupBottomBar(TransactionStatus status) {
        if(status == TransactionStatus.SUCCESS){
            bottom_print.setVisibility(View.VISIBLE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.GONE);
            // <code>
            bottom_order_again.setVisibility(View.GONE);
            refund_detail.setVisibility(View.GONE);
            // </code>
            ref.setVisibility(View.VISIBLE);
            bottom_manual.setVisibility(View.GONE);

            /**
             * @author Jesslyn
             * @note for case PDAM UAT (transaction suspected no need to print receipt)
             */
            // <code>
            if(data.getResponseCode() != null && data.getResponseCode() == ResponseCode.TRANSACTION_SUSPECTED){
                bottom_print.setVisibility(View.GONE);
                bottom_order_again.setVisibility(View.VISIBLE);
            }
            // </code>

            /**
             * @author Eliza Sutantya
             * @patch FR19022
             * @notes if prepaid coupon then hide button cetak & catat transaksi
             */
            // <code>
            if(data.getTypeTxn() != null && data.getTypeTxn() == TransactionType.PREPAID_COUPON){
                bottom_print.setVisibility(View.GONE);
                bottom_order_again.setVisibility(View.VISIBLE);
            }
            // </code>
        }else if(status == TransactionStatus.CANCEL){
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.GONE);
            // <code>
            bottom_order_again.setVisibility(View.VISIBLE);
            refund_detail.setVisibility(View.GONE);
            // </code>
            ref.setVisibility(View.VISIBLE);
            bottom_manual.setVisibility(View.GONE);
        }else if(status == TransactionStatus.WAITING && data.isDepositTxn()){
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.VISIBLE);
            /**
             * @author Jesslyn
             * @note beware for set bottom cancel visible, default for confirm_payment is gone. its only appears for txn type Bank Transfer
             */
            // <code>
            setBottomCancelForBtTransfer();
            // </code>
            bottom_continue.setVisibility(View.GONE);
            // <code>
            bottom_order_again.setVisibility(View.GONE);
            refund_detail.setVisibility(View.GONE);
            // </code>
            ref.setVisibility(View.GONE);
            bottom_manual.setVisibility(View.GONE);
        }else if(status == TransactionStatus.WAITING){
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.VISIBLE);
            /**
             * @author Jesslyn
             * @note beware for using bottom_conitue, default cancel order deposit is gone, always call this function if you new to set bottom_continue set visible
             */
            // <code>
            setBottomContinueCancelOrderForDeposit();
            // </code>
            // <code>
            bottom_order_again.setVisibility(View.GONE);
            refund_detail.setVisibility(View.GONE);
            // </code>
            ref.setVisibility(View.GONE);
            bottom_manual.setVisibility(View.GONE);
        }else if(status == TransactionStatus.FAILED) {
            /**
             * @authors : Cherry
             * @notes : disable button cetak dan catat transaksi if transaction failed, request Mr. Yo. Button order again added
             */
            // <code>
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.GONE);
            bottom_order_again.setVisibility(View.VISIBLE);
            refund_detail.setVisibility(View.GONE);
            ref.setVisibility(View.GONE);
            bottom_manual.setVisibility(View.GONE);
            // </code>
        }else if(status == TransactionStatus.REFUND){
            /**
             * @authors : liao mei
             * @notes : if status refund, display refund notes with button order again
             */
            // <code>
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.GONE);
            bottom_order_again.setVisibility(View.VISIBLE);
            refund_detail.setVisibility(View.VISIBLE);
            ref.setVisibility(View.GONE);
            bottom_manual.setVisibility(View.GONE);
            // </code>
        }else if(status == TransactionStatus.EXPIRED){
            /**
             * @authors : Yohanes AI
             * @notes : if status expired, display only order button again
             */
            // <code>
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.GONE);
            bottom_order_again.setVisibility(View.VISIBLE);
            refund_detail.setVisibility(View.GONE);
            ref.setVisibility(View.GONE);
            bottom_manual.setVisibility(View.GONE);
            // </code>
        }else if(status == TransactionStatus.PROCESS){
            /**
             * @authors : Yohanes AI
             * @notes : if status proccessed (only for otomax), display only order button again
             */
            // <code>
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.GONE);
            bottom_order_again.setVisibility(View.VISIBLE);
            refund_detail.setVisibility(View.GONE);
            ref.setVisibility(View.GONE);
            bottom_manual.setVisibility(View.GONE);
            // </code>
        }else if(status == TransactionStatus.MANUAL){
            /**
             * @author Jesslyn
             * @note Add new status for ManualAdvicePrepaid
             */
            // <code>
            bottom_print.setVisibility(View.GONE);
            bottom_cancel.setVisibility(View.GONE);
            bottom_continue.setVisibility(View.GONE);
            bottom_order_again.setVisibility(View.GONE);
            refund_detail.setVisibility(View.GONE);
            ref.setVisibility(View.GONE);
            bottom_manual.setVisibility(View.VISIBLE);
            // </code>
        }

        /**
         * @authors : Jacqueline
         * @notes : simplified code using base class function
         */
        // <code>
        int statusColor = getStatusColor(data.getStatus());
        order_status.setBackgroundColor(getResources().getColor(statusColor));
        // </code>

        /**
         * @author Jesslyn
         * @note move snippet code here to handling activity refresh (BaseResponseData Callback)
         */
        // <code>
        if(data.isDepositTxn() || data.getProductTypeData() == ProductType.DEPOSIT) {
            ref.setVisibility(View.GONE);
            print.setVisibility(View.GONE);
        }
        // </code>
    }

    @Override
    public void onItemResponseSuccess(BaseResponseData item, String message) {
        dismiss_wait_modal();

        requiredRefresh = true;
        onStatusChange(TransactionStatus.CANCEL);
    }

    private void onStatusChange(TransactionStatus status){
        data.setStatus(status);
        setupBottomBar(data.getStatus());
        order_status.setText(data.getStatus().toDisplayString());
    }

    private void handlingBackPressed(){
        user_edit_SP.putString("bottom_main_menu","order");
        user_edit_SP.commit();

        startActivity(new Intent(this, MainActivity.class)
                .putExtra("has_screen_request", true)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        /**
         * @author Jesslyn
         * @note add new transaction status to back to home fragment
         *
         * @author Dinda
         * @note remove status checking from order detail, set to order fragment. send extras has_screen_request -> handling at MainActivity
         * <code>
         *     if(data.getStatus() == TransactionStatus.WAITING || data.getStatus() == TransactionStatus.CANCEL || data.getStatus() == TransactionStatus.SUCCESS) {
         *     }
         * </code>
         */
        // <code>
        // if intent has extra caller and not required to handling back pressed
        if (getIntent().hasExtra("caller") && !requiredRefresh) {
            if (getIntent().getStringExtra("caller").equalsIgnoreCase(MainActivity.class.getCanonicalName())) {
                super.onBackPressed();
                return;
            }
        }

        // else if has not extra caller and doesn't required / required handling back pressed
        handlingBackPressed();
        // </code>

        finish();
    }

    private void navigateToPrintOrder() {
        startActivity(new Intent(OrderDetailActivity.this, PrintOrderOverviewActivity.class)
                .putExtra("data", data)
                .putExtra("class", OrderDetailActivity.class.toString())
        );
    }

    private void navigateToSetAgenPrice() {
        String productName = data.getNameProduct();
        Double billAmount = data.getBillAmount();

        PriceData selected = new PriceData();
        selected.setName(productName);
        selected.setPrice(billAmount);

        Intent intent = new Intent(this, PrintManageSellPriceActivity.class);
        intent.putExtra("data", data);
        startActivityForResult(intent,CHANGE_SELL_PRICE);
    }

    /**
     * @author Jesslyn
     * @note case TDD 12491 - add new manualAdvice function
     */
    // <code>
    private void refreshOrderDetail() {
        api.getOrderDetail(data, new APICallback.ItemCallback<OrderData>() {
            @Override
            public void onItemResponseSuccess(OrderData item, String message) {
                dismiss_wait_modal();

                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("caller", "");
                intent.putExtra("data",item);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                OrderDetailActivity.this.onAPIResponseFailure(error);
            }
        });
    }

    private void manualAdvice() {
        show_wait_modal();
        api.manualAdvice(data, new APICallback.ItemCallback<OrderData>() {
            @Override
            public void onItemResponseSuccess(OrderData item, String message) {
                if(item.getResponseCode() == ResponseCode.SUCCESS){
                    try {
                        redirect_to_success();
                    } catch (JSONException e) {
                        dismiss_wait_modal();
                        e.printStackTrace();
                    }
                }else{
                    refreshOrderDetail();
                }
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                OrderDetailActivity.this.onAPIResponseFailure(error);
            }
        });
    }

    private void redirect_to_success() throws JSONException {
        dismiss_wait_modal();
        JSONObject dataTemp = new JSONObject();
        JSONObject invoiceRequest = new JSONObject();
        invoiceRequest.put("idOrder", data.getIdOrder());
        invoiceRequest.put("typeTxn", data.getTypeTxn());

        dataTemp.put("invoice_request", invoiceRequest);

        startActivity(new Intent(OrderDetailActivity.this, SuccessActivity.class)
            .putExtra("data", dataTemp.toString()));
    }
    // </code>

    private void setAgenPriceTxn(double price) {
        data.setAgenPrice(price);
        show_wait_modal();
        api.insertAgenPrice(data, new APICallback.ItemCallback<BaseResponseData>() {
            @Override
            public void onItemResponseSuccess(BaseResponseData item, String message) {
                dismiss_wait_modal();
                getOrderDetail(data);
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                OrderDetailActivity.this.onAPIResponseFailure(error);
            }
        });
    }

    private void getOrderDetail(final OrderData data) {
        // @notes : called when user had successfully set agent price, feeding screen 5.1.4
        api.getOrderDetail(data, new APICallback.ItemCallback<OrderData>() {
            @Override
            public void onItemResponseSuccess(OrderData item, String message) {
                dismiss_wait_modal();
                OrderDetailActivity.this.data = item;
                navigateToPrintOrder();
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                OrderDetailActivity.this.onAPIResponseFailure(error);
            }
        });
    }

    private void continuePayment(final OrderData data) {
        // for deposit scenario, cause deposit possible have typeTxn null
        if(data.getTypeTxn() == null) {
            navigateToChoosePayment();
            return;
        }

        // for other deposit transaction
        api.continuePayment(data, new APICallback.ItemCallback<JSONObject>() {
            @Override
            public void onItemResponseSuccess(JSONObject item, String message) {
                try {
                    navigateToPayment(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismiss_wait_modal();
                    show_error_message(OrderDetailActivity.this.layout, "Gagal melanjutkan pembayaran");
                }
            }

            @Override
            public void onAPIResponseFailure(VolleyError error) {
                dismiss_wait_modal();
                OrderDetailActivity.this.onAPIResponseFailure(error);
            }
        });
    }

    private void navigateToChoosePayment() {
        String item = getIntent().getStringExtra("item");

        startActivity(new Intent(this, ChoosePaymentActivity.class)
                .putExtra("type","deposit")
                .putExtra("caller", OrderDetailActivity.class.getCanonicalName())
                .putExtra("data", item));
    }

    private void navigateToPayment(JSONObject item) throws JSONException{
        item = adjustContinuePaymentResponseObject(item);
        if(item.has("refId") && !item.has("idRef"))
            item.put("idRef",item.getString("refId"));
        if(item.has("totBillAmountVendor") && !item.has("totBillAmount"))
            item.put("totBillAmount",item.getDouble("totBillAmountVendor"));

        /**
         * @author Dinda
         * @note case tdd 12445 - add new data
         *       1. add MSN
         *       2. add $curl_response->data->subscriberID
         *       3. add vendorAdmin
         */
        // <code>
        if(item.has("vendorAdmin"))
            item.put("vendorAdmin", item.getDouble("vendorAdmin"));
        // </code>


        String cdeProduct = item.getString("cdeProduct");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("invoice_data", item);
        jsonObject.put("url_post", data.getTypeTxn().toUrlPost(cdeProduct));
        jsonObject.put("url_pay", data.getTypeTxn().toUrlPay(cdeProduct));
        jsonObject.put("data_post", item);
        jsonObject.put("breakdown_price", new JSONArray(mapBreakdownPrice(item).toString()));

        /**
         * @author Eliza Sutantya
         * @patch FR19002
         * @note add product image to continue payment
         */
        // <code>
        if(item.has("image") && !isNullOrEmpty(item.getString("image"))){
            JSONObject data = new JSONObject();
            data.put("image_url", item.getString("image"));
            jsonObject.put("data", data);
        }
        // </code>

        startActivity(
                new Intent(this, ChoosePaymentActivity.class)
                        .putExtra("data", jsonObject.toString())
                        .putExtra("caller", OrderDetailActivity.class.getCanonicalName())
                        .putExtra("type", this.data.getTypeTxn().toChoosePaymentType()));
    }

    private List<JSONObject> mapBreakdownPrice(JSONObject jsonObject) throws JSONException {
        List<JSONObject> breakdownPrice = new ArrayList<>();

        String nameTxn = jsonObject.optString("nameTxn", "");
        String adminFeeLabel = getString(R.string.admin_cost_label);
        double billAmount = 0;
        double adminFee = 0;

        if(data.isPrepaidTxn()) {
            billAmount = jsonObject.optDouble("totBillAmountVendor", 0);
            /**
             * @author Eliza Sutantya
             * @patch FR19022
             * @notes fixing admin fee value, hide profit at pulsa, data, and token pln
             */
            // <code>
            adminFee = jsonObject.optDouble("adminFee", 0);
            // </code>

        } else {
            billAmount = jsonObject.optDouble("billAmount", 0);
            adminFee = jsonObject.optDouble("adminFee", 0);
        }

        breakdownPrice.add(new JSONObject("{\"title\":\""+nameTxn+"\",\"price\":"+billAmount+"}"));

        if(data.getTypeTxn() == TransactionType.POSTPAID_PLN) {
            double sumDenda = 0;
            for(OrderPrintData.DetailTagihan item: data.getPostpaidPLNDetailTagihan()) {
                sumDenda += item.getDenda();
            }
            breakdownPrice.add(new JSONObject("{\"title\":\"Denda\",\"name\":\"Denda\",\"price\":"+sumDenda+",\"price_str\":\""+NumberUtils.format(sumDenda)+"\"}"));
        }

        if(data.getTypeTxn() == TransactionType.POSTPAID_PDAM) {
            double sumDenda = 0;
            for(OrderPrintData.Tagihan item: data.getPostpaidTagihan()) {
                sumDenda += item.getPenalty();
            }
            breakdownPrice.add(new JSONObject("{\"title\":\"Denda\",\"name\":\"Denda\",\"price\":"+sumDenda+",\"price_str\":\""+NumberUtils.format(sumDenda)+"\"}"));
        }

        if(data.getTypeTxn() == TransactionType.POSTPAID_MULTIFINANCE) {
            /**
             * @authors : Cherry
             * @notes : fixing bug multifinance "denda" not appears. because MAF, MCF, and WOM using penalty instead of tagihan
             */
            // <code>
            double sumDenda = data.getPenalty();
            // </code>
            breakdownPrice.add(new JSONObject("{\"title\":\"Denda\",\"name\":\"Denda\",\"price\":"+sumDenda+",\"price_str\":\""+NumberUtils.format(sumDenda)+"\"}"));
        }

        if(data.getTypeTxn() == TransactionType.POSTPAID_PDAM) {
            double sumTagihanLain = 0;
            for(OrderPrintData.Tagihan item: data.getPostpaidTagihan()) {
                sumTagihanLain += item.getTagihanLain();
            }
            breakdownPrice.add(new JSONObject("{\"title\":\"Tagihan Lain\",\"name\":\"Tagihan Lain\",\"price\":"+sumTagihanLain+",\"price_str\":\""+NumberUtils.format(sumTagihanLain)+"\"}"));
        }

        if(!data.getTypeTxn().equals("TOPUPDEPOSITVA") && !data.getTypeTxn().equals("TOPUPDEPOSITBT"))
            breakdownPrice.add(new JSONObject("{\"title\":\""+adminFeeLabel+ "\",\"price\":"+adminFee+"}"));

        return  breakdownPrice;
    }

    private JSONObject adjustContinuePaymentResponseObject(JSONObject jsonObject) throws JSONException {

        if (data.getTypeTxn() == TransactionType.PREPAID_DATA) {
            jsonObject.put("codeData", jsonObject.optString("codeDataPulsa", ""));
            jsonObject.put("vendorAmount", jsonObject.optString("totBillAmountVendor", ""));
        }

        if (data.getTypeTxn() == TransactionType.PREPAID_PULSA) {
            jsonObject.put("codePulsa", jsonObject.optString("codeDataPulsa", ""));
            jsonObject.put("vendorAmount", jsonObject.optString("totBillAmountVendor", ""));
        }

        if (data.isPostpaidTxn()) {
            jsonObject.put("vendorAmount", jsonObject.optString("billAmount", ""));
        }

        return jsonObject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == CHANGE_SELL_PRICE){
            double agenPrice = data.getDoubleExtra("sell_price", 0);
            setAgenPriceTxn(agenPrice);
        }
    }

    private void reorder() {
        switch (data.getTypeTxn()) {
            case TOPUP_GENERAL:
            case TOPUP_DEPOSIT_BT:
            case TOPUP_DEPOSIT_VA:
            case TOPUP_DEPOSIT_QRIS:
            case TOPUP_DEPOSIT_RETAIL:
                reorderDeposit(); break;
            case PREPAID_DATA: reorderPrepaidData();break;
            case PREPAID_PULSA: reorderPrepaidPulsa();break;
            case PREPAID_PLN: reorderPrepaidTokenPLN();break;
            case PREPAID_COUPON: reorderPrepaidCoupon();break;
            case PREPAID_TOPUPGAMES:
            case PREPAID_VOUCHERGAMES: reorderPrepaidGames();break;
            case PREPAID_EMONEY: reorderPrepaidEmoney(); break;
            case PREPAID_BANKTRANSFER: reorderPrepaidBankTransfer(); break;
            case POSTPAID_CELL: reorderPostpaidCellular();break;
            case POSTPAID_BPJS: reorderPostpaidBPJS();break;
            case POSTPAID_MULTIFINANCE: reorderPostpaidMultifinance();break;
            case POSTPAID_PDAM: reorderPostpaidPDAM();break;
            case POSTPAID_PGN: reorderPostpaidPGN();break;
            case POSTPAID_PLN: reorderPostpaidPLN();break;
            case POSTPAID_TELKOM: reorderPostpaidTelkom();break;
            case POSTPAID_TV: reorderPostpaidTv();break;
            default:break;
        }
    }


    private void reorderDeposit() {
        startActivity(new Intent(this, TopUpActivity.class));
    }

    private void reorderPrepaidData() {
        startActivity(new Intent(this, PrepaidDataInputPhoneNoActivity.class)
                .putExtra("no_hp", data.getNoHp())
        );
    }

    private void reorderPrepaidPulsa() {
        startActivity(new Intent(this, PrepaidMobileCreditInputPhoneNoActivity.class)
                .putExtra("no_hp", data.getNoHp())
        );
    }

    private void reorderPrepaidTokenPLN() {
        startActivity(new Intent(this, ElectricalTokenInputNoActivity.class)
                        .putExtra("no_cust", data.getNoCust()));
    }

    private void reorderPrepaidCoupon() {
        startActivity(new Intent(this, CouponHomeActivity.class));
    }

    private void reorderPrepaidGames(){
        startActivity(new Intent(this, PrepaidGamesSelectActivity.class));
    }

    private void reorderPrepaidEmoney(){
        startActivity(new Intent(this, EmoneySelectActivity.class));
    }

    private void reorderPrepaidBankTransfer(){
        startActivity(new Intent(this, BankTransferSelectActivity.class));
    }

    private void reorderPostpaidCellular() {
        startActivity(new Intent(this, PostpaidDataChooseProviderActivity.class));
    }

    private void reorderPostpaidPLN() {
        startActivity(new Intent(this, ElectricalInputNoActivity.class));
    }

    private void reorderPostpaidPGN() {
        startActivity(new Intent(this, GasInputNoActivity.class));
    }

    private void reorderPostpaidBPJS() {
        startActivity(new Intent(this, InsuranceInputNoActivity.class));
    }

    private void reorderPostpaidPDAM() {
        startActivity(new Intent(this, WaterChooseAreaActivity.class));
    }

    private void reorderPostpaidTv() {
        startActivity(new Intent(this, TvChooseProviderActivity.class));
    }

    private void reorderPostpaidTelkom() {
        startActivity(new Intent(this, PhoneChooseProviderActivity.class));
    }

    private void reorderPostpaidMultifinance() {
        startActivity(new Intent(this, MultifinanceChooseProviderActivity.class));
    }
}