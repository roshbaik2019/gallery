package com.surhds.childrenpictures.External;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.surhds.childrenpictures.R;

import java.net.URLEncoder;

public class ContactUs extends AppCompatActivity {
    private TextView textView;
    private Button btn_contact;
    private ImageView arrow_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        textView = findViewById(R.id.textcontact);
        btn_contact = findViewById(R.id.btn_contact);
        textView.setText(getString(R.string.contactus));
        arrow_back = findViewById(R.id.arrow_back);
        setArrowBack();
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+201153450716&&text=" + getString(R.string.Application_request)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setArrowBack() {
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}