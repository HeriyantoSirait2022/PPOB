package com.qdi.rajapay.account.verification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qdi.rajapay.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class VerificationUploadSignatureModal extends BottomSheetDialogFragment {
    ImageView image_right,image_wrong;
    TextView title,subtitle;
    Button understood;

    VerificationUploadSignatureActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_verification_example_modal,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        understood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void init(View view){
        image_right = view.findViewById(R.id.image_right);
        image_wrong = view.findViewById(R.id.image_wrong);
        title = view.findViewById(R.id.title);
        subtitle = view.findViewById(R.id.subtitle);
        understood = view.findViewById(R.id.understood);

        parent = (VerificationUploadSignatureActivity) getActivity();
        title.setText(getResources().getString(R.string.account_verification_upload_signature_example_modal_title));
        subtitle.setText(getResources().getString(R.string.account_verification_upload_signature_example_modal_subtitle));
        image_right.setImageDrawable(getResources().getDrawable(R.drawable.icon_signature_right));
        image_wrong.setImageDrawable(getResources().getDrawable(R.drawable.icon_signature_wrong));
    }
}
