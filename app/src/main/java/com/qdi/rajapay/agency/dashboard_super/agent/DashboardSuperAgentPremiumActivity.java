package com.qdi.rajapay.agency.dashboard_super.agent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DashboardSuperAgentPremiumActivity extends BaseActivity {
    ViewPager view_pager;
    TabLayout tabbar;

    JSONObject data = new JSONObject();

    ViewPagerAdapter viewPagerAdapter;
    Fragment selected_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_super_bonus_premium);

        init_toolbar(getResources().getString(R.string.agency_dashboard_agent_transaction));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException, ParseException {
        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);

        data = new JSONObject(getIntent().getStringExtra("data"));

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DashboardSuperAgentPremiumFragment("S"),getResources().getString(R.string.agency_dashboard_agent_detail_valid));
        viewPagerAdapter.addFragment(new DashboardSuperAgentPremiumFragment("not S"),getResources().getString(R.string.agency_dashboard_agent_detail_invalid));

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        selected_fragment = new DashboardSuperAgentPremiumFragment("S");
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