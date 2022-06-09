package com.qdi.rajapay.print;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.OrderData;
import com.qdi.rajapay.order.OrderDetailActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @module 5.1 Transaksi
 * @screen 5.1.4
 *
 * @module 6.0 Kasir
 * @screen 6.18.2
 */

public class PrintOrderOverviewActivity extends BaseActivity {
    RecyclerView list;
    TextView total_price, ref_no, order_id, order_date, order_status, title_mode, payment_method;
    TextView print_order_id, print_qty, print_order_time, print_order_date;
    Button pdf, choose_device;
    LinearLayout ref_no_layout;
    View order_overview, order_print;
    ImageView productImage, copy_transaction_id, copy_ref_no;

    PrintOrderOverviewAdapter adapter;
    RecyclerView.LayoutManager layout_manager;

    OrderData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_order_overview);

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrintOrderOverviewActivity.this, PrintSavePDFActivity.class)
                        .putExtra("data",data));
            }
        });

        choose_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrintOrderOverviewActivity.this, PrintChooseDeviceActivity.class)
                        .putExtra("data",data));
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

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        total_price = findViewById(R.id.total_price);
        ref_no = findViewById(R.id.ref_no);
        pdf = findViewById(R.id.pdf);
        choose_device = findViewById(R.id.choose_device);
        order_status = findViewById(R.id.order_status);
        productImage = findViewById(R.id.product_image);
        copy_transaction_id = findViewById(R.id.copy_transaction_id);
        copy_ref_no = findViewById(R.id.copy_ref_no);

        title_mode = findViewById(R.id.title_mode);
        payment_method = findViewById(R.id.payment_method);

        ref_no_layout = findViewById(R.id.ref_no_layout);
        order_overview = findViewById(R.id.order_overview);
        order_print = findViewById(R.id.order_print);

        data = (OrderData) getIntent().getSerializableExtra("data");
        adapter = new PrintOrderOverviewAdapter(data.getPrintOverviewBreakdownPrice(),this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        /**
         * @author : jesslyn
         * @notes : important part, required to update. please use this code
         * <code>
         */
        if(data.getSellingData() != null)
            total_price.setText("Rp. "+ data.getSellingData().getFormattedTxnSellPrice());
        else
            total_price.setText("Rp. "+ data.getFormattedTotBillAmount());
        // </code>

        String prevClass = getIntent().getStringExtra("class");
        if(prevClass.equals(OrderDetailActivity.class.toString())) {
            // 5.1.4
            init_toolbar(getResources().getString(R.string.activity_title_detail_transaction));
            productImage.setVisibility(View.VISIBLE);
            order_overview.setVisibility(View.VISIBLE);
            order_print.setVisibility(View.GONE);
            ref_no_layout.setVisibility(View.VISIBLE);
            title_mode.setVisibility(View.VISIBLE);
            payment_method.setVisibility(View.VISIBLE);

            title_mode.setText(getResources().getText(R.string.order_detail_transaction_payment_detail));

            String paymentMethod = data.getPaymentMtd() == null ? "" : data.getPaymentMtd().toDisplayString();
            payment_method.setText(paymentMethod);

            Picasso.get().load(data.getImage()).into(productImage);
            ref_no.setText(data.getRef());
            order_status.setText(data.getStatus().toDisplayString());

            /**
             * @authors : Jacqueline
             * @notes : simplified code using base class function
             */
            // <code>
            int statusColor = getStatusColor(data.getStatus());
            // </code>
            order_status.setBackgroundColor(getResources().getColor(statusColor));

            /**
             * @author : jesslyn
             * @notes : important part, required to update. please use this code
             * <code>
             */
            order_id = findViewById(R.id.order_id);
            order_id.setText("Order ID #" + data.getIdOrder());

            order_date = findViewById(R.id.order_date);
            order_date.setText(data.getPrintFormattedDateOrder1());
            // </code>
        }
        else {
            // 6.18.2
            init_toolbar(getResources().getString(R.string.activity_title_report_detail));
            productImage.setVisibility(View.GONE);
            order_overview.setVisibility(View.GONE);
            order_print.setVisibility(View.VISIBLE);
            ref_no_layout.setVisibility(View.GONE);

            title_mode.setVisibility(View.VISIBLE);
            payment_method.setVisibility(View.GONE);

            title_mode.setText(getResources().getText(R.string.print_order_overview_overview_bill));

            /**
             * @author : jesslyn
             * @notes : important part, required to update. please use this code
             * <code>
             */
            print_order_id = findViewById(R.id.print_order_id);
            print_order_id.setText("Tagihan " + data.getIdOrder());

            print_order_date = findViewById(R.id.print_order_date);
            print_order_date.setText(data.getPrintFormattedDateOrder2());

            print_order_time = findViewById(R.id.print_order_time);
            print_order_time.setText(data.getPrintFormattedTimeOrder());
            // </code>
        }
    }
}