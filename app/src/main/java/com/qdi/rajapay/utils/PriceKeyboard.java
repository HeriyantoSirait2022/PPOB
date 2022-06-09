package com.qdi.rajapay.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qdi.rajapay.R;
import com.google.android.material.button.MaterialButton;

public class PriceKeyboard extends LinearLayout implements View.OnClickListener {
    private Button button1, button2, button3, button4,
            button5, button6, button7, button8,
            button9, button0, buttonEnter, buttonC, button000;

    private MaterialButton buttonDelete;

    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    public PriceKeyboard(Context context) {
        this(context, null, 0);
    }

    public PriceKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.price_keyboard, this, true);
        button1 = (Button) findViewById(R.id.button_1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(this);
        button3 = (Button) findViewById(R.id.button_3);
        button3.setOnClickListener(this);
        button4 = (Button) findViewById(R.id.button_4);
        button4.setOnClickListener(this);
        button5 = (Button) findViewById(R.id.button_5);
        button5.setOnClickListener(this);
        button6 = (Button) findViewById(R.id.button_6);
        button6.setOnClickListener(this);
        button7 = (Button) findViewById(R.id.button_7);
        button7.setOnClickListener(this);
        button8 = (Button) findViewById(R.id.button_8);
        button8.setOnClickListener(this);
        button9 = (Button) findViewById(R.id.button_9);
        button9.setOnClickListener(this);
        button0 = (Button) findViewById(R.id.button_0);
        button0.setOnClickListener(this);
        button000 = (Button) findViewById(R.id.button_000);
        button000.setOnClickListener(this);
        buttonC = (Button) findViewById(R.id.button_c);
        buttonC.setOnClickListener(this);
        buttonDelete = (MaterialButton) findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(this);
        buttonEnter = (Button) findViewById(R.id.button_enter);
        buttonEnter.setOnClickListener(this);

        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
        keyValues.put(R.id.button_000, "000");
        keyValues.put(R.id.button_enter, "\n");
    }

    @Override
    public void onClick(View view) {

        if (inputConnection == null)
            return;

        if (view.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);

            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else if (view.getId() == R.id.button_c) {
            CharSequence currentText = inputConnection.getExtractedText(new ExtractedTextRequest(), 0).text;
            CharSequence beforCursorText = inputConnection.getTextBeforeCursor(currentText.length(), 0);
            CharSequence afterCursorText = inputConnection.getTextAfterCursor(currentText.length(), 0);

            inputConnection.deleteSurroundingText(beforCursorText.length(), afterCursorText.length());
        }else if(view.getId() == R.id.button_enter){
            inputConnection.performEditorAction(EditorInfo.IME_ACTION_DONE);
        }else {
            String value = keyValues.get(view.getId());
            inputConnection.commitText(value, 1);
        }
    }

    public void setInputConnection(InputConnection ic) {
        inputConnection = ic;
    }
}
