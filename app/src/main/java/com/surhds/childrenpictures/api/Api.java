package com.surhds.childrenpictures.api;

import com.surhds.childrenpictures.models.AdsResponse;
import com.surhds.childrenpictures.models.DefaultResponse;
import com.surhds.childrenpictures.models.PhotoinsectionResponse;
import com.surhds.childrenpictures.models.SectionResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Api {
// الحصول علي جميع الاقسام
    @GET("allsection")
    Call<SectionResponse> allsection();

    // الحصول على جميع الصور داخل قسم ما
    @GET("photoinsection/{id}")
    Call<PhotoinsectionResponse>photoinsection(
            @Path("id") int id);

    // رفع صورة الى السيرفر
    @Multipart
    @POST("Upload")
    Call<DefaultResponse> Uploadfile(
            @Part("sectionName") RequestBody sectionName,
            @Part("imageType") RequestBody imageType,
            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @PUT("saveview")
    Call<DefaultResponse> saveview(
            @Field("id") int id,
            @Field("views") String views
    );

    // الحصول علي جميع الاعلانات
    @GET("allads")
    Call<AdsResponse> allads();

    // تحديث عدد مشاهدات الاعلان
    @FormUrlEncoded
    @PUT("saveviewads")
    Call<DefaultResponse> saveviewads(
            @Field("id") int id,
            @Field("views") int views
    );

    @FormUrlEncoded
    @PUT("saveclickads")
    Call<DefaultResponse> saveclickads(
            @Field("id") int id,
            @Field("click") int click
    );

}
