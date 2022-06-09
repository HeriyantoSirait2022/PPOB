package com.qdi.rajapay.agency.upgrade_premium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qdi.rajapay.R;

public class UpgradePremiumPremiumFragment extends Fragment {
    WebView webview_content;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.agency_upgrade_premium_premium_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * @author Dinda
         * @note Add webview to display premium link
         */
        // <code>
        webview_content = view.findViewById(R.id.webview_content);
        webview_content.loadUrl(getContext().getResources().getString(R.string.main_menu_premium_link));
        // </code>
    }
}
