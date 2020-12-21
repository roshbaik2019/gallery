package com.surhds.childrenpictures.fragmentBottomMenu;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.surhds.childrenpictures.R;
import com.surhds.childrenpictures.adapters.SectionAdapter;
import com.surhds.childrenpictures.api.RetrofitClient;
import com.surhds.childrenpictures.models.Ads;
import com.surhds.childrenpictures.models.AdsResponse;
import com.surhds.childrenpictures.models.DefaultResponse;
import com.surhds.childrenpictures.models.SectionResponse;
import com.surhds.childrenpictures.models.section;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SectionFragment extends Fragment {
    RelativeLayout relativeLayoutHomeContent;
    LinearLayout linearLayoutHomeEmpty;
    ProgressBar progressBarHomeView;
    private RecyclerView recyclerView;
    private SectionAdapter adapter;
    private List<section> sectionList;
    private List<Ads> adsList;
    private int IDads,click,views;
    private String Codeads;
    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_section, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        relativeLayoutHomeContent = view.findViewById(R.id.relativeLayoutHomeContent);
        linearLayoutHomeEmpty = view.findViewById(R.id.linearLayoutHomeEmpty);
        progressBarHomeView = view.findViewById(R.id.progressBarHomeView);
        recyclerView = view.findViewById(R.id.my_recycler_view_Home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        // code html for ads1
        LoadingMyAds();
        webView = (WebView) view.findViewById(R.id.webView);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // عند الضغط على اعلان الخدمات
                //1- فتح الواتساب للتواصل
                 Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+201153450716&&text=" + getString(R.string.Application_request)); // missing 'http://' will cause crashed
                 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                 startActivity(intent);
                // 2- ارسال الضغطة لتسجيلها الى السيرفر
                SendClickAds(IDads,click);
                return false;
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        Call<SectionResponse> call = RetrofitClient.getInstance().getApi().allsection();
        call.enqueue(new Callback<SectionResponse>() {
            @Override
            public void onResponse(Call<SectionResponse> call, Response<SectionResponse> response) {
                if (!response.body().getSectionList().isEmpty()) {
                    Log.d("SectionList", "->" + response.body().getSectionList());
                    sectionList = response.body().getSectionList();
                    adapter = new SectionAdapter(getActivity(), sectionList);
                    recyclerView.setAdapter(adapter);
                    // load my ads
                    //Log.d("Sectionads", "->" + sectionList.get(3).getSectionName());
                   //image_ads.setImageURI(Uri.parse(sectionList.get(3).getUrl()));
                    /*
                    Glide.with(getContext())
                            .load(sectionList.get(3).getUrl().trim())
                            .centerCrop()
                            .override(200,60)
                            .placeholder(R.drawable.insert_photo)
                            .error(R.drawable.time)
                            .into(image_ads);

                    */
                    progressBarHomeView.setVisibility(View.GONE);
                    relativeLayoutHomeContent.setVisibility(View.VISIBLE);
                    linearLayoutHomeEmpty.setVisibility(View.GONE);
                } else {
                    progressBarHomeView.setVisibility(View.GONE);
                    relativeLayoutHomeContent.setVisibility(View.GONE);
                    linearLayoutHomeEmpty.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onFailure(Call<SectionResponse> call, Throwable t) {
                relativeLayoutHomeContent.setVisibility(View.GONE);
                linearLayoutHomeEmpty.setVisibility(View.VISIBLE);

            }
        });



    }

    // اظهار اعلان الافليت
    public void LoadingMyAds(){
        Call<AdsResponse> call = RetrofitClient.getInstance().getApi().allads();
        call.enqueue(new Callback<AdsResponse>() {
            @Override
            public void onResponse(Call<AdsResponse> call, Response<AdsResponse> response) {
                if (!response.body().getAds().isEmpty()){
                    adsList = response.body().getAds();
                    Codeads = adsList.get(0).getCode(); // شفرة الاعلان
                    IDads = adsList.get(0).getId(); // معرف الاعلان
                    views = adsList.get(0).getViews();//عدد مشاهدات الاعلان
                    click = adsList.get(0).getClick(); // عدد الضغطات على الاعلان
                    //Log.w("ads", adsList.get(0).getCode());
                    //Log.w("ads", "" + adsList.get(0).getId());
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadDataWithBaseURL(null, Codeads, "text/html", "utf-8", null);
                    SendViewsAds(IDads,views);
                }
            }

            @Override
            public void onFailure(Call<AdsResponse> call, Throwable t) {

            }
        });
    }
    // ارسال الضغطة الى السيرفر عندما يضغط الزائر على الاعلان
    public void SendClickAds(int id,int click){
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().saveclickads(id,click);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (!response.body().getMsg().isEmpty()){
                    Log.d("clickads",response.body().getMsg().trim());

                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {

            }
        });

    }

    // ارسال مشاهدة الاعلان الي السيرفر
    public void SendViewsAds(int id,int views){
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().saveviewads(id,views);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (!response.body().getMsg().isEmpty()){
                    Log.d("viewads",response.body().getMsg().trim());

                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {

            }
        });
    }

}