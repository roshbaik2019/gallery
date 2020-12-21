package com.surhds.childrenpictures.fragmentBottomMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.surhds.childrenpictures.MainActivity;
import com.surhds.childrenpictures.R;
import com.surhds.childrenpictures.api.RetrofitClient;
import com.surhds.childrenpictures.models.DefaultResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import naseem.ali.flexibletoast.EasyToast;
import naseem.ali.flexibletoast.ToastBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddPhotoFragment extends Fragment {
    private View view;
    private static final int PERMISSION_ALL = 300; // وجود صلاحيات المعرض والكاميرا
    private static final int PICK_IMAGE = 100;
    private static final int PICK_CAMIRA = 101;
    private Uri imageUri;
    private ImageView Up_image;
    private Button btn_upload;
    private String fileNameToShare;
    private File file ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_add_photo, container, false);
        Up_image = view.findViewById(R.id.Up_image);
        btn_upload = view.findViewById(R.id.btn_upload);

        EasyToast easyToast=new ToastBuilder(getContext())
                .textColor(Color.WHITE)
                .type(EasyToast.INFO)
                .leftIcon(R.drawable.upload)
                .duration(50000)
                .text(getString(R.string.ballon))
                .build();
        easyToast.show();
        // اظهار طلب الاذونات
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            String[] PERMISSIONS = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
            };

            if (!hasPermissions(getContext(), PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSION_ALL);

            }
        }
        ///////////////////////////////////////

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // اختبار اذونات الكاميرا والمعرض
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    String[] PERMISSIONS = {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA
                    };

                    if (!hasPermissions(getContext(), PERMISSIONS)) {
                        requestPermissions( PERMISSIONS, PERMISSION_ALL);

                    } else {
                        // اذا كانت الاذونات مسموح بها من قبل
                        // اظهار قائمة اختيار الكاميرا او المعرض
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(getString(R.string.chooseImage));
                        builder.setItems(R.array.dialog_profile_image_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                // The 'index' argument contains the index position
                                // of the selected item
                                // Gallery
                                switch (index){
                                    case 0:
                                        openGallery();
                                        //Toast.makeText(getContext(),"gallery",Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        openCamira();
                                        break;

                                }

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        ////////

                    }

                }



            }
        });
        return view;
    }

    // Start My Method
    // مثود اختبار الاذونات
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSION_ALL:{

                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(getContext(),"تم السماح",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(),getString(R.string.Allow),Toast.LENGTH_LONG).show();
                }

            }


        }



    }
    // مثود فتح معرض الصور
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    // مثود فتح الكاميرا
    private void openCamira(){
        Context context = getContext();
        final PackageManager pm = context.getPackageManager();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(pm) != null) {
            startActivityForResult(takePictureIntent, PICK_CAMIRA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            //Log.d("imageUri","=" + imageUri);
            Up_image.setImageURI(imageUri);

            // start save your image to folder
            SaveToStorage();



        }
        if (resultCode == RESULT_OK && requestCode == PICK_CAMIRA){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Up_image.setImageBitmap(imageBitmap);
            // start save your image to folder
            SaveToStorage();


        }
    }
    private void SaveToStorage() {
        Bitmap bm = ((BitmapDrawable)Up_image.getDrawable()).getBitmap();
        fileNameToShare = saveImageFile(bm);
        Uri imgUri = Uri.parse(fileNameToShare); // مسار الصورة بالكامل داخل المجلد
        Log.d("url" ,"  " + imgUri.toString());
        // حفظ مسار الصورة فى القرص الداخلي
        UploadFile(imgUri.toString());
       // SharedPrefManager.getInstance(getContext()).SaveMRegister2(imgUri.toString());

    }
    ///////////////////////////////////////////////////
    // function to save image to folder
    public String saveImageFile(Bitmap bitmap) {
        FileOutputStream out = null;

        String filename = getFilename();
        bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 500, false);

        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }
    private String getFilename() {
        String folderNameToSave = getString(R.string.app_name);
        // File file ;

        //file = new File(Environment.getExternalStorageDirectory().getPath(), folderNameToSave);
        file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), folderNameToSave);


        if (!file.exists()) {
            file.mkdirs();
        }


        String uriSting = (file.getAbsolutePath() + "/"
                + "tmp" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    // رفع الصورة الى السيرفر
   public void UploadFile(String imgUri){
       File file = new File(imgUri);
       RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
       // file + اسم الصورة
       MultipartBody.Part fileupload = MultipartBody.Part.createFormData("profileimage",file.getName(),requestBody);
       RequestBody sectionName = RequestBody.create(MediaType.parse("text/plain"), "3");
       RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "imageuser");
       Call<DefaultResponse> call = RetrofitClient
               .getInstance()
               .getApi()
               .Uploadfile(sectionName,imageType,fileupload);

       call.enqueue(new Callback<DefaultResponse>() {
           @Override
           public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
               assert response.body() != null;
               if (!response.body().getMsg().isEmpty()) {

                   Toast.makeText(getContext(),getString(R.string.uploadcomplete), Toast.LENGTH_LONG).show();
               }
           }

           @Override
           public void onFailure(Call<DefaultResponse> call, Throwable t) {
               Toast.makeText(getContext(), getString(R.string.connection), Toast.LENGTH_LONG).show();

           }
       });

   }


}