package com.qdi.rajapay.inbox;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.contact_us.ContactUsConfirmationActivity;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.inbox.news.InboxNewsFragment;
import com.qdi.rajapay.inbox.ticket.InboxTicketFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @module 4.2 Kotak Masuk
 * @screen 4.2.1
 */
public class InboxListActivity extends BaseActivity {
    ViewPager view_pager;
    TabLayout tabbar;

    ViewPagerAdapter viewPagerAdapter;
    int selected_page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_list);

        init_toolbar(getResources().getString(R.string.activity_title_inbox));
        init();

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selected_page = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        if(viewPagerAdapter.getItem(selected_page) instanceof InboxTicketFragment){
            InboxTicketFragment fragment = (InboxTicketFragment) viewPagerAdapter.getItem(selected_page);
            fragment.get_data();
        }
        super.onResume();
    }

    private void init(){
        view_pager = findViewById(R.id.view_pager);
        tabbar = findViewById(R.id.tabbar);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new InboxNewsFragment(),getResources().getString(R.string.news_label));
        viewPagerAdapter.addFragment(new InboxTicketFragment(),getResources().getString(R.string.ticket_label));

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        if(getIntent().hasExtra("selected") && getIntent().getStringExtra("selected").equals("ticket"))
            view_pager.setCurrentItem(1,true);
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

    /**
     * @author Dinda
     * @note BDD 5644 - remove onBackPressed handling
     *
     * @author Eliza Sutantya
     * @patch FR19022
     * @notes this onBackPressed usef for contactUsConfirmationActivity, do not remove this function
     */
    // <code>
    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra("caller") && getIntent().getStringExtra("caller").equals(ContactUsConfirmationActivity.class.getCanonicalName())){
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }else{
            super.onBackPressed();
        }
    }
    // </code>
}