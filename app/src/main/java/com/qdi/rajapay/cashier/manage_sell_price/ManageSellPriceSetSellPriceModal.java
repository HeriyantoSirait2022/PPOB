package com.qdi.rajapay.cashier.manage_sell_price;

import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.model.PriceData;
import com.qdi.rajapay.utils.PriceKeyboard;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;

/**
 * @module 6.0 Kasir
 * @screen 6.7.3
 */
public class ManageSellPriceSetSellPriceModal extends BottomSheetDialogFragment {

    public interface Callback {
        void onUpdatePriceSubmitted(double price);
    }

    EditText price;
    LinearLayout layout;
    ImageView cancel;
    TextView title;
    PriceKeyboard pk;

    BaseActivity parent;
    PriceData selected;
    double basePrice;
    Callback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manage_sell_price_set_sell_price_modal,container,false);
    }
    private void submit(){
        if(price.getText().toString().equals("")) {
            parent.displayToast(parent, getResources().getString(R.string.print_confirmation_manage_sell_price_empty_sell_price));
        }else{
            double currPrice = Double.valueOf(price.getText().toString().replace(".",""));

            if(currPrice >= basePrice || currPrice == 0){
                dismiss();
                callback.onUpdatePriceSubmitted(currPrice);
            }else{
                parent.displayToast(parent, getString(R.string.print_confirmation_manage_sell_price_lower_sell_price));
            }
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        parent.edittext_currency(price);
    }

    private void init(View view) throws JSONException {
        price = view.findViewById(R.id.price);
        layout = view.findViewById(R.id.layoutDialog);
        cancel = view.findViewById(R.id.cancel);
        title = view.findViewById(R.id.title);
        pk = view.findViewById(R.id.price_keyboard);

        if (Build.VERSION.SDK_INT >= 11) {
            price.setRawInputType(InputType.TYPE_CLASS_TEXT);
            price.setTextIsSelectable(true);
        } else {
            price.setRawInputType(InputType.TYPE_NULL);
            price.setFocusable(true);
        }

        price.setShowSoftInputOnFocus(false);
        price.setMaxLines(1);
        price.setImeOptions(EditorInfo.IME_ACTION_DONE);
        price.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    submit();
                }
                return handled;
            }
        });
        InputConnection ic = price.onCreateInputConnection(new EditorInfo());
        pk.setInputConnection(ic);

        price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    parent.hideKeyboard(v);
                }
            }
        });

        parent = (BaseActivity) getActivity();

        if(selected != null) {
            basePrice = selected.getPrice()==null?0:selected.getPrice();

            title.setText(selected.getName());
            price.setHint("Rp. " + parent.formatter.format(basePrice));
        }
    }

    public void setSelected(PriceData selected) {
        this.selected = selected;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
