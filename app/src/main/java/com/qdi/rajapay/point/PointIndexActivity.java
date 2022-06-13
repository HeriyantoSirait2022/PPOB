package com.qdi.rajapay.point;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.api.APICallback;
import com.qdi.rajapay.api.PointAPI;
import com.qdi.rajapay.model.RewardData;
import com.qdi.rajapay.model.RewardHistoryData;

import org.json.JSONException;

import java.text.MessageFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class PointIndexActivity extends BaseActivity
implements APICallback.ItemMultipleListCallback<RewardData, RewardHistoryData>,
APICallback.ItemCallback<RewardData>{

    TextView point;
    ArrayList<RewardData> arrReward = new ArrayList<>();
    RewardData selectedData;

    ArrayList<RewardHistoryData> arrHistory = new ArrayList<>();
    PointAPI api;

    ViewPager view_pager;
    TabLayout tabbar;

    ViewPagerAdapter viewPagerAdapter;

    Fragment selected_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_index);

        init_toolbar(getResources().getString(R.string.point_label));

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selected_fragment = viewPagerAdapter.getItem(position);

                if(selected_fragment instanceof PointRewardFragment) {
                    ((PointRewardFragment) selected_fragment).show_data();
                }
                else if(selected_fragment instanceof RequestRewardHistoryFragment) {
                    ((RequestRewardHistoryFragment) selected_fragment).show_data();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void init() throws JSONException {
        point = findViewById(R.id.point);

        point.setText(MessageFormat.format("+ {0}", formatter.format(user_SP.getInt("balance_point", 0))));

        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new PointRewardFragment(),getResources().getString(R.string.reward_list));
        viewPagerAdapter.addFragment(new RequestRewardHistoryFragment(),getResources().getString(R.string.reward_history));

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        selected_fragment = new PointRewardFragment();

        api = new PointAPI(this, this.user_SP);
        api.getList(this);
    }

    public void redeemConfirmed(){
        if(selectedData!=null)
            api.requestReward(selectedData, this);
    }

    @Override
    public void onMulitpleListResponseSuccess(List<RewardData> rewardList, List<RewardHistoryData> historyList,
                                      String baseUrl) {
        arrReward.clear();
        for(RewardData item: rewardList){
            RewardData obj = new RewardData();
            obj.id = item.id;
            obj.name =  item.name;
            obj.point = item.point;
            obj.description = item.description;
            obj.image = baseUrl + "/" + item.image;
            obj.is_pending = item.is_pending;
            arrReward.add(obj);
        }

        arrHistory.clear();
        for(RewardHistoryData hist: historyList){
            RewardHistoryData obj = new RewardHistoryData();
            obj.id = hist.id;
            obj.reward_name = hist.reward_name;
            obj.reward_point = hist.reward_point;
            obj.reward_description = hist.reward_description;
            obj.image = baseUrl + "/" + hist.image;
            obj.status = hist.status;
            obj.status_name = hist.status_name;
            obj.review_note = hist.review_note == null ? "" : hist.review_note;
            obj.review_date = hist.review_date;
            arrHistory.add(obj);
        }

        if(selected_fragment instanceof PointRewardFragment) {
            ((PointRewardFragment) selected_fragment).show_data();
        }
        else if(selected_fragment instanceof RequestRewardHistoryFragment) {
            ((RequestRewardHistoryFragment) selected_fragment).show_data();
        }
    }

    @Override
    public void onItemResponseSuccess(RewardData item, String message) {
        user_edit_SP.putInt("balance_point",  item.point);
        point.setText(MessageFormat.format("+ {0}", formatter.format(item.point)));

        api.getList(this);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clear_fragment(){
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}