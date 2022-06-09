package com.qdi.rajapay.cashier.monthly_report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qdi.rajapay.R;
import com.qdi.rajapay.model.ReportData;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CashierMonthlyReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Map<String, List<ReportData>> mapReport;
    ClickListener clickListener;
    Context context;

    private static int TYPE_SECTION = 1;
    private static int TYPE_ITEM = 2;

    public interface ClickListener{
        void onClick(int position);
        void onDelete(int position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public SectionViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,description,price,revised_price;
        public ImageView delete;
        public CardView card;

        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            revised_price = view.findViewById(R.id.revised_price);
            delete = view.findViewById(R.id.delete);
            card = view.findViewById(R.id.card);

            if(delete != null) {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onDelete(arrayIndex(getAdapterPosition()));
                    }
                });
            }

            if(card != null) {
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClick(arrayIndex(getAdapterPosition()));
                    }
                });
            }
        }
    }

    public CashierMonthlyReportAdapter(List<ReportData> arr, Context context) {
        this.mapReport = mapReportData(arr);
        this.context = context;
    }

    public void setList(List<ReportData> list) {
        this.mapReport = mapReportData(list);
        this.notifyDataSetChanged();
    }

    private Map<String, List<ReportData>> mapReportData(List<ReportData> list) {

        Map<String, List<ReportData>> mapReport = new LinkedHashMap<>();
        for(ReportData data: list) {
            String dateStr = data.getFormattedDate();
            if(!mapReport.containsKey(dateStr)){
                mapReport.put(dateStr, new ArrayList<ReportData>());
            }
            mapReport.get(dateStr).add(data);
        }
        return mapReport;
    }

    @Override
    public int getItemViewType(int position) {
        return isSection(position) ? TYPE_SECTION : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == TYPE_SECTION) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cashier_monthly_report_date_item, viewGroup, false);
            return new SectionViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cashier_monthly_report_item, viewGroup, false);
            return new MyViewHolder(view);
        }
    }

    private boolean isSection(int position) {
        if(position == 0) return true;

        int temp = position;
        for(String key: mapReport.keySet()) {
            temp -= mapReport.get(key).size() + 1;
            if(temp==0) return true;
        }
        return false;
    }

    private int sectionIndex(int position) {
        if(position == 0) return 0;

        int index = 0;
        int temp = position;
        for(String key: mapReport.keySet()) {
            index++;
            temp -= mapReport.get(key).size() + 1;
            if(temp==0) return index;
        }
        return 0;
    }

    private int arrayIndex(int position) {
        int arrayIndex = 0;
        int temp = position-1;
        for(String key: mapReport.keySet()) {
            if(temp>mapReport.get(key).size()) {
                temp -= mapReport.get(key).size() + 1;
                arrayIndex += mapReport.get(key).size();
            } else {
                arrayIndex += temp;
                break;
            }
        }
        return arrayIndex;
    }

    private int itemIndex(int position) {
        int temp = position;
        for(String key: mapReport.keySet()) {
            if(temp>mapReport.get(key).size())
                temp -= mapReport.get(key).size();
            else return temp;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder myViewHolder, int i) {
        if(isSection(i)) {

            List<String> keys = new ArrayList<>(mapReport.keySet());
            int sectionIndex = sectionIndex(i);
            String title = keys.get(sectionIndex);
            ((SectionViewHolder)myViewHolder).title.setText(title);
            return;
        }

        ReportData data = null;
        int temp = i-1;
        for(String key: mapReport.keySet()) {
            if(temp>mapReport.get(key).size())
                temp -= mapReport.get(key).size() + 1;
            else {
                data = mapReport.get(key).get(temp);
                break;
            }
        }

        if(data == null) return;

        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator(',');
        formatSymbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###,###",formatSymbols);

        ((MyViewHolder)myViewHolder).title.setText("Tagihan " + data.getId());
        ((MyViewHolder)myViewHolder).description.setText(data.getDetail());
        ((MyViewHolder)myViewHolder).price.setText("Rp. " +formatter.format(data.getAgenPrice()));
        ((MyViewHolder)myViewHolder).revised_price.setText("Rp. " +formatter.format(data.getProfit()));
    }

    @Override
    public int getItemCount() {
        int size = mapReport.size();
        for(String key: mapReport.keySet()) {
            size += mapReport.get(key).size();
        }
        return size;
    }
}