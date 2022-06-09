package com.qdi.rajapay.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.dashboard_super.commition.ContactCSModal;

/***
 * @module 4.0 Beranda 
 */
public class MainActivity extends BaseActivity {
    LinearLayout home, order, agency, account, top_up;
    View home_selected, order_selected, agency_selected, account_selected;
    LinearLayout cashier;
    FloatingActionButton contactCs;

    Fragment selected_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_edit_SP.putString("bottom_main_menu", "home");
                user_edit_SP.commit();
                manage_bottom_menu_selected();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_edit_SP.putString("bottom_main_menu", "order");
                user_edit_SP.commit();
                manage_bottom_menu_selected();
            }
        });

        agency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_edit_SP.putString("bottom_main_menu", "agency");
                user_edit_SP.commit();
                manage_bottom_menu_selected();
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_edit_SP.putString("bottom_main_menu", "account");
                user_edit_SP.commit();
                manage_bottom_menu_selected();
            }
        });

        cashier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_edit_SP.putString("bottom_main_menu", "cashier");
                user_edit_SP.commit();
                manage_bottom_menu_selected();
//                startActivity(new Intent(MainActivity.this, CashierIndexActivity.class));
            }
        });

        contactCs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ContactCSModal modal = new ContactCSModal();
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        /**
         * @author Dinda
         * @note Handling has screen request (if exist)
         */
        // <code>
        Intent intent = getIntent();
        Boolean extras = (Boolean) isExtras(intent, "has_screen_request");
        if(extras == null){
            user_edit_SP.putString("bottom_main_menu", "home");
            user_edit_SP.commit();
        }

        manage_bottom_menu_selected();
        showMessage(intent);
        // </code>
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        manage_bottom_menu_selected();
        showMessage(intent);
    }

    private void init() {
        home = findViewById(R.id.home);
        order = findViewById(R.id.order);
        agency = findViewById(R.id.agency);
        account = findViewById(R.id.account);
        home_selected = findViewById(R.id.home_selected);
        order_selected = findViewById(R.id.order_selected);
        agency_selected = findViewById(R.id.agency_selected);
        account_selected = findViewById(R.id.account_selected);
        cashier = findViewById(R.id.cashier);
        layout = findViewById(R.id.layout);

        contactCs = findViewById(R.id.contact_cs);
    }

    private void manage_bottom_menu_selected() {
        if (user_SP.getString("bottom_main_menu", "home").equals("home")) {
            home_selected.setVisibility(View.VISIBLE);
            order_selected.setVisibility(View.GONE);
            agency_selected.setVisibility(View.GONE);
            account_selected.setVisibility(View.GONE);
        } else if (user_SP.getString("bottom_main_menu", "home").equals("order")) {
            home_selected.setVisibility(View.GONE);
            order_selected.setVisibility(View.VISIBLE);
            agency_selected.setVisibility(View.GONE);
            account_selected.setVisibility(View.GONE);
        } else if (user_SP.getString("bottom_main_menu", "home").equals("agency")) {
            home_selected.setVisibility(View.GONE);
            order_selected.setVisibility(View.GONE);
            agency_selected.setVisibility(View.VISIBLE);
            account_selected.setVisibility(View.GONE);
        } else if (user_SP.getString("bottom_main_menu", "home").equals("account")) {
            home_selected.setVisibility(View.GONE);
            order_selected.setVisibility(View.GONE);
            agency_selected.setVisibility(View.GONE);
            account_selected.setVisibility(View.VISIBLE);
        } else {
            home_selected.setVisibility(View.GONE);
            order_selected.setVisibility(View.GONE);
            agency_selected.setVisibility(View.GONE);
            account_selected.setVisibility(View.INVISIBLE);
        }
        manage_fragment();
    }

    private void manage_fragment() {
        if (user_SP.getString("bottom_main_menu", "home").equals("home"))
            selected_fragment = new HomeFragment();
        else if (user_SP.getString("bottom_main_menu", "home").equals("order"))
            selected_fragment = new OrderFragment();
        else if (user_SP.getString("bottom_main_menu", "home").equals("agency"))
            selected_fragment = new AgencyFragment();
        else if (user_SP.getString("bottom_main_menu", "home").equals("account"))
            selected_fragment = new AccountFragment();
        else if (user_SP.getString("bottom_main_menu", "home").equals("cashier"))
            selected_fragment = new CashierFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, selected_fragment).commit();
    }
}
