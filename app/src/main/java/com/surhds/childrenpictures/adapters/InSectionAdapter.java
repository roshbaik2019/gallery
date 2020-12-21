package com.surhds.childrenpictures.adapters;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.surhds.childrenpictures.Activity.PhotoEdit;
import com.surhds.childrenpictures.R;
import com.surhds.childrenpictures.models.photoinsection;
import java.util.List;

public class InSectionAdapter extends RecyclerView.Adapter<InSectionAdapter.AllPhotoViewHolder> {
    private Context context;
    private List<photoinsection> photoinsectionList;

    public InSectionAdapter(Context context, List<photoinsection> photoinsectionList) {
        this.context = context;
        this.photoinsectionList = photoinsectionList;
    }


    @NonNull
    @Override
    public InSectionAdapter.AllPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_layout_photoinsections, parent, false);
        return new AllPhotoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull InSectionAdapter.AllPhotoViewHolder holder, int position) {
        photoinsection photoinsection = photoinsectionList.get(position);
        Log.d("url:",photoinsection.getUrl());
        // Glide load image
        Glide.with(context)
                .load(photoinsection.getUrl().trim())
                .centerCrop()
                .placeholder(R.drawable.insert_photo)
                .error(R.drawable.time)
                .into(holder.Insection_image);

        holder.view.setText(photoinsection.getViews());
        // عند الضغط على اى صورة
        holder.Insection_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putInt("PHOTO_ID",photoinsection.getId());
                extras.putString("URL",photoinsection.getUrl().trim());
                extras.putString("VIEW",photoinsection.getViews());
                Intent intent = new Intent(context, PhotoEdit.class);
                intent.putExtras(extras);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return photoinsectionList.size();
    }

    public class AllPhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView Insection_image;
        TextView view;
        public AllPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            Insection_image = itemView.findViewById(R.id.Insection_image);
            view = itemView.findViewById(R.id.view);
        }
    }

}
