package com.example.towncenterstore.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.towncenterstore.R;
import com.example.towncenterstore.general.Constants;
import com.example.towncenterstore.databinding.ActivityContactUsBinding;
import com.example.towncenterstore.listeners.OnClick;

public class ContactUsActivity extends AppCompatActivity {

    ActivityContactUsBinding binding;
    OnClick onClick;
    public static Activity contactUsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();


    }

    private void init() {
        contactUsActivity = this;
        onClickListeners();

    }

    private void onClickListeners() {
        onClickBtnBack();
        onClickFacebookIcon();
        onClickPhoneIcon();
        onClickWhatsAppIcon();
        onClickEmailIcon();
    }

    private void onClickBtnBack() {
        binding.imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                finish();
            }
        });
    }

    private void onClickFacebookIcon(){
        binding.textInputLayoutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(newFacebookIntent(getPackageManager(),"https://www.facebook.com/TOWNCENTERDERGROUP"));
            }
        });
    }

    private void onClickEmailIcon(){
        binding.textInputLayoutGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "osamaelshareef621@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "This is my subject text");
                startActivity(Intent.createChooser(emailIntent, null));
            }
        });
    }

    private void onClickWhatsAppIcon(){
        binding.textInputLayoutWhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "+970599080858";
                String url = "https://api.whatsapp.com/send?phone="+phone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void onClickPhoneIcon(){
        binding.textInputLayoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String phone = "0599080858";
                callIntent.setData(Uri.parse("tel:" + Uri.encode(phone)));//change the number.
                startActivity(callIntent);

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        finish();
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }
}