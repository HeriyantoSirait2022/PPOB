package com.qdi.rajapay.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.OrderData;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @module 6.0 Kasir
 * @screen 6.18.4
 *
 * @module 5.1 Transaksi
 * @screen 5.1.6*
 */
public class PrintChooseDeviceActivity extends BaseActivity {

    RecyclerView list;
    TextView total_price, order_id, order_date, order_status;
    Button add_connection;
    ImageView copy_transaction_id;

    PrintChooseDeviceAdapter adapter;
    RecyclerView.LayoutManager layout_manager;

    OrderData data;

    public static int REQUEST_BLUETOOTH = 1;

    BluetoothAdapter mBluetoothAdapter;
    List<BluetoothDevice> mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_choose_device);

        init_toolbar(getResources().getString(R.string.activity_title_print));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        add_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @author Eliza
                 * @patch FR19022
                 * @notes 0911254321-213 W14 Bluetooth search intent to settings
                 * @previoys
                 * startActivity(new Intent(PrintChooseDeviceActivity.this, PrintAddDeviceActivity.class)
                .* putExtra("data",data));
                 */
                // <code>
                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settingsIntent);
                // </code>

            }
        });

        adapter.setOnItemClickListener(new PrintChooseDeviceAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(PrintChooseDeviceActivity.this, PrintConfirmationActivity.class)
                        .putExtra("data",data)
                        .putExtra("device",mDeviceList.get(position)));
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
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        total_price = findViewById(R.id.total_price);
        add_connection = findViewById(R.id.add_connection);
        order_id = findViewById(R.id.order_id);
        order_status = findViewById(R.id.order_status);
        order_date = findViewById(R.id.order_date);

        copy_transaction_id = findViewById(R.id.copy_transaction_id);

        mDeviceList = new ArrayList<>();

        data = (OrderData) getIntent().getSerializableExtra("data");

        adapter = new PrintChooseDeviceAdapter(mDeviceList,this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        order_id.setText("Order ID #" + data.getIdOrder());
        order_status.setText(data.getStatus().toDisplayString());
        order_date.setText(data.getPrintFormattedDateOrder1());

        /**
         * @authors : Jacqueline
         * @notes : simplified code using base class function
         */
        // <code>
        int statusColor = getStatusColor(data.getStatus());
        // </code>
        order_status.setBackgroundColor(getResources().getColor(statusColor));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            show_error_message(layout, getString(R.string.print_device_bluetooth_off));
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {

            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);

        } else {

            discoverDeviceList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        discoverDeviceList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BLUETOOTH && resultCode == RESULT_OK) {

            discoverDeviceList();
        }
    }

    private void discoverDeviceList() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mDeviceList = new ArrayList<BluetoothDevice>();
        for(BluetoothDevice bt : pairedDevices)
            mDeviceList.add(bt);

        adapter.setArr(mDeviceList);
    }
}