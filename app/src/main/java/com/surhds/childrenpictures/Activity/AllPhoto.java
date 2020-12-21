package com.surhds.childrenpictures.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.surhds.childrenpictures.R;
import com.surhds.childrenpictures.adapters.InSectionAdapter;
import com.surhds.childrenpictures.adapters.SectionAdapter;
import com.surhds.childrenpictures.api.RetrofitClient;
import com.surhds.childrenpictures.models.PhotoinsectionResponse;
import com.surhds.childrenpictures.models.photoinsection;
import com.surhds.childrenpictures.models.section;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllPhoto extends AppCompatActivity {
    private int Section_ID;
    RelativeLayout relativeLayoutPhotoinsectionsContent;
    LinearLayout linearLayoutPhotoinsectionsEmpty;
    ProgressBar progressBarPhotoinsectionsView;
    private RecyclerView recyclerView;
    private InSectionAdapter adapter;
    private List<photoinsection> photoinsectionList;
    private ImageView arrow_back;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_photo);

        arrow_back = findViewById(R.id.arrow_back);
        setArrowBack();

        relativeLayoutPhotoinsectionsContent = findViewById(R.id.relativeLayoutPhotoinsectionsContent);
        linearLayoutPhotoinsectionsEmpty = findViewById(R.id.linearLayoutPhotoinsectionsEmpty);
        progressBarPhotoinsectionsView = findViewById(R.id.progressBarPhotoinsectionsView);
        recyclerView = findViewById(R.id.my_recycler_view_Photoinsections);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        // الحصول على id القسم الخاص للحصول علي جميع الصور الخاصة بالقسم
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Section_ID = extras.getInt("SECTION_ID"); // ايدي المستخدم
        Log.d("SECTION_ID" , "->" + Section_ID);
        AdsInt();
    }

    @Override
    public void onBackPressed() {
        showAds();
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

    @Override
    protected void onStart() {
        super.onStart();
        Call<PhotoinsectionResponse> call = RetrofitClient.getInstance().getApi().photoinsection(Section_ID);
        call.enqueue(new Callback<PhotoinsectionResponse>() {
            @Override
            public void onResponse(Call<PhotoinsectionResponse> call, Response<PhotoinsectionResponse> response) {
                if (!response.body().getPhotoinsections().isEmpty()){
                    Log.d("Photoinsections", "->" + response.body().getPhotoinsections());
                    photoinsectionList = response.body().getPhotoinsections();
                    adapter = new InSectionAdapter(getApplication(),photoinsectionList);
                    // اضافة كود الاعلانات عن طريق المكتبة

                    AdmobNativeAdAdapter admobNativeAdAdapter=AdmobNativeAdAdapter.Builder
                            .with(
                                    "ca-app-pub-8838614073880804/4319487992",//Create a native ad id from admob console
                                    adapter,//The adapter you would normally set to your recyClerView
                                    "custom"//Set it with "small","medium" or "custom"
                            )
                            .adItemIterval(6)//native ad repeating interval in the recyclerview
                            .build();

                    recyclerView.setAdapter(admobNativeAdAdapter);

                    progressBarPhotoinsectionsView.setVisibility(View.GONE);
                    relativeLayoutPhotoinsectionsContent.setVisibility(View.VISIBLE);
                    linearLayoutPhotoinsectionsEmpty.setVisibility(View.GONE);
                } else{
                    progressBarPhotoinsectionsView.setVisibility(View.GONE);
                    relativeLayoutPhotoinsectionsContent.setVisibility(View.GONE);
                    linearLayoutPhotoinsectionsEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PhotoinsectionResponse> call, Throwable t) {
                relativeLayoutPhotoinsectionsContent.setVisibility(View.GONE);
                linearLayoutPhotoinsectionsEmpty.setVisibility(View.VISIBLE);
            }
        });

    }

    // loadads
    ///////////////////////////////////////////////////////////

    private void AdsInt(){
        /// ads

        mInterstitialAd = new InterstitialAd(AllPhoto.this);
        mInterstitialAd.setAdUnitId(getString(R.string.adUnitId2));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        //////

    }
    private void showAds() {
        // load and show ads
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }
}