package com.qdi.rajapay.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.qdi.rajapay.R;
import com.qdi.rajapay.inbox.InboxListActivity;
import com.qdi.rajapay.order.DepositMutationFragment;
import com.qdi.rajapay.order.TransactionFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List; 

/**
 * @module 5.1 Transaksi
 * @screen 5.1.1
 */
public class OrderFragment extends Fragment {
    Button contact_us;
    ViewPager view_pager;
    TabLayout tabbar;
    EditText search;
    ImageView imgReset;
    ViewPagerAdapter viewPagerAdapter;

    MainActivity parent;
    TransactionFragment transactionFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_order_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(parent, InboxListActivity.class));
            }
        });

        imgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                transactionFragment.setQuery(search.getText().toString().trim());
            }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    transactionFragment.setQuery(search.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

//        search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged (CharSequence s,int start, int count,
//                                           int after){
//            }
//            @Override
//            public void onTextChanged ( final CharSequence s, int start, int before,
//                                        int count){
//                //You need to remove this to run only once
//                handler.removeCallbacks(input_finish_checker);
//
//            }
//            @Override
//            public void afterTextChanged ( final Editable s){
//                //avoid triggering event when text is empty
//                last_text_edit = System.currentTimeMillis();
//                handler.postDelayed(input_finish_checker, delay);
//            }
//        });
    }

//    long delay = 1000; // 1 seconds after user stops typing
//    long last_text_edit = 0;
//    Handler handler = new Handler();
//
//    private Runnable input_finish_checker = new Runnable() {
//        public void run() {
//            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
//                // TODO: do what you need here
//                // ............
//                // ............
//                transactionFragment.setQuery(search.getText().toString().trim());
//            }
//        }
//    };

    private void init(View view){
        view_pager = view.findViewById(R.id.view_pager);
        tabbar = view.findViewById(R.id.tabbar);
        contact_us = view.findViewById(R.id.contact_us);
        search = view.findViewById(R.id.search);
        imgReset = view.findViewById(R.id.img_reset);

        transactionFragment = new TransactionFragment();

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(transactionFragment,getResources().getString(R.string.transaction_label));
        viewPagerAdapter.addFragment(new DepositMutationFragment(),getResources().getString(R.string.deposit_mutation_label));

        view_pager.setAdapter(viewPagerAdapter);
        tabbar.setupWithViewPager(view_pager);

        parent = (MainActivity) getActivity();
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
