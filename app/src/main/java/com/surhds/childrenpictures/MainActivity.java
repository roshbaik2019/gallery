package com.surhds.childrenpictures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.surhds.childrenpictures.External.ContactUs;
import com.surhds.childrenpictures.fragmentBottomMenu.AddPhotoFragment;
import com.surhds.childrenpictures.fragmentBottomMenu.SectionFragment;
import com.surhds.childrenpictures.fragmentBottomMenu.UserPhotoFragment;

import static com.surhds.childrenpictures.External.AppRater.showRateDialog;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // tollbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // اظهار الفريجمنت الاساسي الذي تريد ان يظهر للمستخدم اولا
        displayFragment(new SectionFragment());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_us:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.share_myapp) + " " + getString(R.string.app_name) + " " +  getString(R.string.share_myapp1) + getString(R.string.share_myapp2) + "\n" +
                        "https://play.google.com/store/apps/details?id="+ getPackageName() +  " \n";
                //String shareBody ="\uD83D\uDC48هاى اصدقائي انصحكم بتطبيق\uD83D\uDE0D حالات واتس اب 2020\uD83E\uDD70 بة صور مكتوب عليها عبارات  رائعة\uD83D\uDC4D تشاركها بسرعة للفيسبوك وواتساب\uD83D\uDC47 رابط التحميل من جوجل بلاي\uD83D\uDCAA" + "\n https://play.google.com/store/apps/details?id="+ getPackageName() +" \n";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));
                return true;

            case R.id.Rate_us:
                showRateDialog(MainActivity.this);
                return true;
            case R.id.Our_app:
                // User chose the "Our_app" action, mark the current item
                // as a Our_app...
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=surhds.com")));
                return true;
            case R.id.Contact_us:
                // User chose the "Contact_us" item, show the app settings UI...
                Intent intentus = new Intent(getApplicationContext(), ContactUs.class);
                startActivity(intentus);

                return true;
            case R.id.Privacy_policy:
                // User chose the "Privacy_policy" action
                Uri uri = Uri.parse("https://surhds.com/privacy-policy/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        showRateDialog(MainActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        // الوصول الى المفاتيح فى البوتوم نافيجيشن
        switch (item.getItemId()){
            case R.id.menu_home:
                fragment = new SectionFragment();
                break;
            case R.id.menu_collections:
                fragment = new UserPhotoFragment();
                break;
            case R.id.menu_add_photo:
                fragment = new AddPhotoFragment();
                break;
        }

        if (fragment != null){
            displayFragment(fragment);
        }

        return false;
    }
    private void displayFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.relativeLayout,fragment)
                .commit();
    }

}