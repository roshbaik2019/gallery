package com.surhds.childrenpictures.adapters;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.surhds.childrenpictures.Activity.AllPhoto;
import com.surhds.childrenpictures.R;
import com.surhds.childrenpictures.models.section;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.PhotoViewHolder> {
private Context context;
private List<section> sectionList;

    public SectionAdapter(Context context, List<section> sectionList) {
        this.context = context;
        this.sectionList = sectionList;
    }

    @NonNull
    @Override
    public SectionAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cards_layout_sections, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        section section1 = sectionList.get(position);
        Log.d("url:",section1.getUrl().trim());

            // Glide load image
            Glide.with(context)
                    .load(section1.getUrl().trim())
                    .centerCrop()
                    .placeholder(R.drawable.insert_photo)
                    .error(R.drawable.time)
                    .into(holder.section_image);

            holder.photoName.setText(section1.getSectionName().trim());



        // عند الضغط علي صورة القسم

        holder.section_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Bundle extras = new Bundle();
                    extras.putInt("SECTION_ID", section1.getId());
                    Intent intent = new Intent(context, AllPhoto.class);
                    intent.putExtras(extras);
                    context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return sectionList.size();

    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView section_image;
        TextView photoName;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            section_image = itemView.findViewById(R.id.section_image);
            photoName = itemView.findViewById(R.id.photoName);

        }
    }
}
