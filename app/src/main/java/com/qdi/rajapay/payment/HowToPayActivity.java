package com.qdi.rajapay.payment;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @module 4.1.3 Virtual Account
 * @screen 4.1.4.3.A
 * @screen 4.1.4.3.B
 * @notes this activity handling cara bayar in module 4.1.3 virtual account
 */
public class HowToPayActivity extends BaseActivity {
    TextView bank_name;
    ImageView image;
    ExpandableListView list;

    /**
     * @author Jesslyn
     * @note remove unused variable
     */
    // <code>
    String bankCode;
    String bankAccountNo;
    // </code>

    // <code>
    JSONObject data, payment_header;
    String paymentMethod = "";
    String total;
    // </code>

    HowToPayAdapter adapter;
    ArrayList<JSONObject> array_header = new ArrayList<>();
    HashMap<JSONObject,ArrayList<JSONObject>> array_child = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer_how_to_pay);

        init_toolbar(getResources().getString(R.string.activity_title_how_to_pay));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                try {
                    array_header.get(groupPosition).put("selected",false);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                try {
                    array_header.get(groupPosition).put("selected",true);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        bank_name = findViewById(R.id.bank_name);
        list = findViewById(R.id.list);
        image = findViewById(R.id.image);

        data = new JSONObject(getIntent().getStringExtra("payment"));
        /**
         * @author : Jesslyn
         * @note : Change behavior for handing intent extras. if there weren't any extras, then get data from server
         */
        // <code>
        String extras = (String) isExtras(getIntent(), "payment_header");
        if(!isNullOrEmpty(extras)){
            payment_header = new JSONObject(extras);
            paymentMethod = payment_header.getString("paymentMtd");
        }
        // </code>

        /**
         * @authors : Cherry
         * @notes : change bank name issue show only in BCA
         *
         * @author : Jesslyn
         * @note : remove unused variable of bankName and accountName
         */
        // <code>
        bankCode = data.getString("cdeBank");
        bankAccountNo = data.getString("noAcct");

        if(data.has("total"))
            // @note update total using standard currency
            total = toLocalIdr(data.getString("total"));
        // </code>

        Picasso.get()
                .load(data.getString("image_url"))
                .into(image);

        prepare_data();
        adapter = new HowToPayAdapter(this,array_header,array_child);
        list.setAdapter(adapter);
    }

    private void setupBankTransfer(JSONObject data, ArrayList<JSONObject> arr) throws JSONException{
        switch (bankCode.toUpperCase()){
            case "BCA_API" :
                bank_name.setText("TRANSFER BCA");

                // M-BCA
                data = new JSONObject();
                data.put("title", "M-BCA");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Mobile Banking<br/><br/>" +
                        "2. Pilih <b>m-Transfer</b><br/><br/>" +
                        "3. Daftarkan nomor rekening berikut: BCA a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "4. Pilih Transfer Antar Rekening<br/><br/>" +
                        "5. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "6. Pilih nomor rekening tujuan BCA a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "7. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(0), arr);

                // KlikBCA
                data = new JSONObject();
                data.put("title", "KLIKBCA");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking<br/><br/>" +
                        "2. Pilih Transaksi Dana<br/><br/>" +
                        "3. Daftarkan nomor rekening berikut: BCA a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "4. Pilih Transfer ke Rek. BCA<br/><br/>" +
                        "5. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "6. Pilih nomor rekening tujuan: BCA a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "7. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(1), arr);

                // ATM TRANSFER
                data = new JSONObject();
                data.put("title", "ATM TRANSFER");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Masukan kartu ATM dan PIN anda<br/><br/>" +
                        "2. Pilih Menu Transaksi Lainnya > Transfer > Ke rekening BCA<br/><br/>" +
                        "3. Masukan nomor BCA a.n. PT Rajapay Teknologi Indonesia di rekening penerima<br/><br/>" +
                        "4. Masukan jumlah nominal sesuai dengan yang diperintahkan<br/><br/>" +
                        "5. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(2), arr);

                break;
            case "BRI" :
                bank_name.setText("TRANSFER BRI");

                // BRI MOBILE
                data = new JSONObject();
                data.put("title", "BRI MOBILE");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Mobile Banking BRI<br/><br/>" +
                        "2. Pilih <b>Transfer</b><br/><br/>" +
                        "3. Pilih <b>Sesama BRI</b><br/><br/>" +
                        "4. Masukan nomor rekening berikut: BRI a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "5. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "6. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(0), arr);

                // INTERNET BANKING BRI
                data = new JSONObject();
                data.put("title", "INTERNET BANKING BRI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking<br/><br/>" +
                        "2. Pilih <b>Transfer Sesama & Antar Bisnis</b><br/><br/>" +
                        "3. Masukan nomor rekening berikut: BRI a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "4. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "5. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(1), arr);

                // ATM TRANSFER
                data = new JSONObject();
                data.put("title", "ATM TRANSFER");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Masukan kartu ATM dan PIN anda<br/><br/>" +
                        "2. Pilih Menu Transaksi Lainnya > Transfer > BRI<br/><br/>" +
                        "3. Masukan nomor BRI a.n. PT Rajapay Teknologi Indonesia di rekening penerima<br/><br/>" +
                        "4. Masukan jumlah nominal sesuai dengan yang diperintahkan<br/><br/>" +
                        "5. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(2), arr);

                break;
            case "BNI_GIRO" :
                bank_name.setText("TRANSFER BNI");

                // BNI MOBILE
                data = new JSONObject();
                data.put("title", "BNI MOBILE");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login BNI Mobile Banking<br/><br/>" +
                        "2. Pilih <b>Transfer</b><br/><br/>" +
                        "3. Pilih <b>Antar Rekening BNI</b><br/><br/>" +
                        "4. Masukan nomor rekening baru: BNI a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "5. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "6. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(0), arr);

                // INTERNET BANKING BNI
                data = new JSONObject();
                data.put("title", "INTERNET BANKING BNI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking<br/><br/>" +
                        "2. Pilih <b>Transaksi</b><br/><br/>" +
                        "3. Pilih <b>Info dan Administrasi</b><br/><br/>" +
                        "4. Masukan nomor rekening tujuan: BNI a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "5. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "6. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(1), arr);

                // SMS BANKING BNI
                data = new JSONObject();
                data.put("title", "SMS BANKING BNI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Aplikasi SMS Banking BNI<br/><br/>" +
                        "2. Pilih <b>Transfer</b><br/><br/>" +
                        "3. Pilih <b>Trf Rekening BNI</b><br/><br/>" +
                        "4. Masukan nomor rekening berikut: BNI a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "5. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "6. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(2), arr);

                // ATM TRANSFER
                data = new JSONObject();
                data.put("title", "ATM TRANSFER");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Masukan kartu ATM dan PIN anda<br/><br/>" +
                        "2. Pilih Menu Lainnya > Transfer > dari Rekening Tabungan > ke Rekening BNI<br/><br/>" +
                        "3. Masukan nomor BNI a.n. PT Rajapay Teknologi Indonesia di rekening penerima<br/><br/>" +
                        "4. Masukan jumlah nominal sesuai dengan yang diperintahkan<br/><br/>" +
                        "5. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(3), arr);

                break;
            case "MANDIRI_GIRO" :
                bank_name.setText("TRANSFER MANDIRI");

                // Mobile Online
                data = new JSONObject();
                data.put("title", "MOBILE ONLINE");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Mobile Online<br/><br/>" +
                        "2. Pilih <b>Transfer</b><br/><br/>" +
                        "3. Pilih <b>ke Rekening Mandiri</b><br/><br/>" +
                        "4. Masukan dan Tambahkan Rekening Tujuan: Mandiri a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "5. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "6. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(0), arr);

                // INTERNET BANKING MANDIRI
                data = new JSONObject();
                data.put("title", "INTERNET BANKING MANDIRI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking Mandiri<br/><br/>" +
                        "2. Pilih <b>Transfer</b><br/><br/>" +
                        "3. Pilih <b>Rekening Mandiri</b><br/><br/>" +
                        "3. Masukan rekening tujuan: Mandiri a.n. PT Rajapay Teknologi Indonesia<br/><br/>" +
                        "4. Masukan jumlah nominal yang sesuai dengan yang diperintahkan<br/><br/>" +
                        "5. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(1), arr);

                // ATM TRANSFER
                data = new JSONObject();
                data.put("title", "ATM TRANSFER");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Masukan kartu ATM dan PIN anda<br/><br/>" +
                        "2. Pilih Menu Lainnya > Transfer > ke Rekening Mandiri<br/><br/>" +
                        "3. Masukan nomor Mandiri a.n. PT Rajapay Teknologi Indonesia di rekening penerima<br/><br/>" +
                        "4. Masukan jumlah nominal sesuai dengan yang diperintahkan<br/><br/>" +
                        "5. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(2), arr);

                break;
        }
    }

    private void setupVa(JSONObject data, ArrayList<JSONObject> arr) throws JSONException{
        switch (bankCode.toUpperCase()){
            case "BCA" :
                bank_name.setText("VIRTUAL ACCOUNT BCA");

                // ATM BCA
                data = new JSONObject();
                data.put("title", "ATM BCA");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Pilih Menu <b>Transaksi Lainnya</b><br><br>" +
                        "2. Pilih <b>Transfer</b><br><br>" +
                        "3. Pilih <b>Ke rekening BCA Virtual Account</b><br><br>" +
                        "4. Input Nomor Virtual Account: " + bankAccountNo +" <br><br>" +
                        "5. Pilih <b>Benar</b><br><br>" +
                        "6. Pilih <b>Ya</b><br><br>" +
                        "7. Ambil bukti bayar Anda\",\"selected\":false}"));

                array_child.put(array_header.get(0), arr);

                // M-BCA
                data = new JSONObject();
                data.put("title", "M-BCA");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Mobile Banking<br><br>" +
                        "2. Pilih <b>m-Transfer</b><br><br>" +
                        "3. Pilih <b>BCA Virtual Account</b><br><br>" +
                        "4. Input Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "5. Klik <b>Send</b><br><br>" +
                        "6. Informasi VA akan ditampilkan<br><br>" +
                        "7. Klik <b>OK</b><br><br>" +
                        "8. Input PIN Mobile Banking<br><br>" +
                        "9. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(1), arr);

                // KLIKBCA
                data = new JSONObject();
                data.put("title", "KLIKBCA");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking<br><br>" +
                        "2. Pilih <b>Transaksi Dana</b><br><br>" +
                        "3. Pilih <b>Transfer Ke BCA Virtual Account</b><br><br>" +
                        "4. Input Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "5. Klik <b>Lanjutkan</b><br><br>" +
                        "6. Input Respon KeyBCA Appli 1<br><br>" +
                        "7. Klik <b>Kirim</b><br><br>" +
                        "8. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(2), arr);
                break;
            case "BNI" :
                bank_name.setText("VIRTUAL ACCOUNT BNI");

                // ATM BNI
                data = new JSONObject();
                data.put("title", "ATM BNI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Masukkan Kartu Anda<br><br>" +
                        "2. Pilih Bahasa<br><br>" +
                        "3. Masukkan PIN ATM Anda<br><br>" +
                        "4. Pilih <b>Menu Lainnya</b><br><br>" +
                        "5. Pilih <b>Transfer</b><br><br>" +
                        "6. Pilih jenis rekening yang akan Anda gunakan (Contoh: 'Dari Rekening Tabungan')<br><br>" +
                        "7. Pilih <b>Virtual Account Billing</b><br><br>" +
                        "8. Masukkan Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "9. Tagihan yang harus dibayarkan akan muncul pada layar konfirmasi<br><br>" +
                        "10. Konfirmasi, apabila telah sesuai, lanjutkan transaksi<br><br>" +
                        "11. Ambil bukti bayar Anda\",\"selected\":false}"));

                array_child.put(array_header.get(0), arr);

                // SMS BANKING BNI
                data = new JSONObject();
                data.put("title", "SMS BANKING BNI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Aplikasi SMS Banking BNI.<br><br>" +
                        "2. Pilih Menu <b>Transfer</b><br><br>" +
                        "3. Pilih <b>Trf Rekening BNI</b><br><br>" +
                        "4. Masukkan Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "5. Masukkan Jumlah Tagihan: 53300<br><br>" +
                        "6. Pilih <b>Proses</b><br><br>" +
                        "7. Pada Pop Up, Pilih <b>Setuju</b><br><br>" +
                        "8. Masukkan 2 Angka PIN dengan mengikuti petunjuk pada pesan konfirmasi<br><br>" +
                        "9. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(1), arr);

                // MOBILE BANKING BNI
                data = new JSONObject();
                data.put("title", "MOBILE BANKING BNI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Pilih <b>Transfer</b><br><br>" +
                        "2. Pilih menu <b>Virtual Account Billing</b> kemudian pilih rekening debet<br><br>" +
                        "3. Masukkan Nomor Virtual Account: " + bankAccountNo + " pada menu <b>input baru</b><br><br>" +
                        "4. Tagihan yang harus dibayarkan akan muncul pada layar konfirmasi<br><br>" +
                        "5. Konfirmasi transaksi dan masukkan Password Transaksi<br><br>" +
                        "6. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(2), arr);

                // INTERNET BANKING BNI
                data = new JSONObject();
                data.put("title", "INTERNET BANKING BNI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking.<br><br>" +
                        "2. Pilih <b>Transaksi</b><br><br>" +
                        "3. Pilih <b>Virtual Account Billing</b><br><br>" +
                        "4. Masukkan Nomor Virtual Account " + bankAccountNo + "<br><br>" +
                        "5. Pilih rekening debet yang akan digunakan<br><br>" +
                        "6. Klik <b>Lanjut</b><br><br>" +
                        "7. Masukkan Kode Otentikasi Token<br><br>" +
                        "8. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(3), arr);

                // ATM BERSAMA
                data = new JSONObject();
                data.put("title", "ATM BERSAMA");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Pilih <b>Transaksi Lainnya</b><br><br>" +
                        "2. Pilih menu <b>Transfer</b><br><br>" +
                        "3. Pilih <b>Transfer ke Bank Lain</b><br><br>" +
                        "4. Masukkan kode bank BNI (<b>009</b>), dan Nomor Virtual Account " + bankAccountNo + "<br><br>" +
                        "5. Masukkan nominal transfer sesuai tagihan<br><br>" +
                        "6. Konfirmasi rincian Anda akan tampil di layar<br><br>" +
                        "7. Klik <b>Ya</b> untuk melanjutkan<br><br>" +
                        "8. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(4), arr);
                break;

            case "BRI" :
                bank_name.setText("VIRTUAL ACCOUNT BRI");

                // ATM BRI
                data = new JSONObject();
                data.put("title", "ATM BRI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Pilih Menu <b>Transaksi Lain</b><br><br>" +
                        "2. Pilih <b>Pembayaran</b><br><br>" +
                        "3. Pilih <b>Lain-lain</b><br><br>" +
                        "4. Pilih <b>BRIVA</b><br><br>" +
                        "5. Input Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "6. Pilih <b>Ya</b><br><br>" +
                        "7. Ambil bukti bayar Anda\",\"selected\":false}"));

                array_child.put(array_header.get(0), arr);

                // BRI MOBILE
                data = new JSONObject();
                data.put("title", "BRI MOBILE");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login BRI Mobile<br><br>" +
                        "2. Pilih <b>Mobile Banking BRI</b><br><br>" +
                        "3. Pilih <b>Info</b><br><br>" +
                        "4. Pilih <b>BRIVA</b><br><br>" +
                        "5. Input Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "6. Input Jumlah Tagihan: " + total + "<br><br>" +
                        "7. Klik <b>Kirim</b><br><br>" +
                        "8. Input PIN Mobile<br><br>" +
                        "9. Klik <b>Kirim</b><br><br>" +
                        "10. Bukti bayar dikirimkan melalui SMS\",\"selected\":false}"));

                array_child.put(array_header.get(1), arr);

                // INTERNET BANKING BRI
                data = new JSONObject();
                data.put("title", "INTERNET BANKING BRI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking<br><br>" +
                        "2. Pilih <b>Pembayaran</b><br><br>" +
                        "3. Pilih <b>BRIVA</b><br><br>" +
                        "4. Input Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "5. Klik <b>Kirim</b><br><br>" +
                        "6. Input Password<br><br>" +
                        "7. Input mToken<br><br>" +
                        "8. Klik <b>Kirim</b><br><br>" +
                        "9. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(2), arr);
                break;

            case "MANDIRI" :
                bank_name.setText("VIRTUAL ACCOUNT MANDIRI");

                // ATM MANDIRI
                data = new JSONObject();
                data.put("title", "ATM MANDIRI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Pilih Menu <b>Bayar/Beli</b><br><br>" +
                        "2. Pilih <b>Lainnya</b><br><br>" +
                        "3. Pilih <b>Multi Payment</b><br><br>" +
                        "4. Input Kode Institusi: 88908 (Xendit 88908)<br><br>" +
                        "5. Input Virtual Account Number: " + bankAccountNo + "<br><br>" +
                        "6. Pilih <b>Benar</b><br><br>" +
                        "7. Pilih <b>Ya</b><br><br>" +
                        "8. Pilih <b>Ya</b><br><br>" +
                        "9. Ambil bukti bayar Anda\",\"selected\":false}"));

                array_child.put(array_header.get(0), arr);

                // MANDIRI MOBILE
                data = new JSONObject();
                data.put("title", "MANDIRI MOBILE");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Mobile Banking<br><br>" +
                        "2. Pilih <b>Bayar</b><br><br>" +
                        "3. Pilih <b>Lainnya</b><br><br>" +
                        "4. Input Penyedia Jasa: Xendit 88908<br><br>" +
                        "5. Input Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "6. Pilih <b>Lanjut</b><br><br>" +
                        "7. Input OTP dan PIN<br><br>" +
                        "8. Pilih <b>OK</b><br><br>" +
                        "9. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(1), arr);

                // INTERNET BANKING
                data = new JSONObject();
                data.put("title", "INTERNET BANKING MANDIRI");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Internet Banking<br><br>" +
                        "2. Pilih <b>Bayar</b><br><br>" +
                        "3. Pilih <b>Multi Payment</b><br><br>" +
                        "4. Input Penyedia Jasa: Xendit 88908<br><br>" +
                        "5. Input Kode Bayar: " + bankAccountNo + "<br><br>" +
                        "6. Centang IDR<br><br>" +
                        "7. Klik <b>Lanjutkan</b><br><br>" +
                        "8. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(2), arr);

                // MANDIRI ONLINE
                data = new JSONObject();
                data.put("title", "MANDIRI ONLINE");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Login Mandiri Online<br><br>" +
                        "2. Pilih <b>Bayar</b><br><br>" +
                        "3. Buat Pembayaran Baru<br><br>" +
                        "4. Pilih <b>Multipayment</b><br><br>" +
                        "5. Input Penyedia Jasa: Xendit 88908<br><br>" +
                        "6. Input Nomor Virtual Account: " + bankAccountNo + "<br><br>" +
                        "7. Tambah nomor baru<br><br>" +
                        "8. Pilih Konfirmasi<br><br>" +
                        "9. Masukkan nominal<br><br>" +
                        "10. Pilih <b>Lanjut</b><br><br>" +
                        "11. Centang tagihan yang akan dibayar<br><br>" +
                        "12. Pilih Lanjut<br><br>" +
                        "13. Pilih Konfirmasi<br><br>" +
                        "14. Input PIN<br><br>" +
                        "15. Pilih OK<br><br>" +
                        "16. Bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(3), arr);
                break;

            case "PERMATA" :
                bank_name.setText("VIRTUAL ACCOUNT PERMATA");

                // ATM PRIMA
                data = new JSONObject();
                data.put("title", "ATM PRIMA / ALTO");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Pada menu utama, pilih transaksi lain<br><br>" +
                        "2. Pilih Pembayaran Transfer<br><br>" +
                        "3. Pilih Pembayaran Lainnya<br><br>" +
                        "4. Pilih pembayaran Virtual Account<br><br>" +
                        "5. Masukan nomor Virtual Account " + bankAccountNo + "<br><br>" +
                        "6. Pada halaman konfirmasi, akan muncul nominal yang dibayarkan, nomor, dan nama merchant, lanjutkan jika sudah sesuai<br><br>" +
                        "7. Pilih sumber pembayaran anda dan lanjutkan<br><br>" +
                        "8. Transaksi anda selesai dan bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(0), arr);

                // PERMATA MOBILE X
                data = new JSONObject();
                data.put("title", "PERMATA MOBILE X");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Buka Permata Mobile X dan Login<br><br>" +
                        "2. Pilih Pay 'Pay Bills' / 'Pembayaran Tagihan'<br><br>" +
                        "3. Pilih 'Virtual Account'<br><br>" +
                        "4. Masukan nomor Virtual Account " + bankAccountNo + "<br><br>" +
                        "5. Detail pembayaran anda akan muncul di layar<br><br>" +
                        "6. Nominal yang ditagihkan akan muncul di layar. Pilih sumber pembayaran<br><br>" +
                        "7. Konfirmasi transaksi anda<br><br>" +
                        "8. Masukan kode response token anda<br><br>" +
                        "9. Transaksi anda selesai dan bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(1), arr);

                // PERMATA MOBILE
                data = new JSONObject();
                data.put("title", "PERMATA MOBILE");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Buka Permata Mobile dan Login<br><br>" +
                        "2. Pilih Pay 'Pay Bills' / 'Pembayaran Tagihan'<br><br>" +
                        "3. Pilih menu 'Transfer'<br><br>" +
                        "4. Pilih sumber pembayaran<br><br>" +
                        "5. Pilih 'Daftar Tagihan Baru'<br><br>" +
                        "6. Masukan nomor Virtual Account " + bankAccountNo + "<br><br>" +
                        "7. Periksa ulang mengenai transaksi yang anda ingin lakukan<br><br>" +
                        "8. Konfirmasi transaksi anda<br><br>" +
                        "9. Masukan SMS token respons<br><br>" +
                        "10. Transaksi anda selesai dan bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(2), arr);

                // INTERNET BANKING
                data = new JSONObject();
                data.put("title", "INTERNET BANKING");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Buka situs permatanet.com dan login<br><br>" +
                        "2. Pilih menu 'Pembayaran'<br><br>" +
                        "3. Pilih menu 'Pembayaran Tagihan'<br><br>" +
                        "4. Pilih 'Virtual Account'<br><br>" +
                        "5. Pilih sumber pembayaran<br><br>" +
                        "6. Pilih menu 'Masukan Daftar Tagihan Baru'<br><br>" +
                        "7. Masukan nomor Virtual Account " + bankAccountNo + "<br><br>" +
                        "8. Konfirmasi transaksi anda<br><br>" +
                        "9. Masukan SMS token respons<br><br>" +
                        "10. Transaksi anda selesai dan bukti bayar ditampilkan\",\"selected\":false}"));

                array_child.put(array_header.get(3), arr);
                break;
        }
    }

    private void setupQris(JSONObject data, ArrayList<JSONObject> arr) throws JSONException{
        switch (bankCode.toUpperCase()) {
            case "QRIS":
                bank_name.setText("SCAN QR");

                // Link Aja / OVO / Dana / GoPay
                data = new JSONObject();
                data.put("title", "Link Aja / OVO / Dana / GoPay");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Buka aplikasi Link Aja / OVO / Dana / GoPay di ponsel anda<br><br>" +
                        "2. Pilih Menu Scan / Pay > Kemudian arahkan kamera ke gambar QR Code <br><br>" +
                        "3. Pastikan nama merchant sudah sesuai<br><br>" +
                        "4. Masukan jumlah nominal sesuai dengan yang diperintahkan<br><br>" +
                        "5. Ikuti instruksi untuk menyelesaikan transaksi\",\"selected\":false}"));
                array_child.put(array_header.get(0), arr);

                break;
        }
    }

    private void setupRetail(JSONObject data, ArrayList<JSONObject> arr) throws JSONException{
        switch (bankCode.toUpperCase()){
            case "ALFAMART" :
                bank_name.setText("GERAI RETAIL");

                // ALFAMART
                data = new JSONObject();
                data.put("title", "Alfamart");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Silahkan pergi ke gerai Alfamart terdekat<br/><br/>" +
                        "2. Infokan kepada kasir dengan menyebutkan <b>Pembayaran melalui Rajapay</b><br/><br/>" +
                        "3. Sebutkan kode pembayaran yang Anda dapatkan saat pemensanan di aplikasi Rajapay <b>" + bankAccountNo + "</b> kepada kasir<br/><br/>" +
                        "4. Lakukan pembayaran sesuai dengan yang jumlah yang dibacakan oleh kasir<br/><br/>" +
                        "5. Minta bukti pembayaran Anda<br/><br/>\",\"selected\":false}"));
                array_child.put(array_header.get(0), arr);

                // Alfamidi
                data = new JSONObject();
                data.put("title", "Alfamidi");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Silahkan pergi ke gerai Alfamidi terdekat<br/><br/>" +
                        "2. Infokan kepada kasir dengan menyebutkan <b>Pembayaran melalui Rajapay</b><br/><br/>" +
                        "3. Sebutkan kode pembayaran yang Anda dapatkan saat pemensanan di aplikasi Rajapay <b>" + bankAccountNo + "</b> kepada kasir<br/><br/>" +
                        "4. Lakukan pembayaran sesuai dengan yang jumlah yang dibacakan oleh kasir<br/><br/>" +
                        "5. Minta bukti pembayaran Anda<br/><br/>\",\"selected\":false}"));
                array_child.put(array_header.get(1), arr);

                // Lawson
                data = new JSONObject();
                data.put("title", "Lawson");
                data.put("selected", false);
                array_header.add(data);

                arr = new ArrayList<>();
                arr.add(new JSONObject("{\"title\":\"1. Silahkan pergi ke gerai Lawson terdekat<br/><br/>" +
                        "2. Infokan kepada kasir dengan menyebutkan <b>Pembayaran melalui Rajapay</b><br/><br/>" +
                        "3. Sebutkan kode pembayaran yang Anda dapatkan saat pemensanan di aplikasi Rajapay <b>" + bankAccountNo + "</b>  kepada kasir<br/><br/>" +
                        "4. Lakukan pembayaran sesuai dengan yang jumlah yang dibacakan oleh kasir<br/><br/>" +
                        "5. Minta bukti pembayaran Anda<br/><br/>\",\"selected\":false}"));
                array_child.put(array_header.get(2), arr);

                break;
        }
    }

    private void prepare_data() throws JSONException {
        /**
         * @author Jesslyn
         * @note prepare data based on Payment Method
         */
        // <code>
        JSONObject data = new JSONObject();
        ArrayList<JSONObject> arr = new ArrayList<>();

        switch (paymentMethod.toUpperCase()){
            case "BANK TRANSFER" :
                setupBankTransfer(data, arr);
                break;

            case "VIRTUAL ACCOUNT" :
                setupVa(data, arr);
                break;

            case "QRIS" :
                setupQris(data, arr);
                break;

            case "RETAIL" :
                setupRetail(data, arr);
                break;
        }

        // </code>

//        arr = new ArrayList<>();
//        arr.add(new JSONObject("{\"title\":\"1. Masukkan kartu ATM dan PIN BCA anda\",\"selected\":false}"));
//        array_child.put(array_header.get(1), arr);
    }
}