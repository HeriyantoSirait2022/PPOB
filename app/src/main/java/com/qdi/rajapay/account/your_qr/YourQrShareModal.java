package com.qdi.rajapay.account.your_qr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class YourQrShareModal extends BottomSheetDialogFragment {
    RecyclerView list;

    YourQrShareAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    YourQrIndexActivity parent;

    OutputStream outputStream;
    InputStream inStream;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.your_qr_share_modal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
            init_bluetooth();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new YourQrShareAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    Bitmap icon = parent.get_qr_bitmap(parent.data.getString("id"));
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "rajapay_qr_code.jpg");
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/rajapay_qr_code.jpg"));
                    startActivity(Intent.createChooser(intent, "Share with"));
                } catch (JSONException | WriterException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view) throws JSONException {
        list = view.findViewById(R.id.list);

        parent = (YourQrIndexActivity) getActivity();

        prepare_data();
        adapter = new YourQrShareAdapter(array,parent);
        layout_manager = new GridLayoutManager(parent,3);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.gmail_label));
        jsonObject.put("image", R.drawable.logos_google_gmail);
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.bluetooth_label));
        jsonObject.put("image", R.drawable.logos_bluetooth);
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.note_label));
        jsonObject.put("image", R.drawable.logos_google_optimize);
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.maps_label));
        jsonObject.put("image", R.drawable.logos_mapzen);
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.facebook_label));
        jsonObject.put("image", R.drawable.logos_facebook);
        array.add(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("text", getResources().getString(R.string.drive_label));
        jsonObject.put("image", R.drawable.logos_google_drive);
        array.add(jsonObject);
    }

    private void init_bluetooth() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = (Object []) bondedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[0];
                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }

    public void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    public void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}