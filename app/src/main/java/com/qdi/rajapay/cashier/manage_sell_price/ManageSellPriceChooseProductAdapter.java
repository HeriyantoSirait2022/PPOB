package com.qdi.rajapay.cashier.manage_sell_price;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.PriceData;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ManageSellPriceChooseProductAdapter extends BaseExpandableListAdapter {
    Context context;
    List<PriceData> list;
    ClickListener clickListener;

    public interface ClickListener{
        void onClick(int group_position,int child_position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener= clickListener;
    }

    public ManageSellPriceChooseProductAdapter(Context context, List<PriceData> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<PriceData> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        PriceData data = list.get(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.manage_sell_price_choose_product_group, null);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView description = convertView.findViewById(R.id.description);
        ImageView image = convertView.findViewById(R.id.image);

        if(BaseActivity.isNullOrEmpty(data.getName())){
            title.setText("N/A");
        }else{
            title.setText(data.getName());
        }

        if(BaseActivity.isNullOrEmpty(data.getDetail())){
            description.setText("N/A");
        }else{
            description.setText(data.getDetail());
        }

        if(BaseActivity.isNullOrEmpty(data.getImage())){
            image.setImageDrawable(context.getDrawable(R.drawable.ic_default_4));
        }else{
            Picasso.get().load(data.getImage()).into(image);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        PriceData data = list.get(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.manage_sell_price_choose_product_child, null);
        }
        TextView txtAppPrice = convertView.findViewById(R.id.app_price);
        TextView txtPrice = convertView.findViewById(R.id.price);
        Button action = convertView.findViewById(R.id.action);


        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator(',');
        formatSymbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###,###",formatSymbols);

        double agenPrice = data.getAgenPrice()==null?0:data.getAgenPrice();
        double price = data.getPrice()==null?0:data.getPrice();
        txtAppPrice.setText("Rp. "+formatter.format(price));
        txtPrice.setText("Rp. "+formatter.format(agenPrice));
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(groupPosition,childPosition);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
