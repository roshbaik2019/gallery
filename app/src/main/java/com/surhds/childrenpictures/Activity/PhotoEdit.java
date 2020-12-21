package com.surhds.childrenpictures.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.surhds.childrenpictures.BuildConfig;
import com.surhds.childrenpictures.R;
import com.surhds.childrenpictures.api.RetrofitClient;
import com.surhds.childrenpictures.models.DefaultResponse;
import com.surhds.childrenpictures.models.SectionResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoEdit extends AppCompatActivity {
    private int PHOTO_ID,width,height;
    private int totalView;
    private String URL;
    private String VIEW;
    private String fileNameToShare;
    private ImageView show_image,arrow_back,background,share,download;
    private File file;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        show_image = findViewById(R.id.show_image);
        arrow_back = findViewById(R.id.arrow_back);
        background = findViewById(R.id.background);
        share = findViewById(R.id.share);
        download = findViewById(R.id.download);
        setArrowBack();

        //  الحصول على  idللصورة

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        PHOTO_ID = extras.getInt("PHOTO_ID"); // ايدي الصورة
        URL = extras.getString("URL");
        VIEW = extras.getString("VIEW");

        Log.d("PHOTO_ID" , "->" + PHOTO_ID);
        Log.d("VIEWT" , "->" + VIEW);

        Glide.with(this)
                .load(URL)
                .centerCrop()
                .placeholder(R.drawable.insert_photo)
                .error(R.drawable.time)
                .into(show_image);

        sendViews();


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToSocialmedia() ;
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable)show_image.getDrawable()).getBitmap();
                fileNameToShare = saveImageFile(bm);
                Toast.makeText(PhotoEdit.this, getString(R.string.save) + "/" + file.getName() + "/", Toast.LENGTH_SHORT).show();

            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setwallpaper();
                Toast.makeText(PhotoEdit.this, getString(R.string.wallpaper_msg), Toast.LENGTH_SHORT).show();
            }
        });

        AdsInt();

    }

    private void sendViews() {
        // زيادة عدد مشاهدات الصورة بمعدل 1
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().saveview(PHOTO_ID,VIEW);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    if (!response.body().getMsg().isEmpty()){
                        Log.d("Views",response.body().getMsg());
                    }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {

            }
        });
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

    // Share Your image on fb/watsapp
    public void shareToSocialmedia(){
        // Start

        shareImage();


    }

    private void shareImage() {
        Bitmap bitmap = getBitmapFromView(show_image);
        try {
            File file = new File (this.getExternalCacheDir(),"temp.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);

            fOut.flush();
            fOut.close();
            file.setReadable(true,false);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            String shareBody = getString(R.string.share_myapp) + " " + getString(R.string.app_name) + " " +  getString(R.string.share_myapp1) + getString(R.string.share_myapp2) +  " \n" +
                    "https://play.google.com/store/apps/details?id="+ getPackageName() +  " \n";
            intent.putExtra(intent.EXTRA_TEXT,shareBody);
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(PhotoEdit.this, BuildConfig.APPLICATION_ID + ".provider",file));
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent,getString(R.string.share)));

        }
        catch (Exception e)

        {
            e.printStackTrace();
        }

    }

    private Bitmap getBitmapFromView(View view) {

        Bitmap returnBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnBitmap);
        Drawable bgDrawable = view.getBackground();
        if(bgDrawable !=null){
            bgDrawable.draw(canvas);
        }
        else {
            canvas.drawColor(Color.WHITE);


        }

        view.draw(canvas);
        return returnBitmap;
    }

    public String saveImageFile(Bitmap bitmap) {
        FileOutputStream out = null;

        String filename = getFilename();
        bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private void setwallpaper() {
       // DisplayMetrics displayMetrics = new DisplayMetrics();
       // getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int width = displayMetrics.widthPixels;

        //int height = displayMetrics.heightPixels;

        //////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            //return windowMetrics.getBounds().width() - insets.left - insets.right;
             width = windowMetrics.getBounds().width() - insets.left - insets.right;
             height = windowMetrics.getBounds().height() - insets.left - insets.right;

        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //return displayMetrics.widthPixels;
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }
        /////////////////////////////////////////////////


        Bitmap bm = ((BitmapDrawable)show_image.getDrawable()).getBitmap();
        bm = Bitmap.createScaledBitmap(bm, width, height, true);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(PhotoEdit.this);
        try {
            wallpaperManager.setBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFilename() {
        String folderNameToSave = getString(R.string.app_name);
        // File file ;

        //file = new File(Environment.getExternalStorageDirectory().getPath(), folderNameToSave);
        file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), folderNameToSave);


        if (!file.exists()) {
            file.mkdirs();
        }


        String uriSting = (file.getAbsolutePath() + "/"
                + "photo" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }
    //End Method For Share Images on FB/WATSAPP

    @Override
    protected void onStart() {
        super.onStart();
    }


    // loadads
    ///////////////////////////////////////////////////////////

    private void AdsInt(){
        /// ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        mInterstitialAd = new InterstitialAd(PhotoEdit.this);
        mInterstitialAd.setAdUnitId(getString(R.string.adUnitIdMessage));
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