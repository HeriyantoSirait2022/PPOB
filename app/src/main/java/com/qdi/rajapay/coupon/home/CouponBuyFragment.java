package com.qdi.rajapay.coupon.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qdi.rajapay.R;
import com.qdi.rajapay.coupon.transaction.CouponListAdapter;
import com.qdi.rajapay.interfaces.OnRefreshData;
import com.qdi.rajapay.payment.ChoosePaymentActivity;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @module 4.21 Kupon
 * @screen 4.21.1.1
 */
public class CouponBuyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ConstraintLayout coupon_list_no_data_layout;
    RecyclerView list;
    CardView bannerLayout;
    boolean showBanner = true;
    boolean useOptions = false;
    Double clean_total = 0d;

    CouponListAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;
    OnRefreshData mCallback;

    public CouponBuyFragment(){}

    public CouponBuyFragment(boolean flagBanner, boolean flagUse){
        this.showBanner = flagBanner;
        this.useOptions = flagUse;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.coupon_buy_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        adapter.setOnItemClickListener(new CouponListAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                JSONObject data = array.get(position);

                Intent intent = new Intent(getActivity(), CouponDetailActivity.class);
                intent.putExtra("data", data.toString());
                if(useOptions) {
                    intent.putExtra("use_options", true);
                    intent.putExtra("total", clean_total);
                    getActivity().startActivityForResult(intent, ChoosePaymentActivity.REQUEST_COUPON);
                }else{
                    intent.putExtra("buy_options", true);
                    startActivity(intent);
                }
            }

        });
    }

    private void init(View view) {
        list = view.findViewById(R.id.list);
        coupon_list_no_data_layout = view.findViewById(R.id.coupon_list_no_data_layout);
        bannerLayout = view.findViewById(R.id.banner_layout);

        adapter = new CouponListAdapter(array, getActivity());
        layout_manager = new LinearLayoutManager(getActivity());
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        refreshUI();

        if(showBanner)
            bannerLayout.setVisibility(View.VISIBLE);
        else
            bannerLayout.setVisibility(View.GONE);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_coupon);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    public void hideLoading(){
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setData(ArrayList<JSONObject> pArray){
        array.clear();
        array.addAll(pArray);
        adapter.notifyDataSetChanged();
        refreshUI();
    }

    public void setClean_total(Double clean_total) {
        this.clean_total = clean_total;
    }

    private void refreshUI(){
        if(array.size() > 0) {
            list.setVisibility(View.VISIBLE);
            coupon_list_no_data_layout.setVisibility(View.GONE);
        }
        else {
            list.setVisibility(View.GONE);
            coupon_list_no_data_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnRefreshData) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement onRefreshData");
        }
    }

    @Override
    public void onRefresh() {
        mCallback.refreshData();
    }
}
