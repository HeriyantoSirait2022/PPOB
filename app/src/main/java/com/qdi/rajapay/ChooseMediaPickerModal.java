package com.qdi.rajapay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ChooseMediaPickerModal extends DialogFragment {
    TextView camera, gallery;
    String mFileName;

    BaseActivity parent;

    public ChooseMediaPickerModal(String pFileName){
        this.mFileName = pFileName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_media_picker_modal,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = new File(Environment.getExternalStorageDirectory(),  mFileName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                parent.camera_uri = Uri.fromFile(photo);
                parent.execute_media_intent(intent,"camera");
                dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.setType("image/*");
                i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                parent.execute_media_intent(i,"gallery");
                dismiss();
            }
        });
    }

    private void init(View view){
        camera = view.findViewById(R.id.camera);
        gallery = view.findViewById(R.id.gallery);

        parent = (BaseActivity) getActivity();


    }
}

