package com.surhds.childrenpictures.External;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.surhds.childrenpictures.R;

public class AppRater {

    public static void showRateDialog(final Context mContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);
        builder.setIcon(R.drawable.ic_star);
        builder.setTitle(R.string.rate );
        builder.setMessage(mContext.getResources().getString(R.string.description_rate));

        //Button One : Yes
        builder.setPositiveButton(mContext.getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Toast.makeText(mContext, "Yes button Clicked!", Toast.LENGTH_LONG).show();*/
                System.exit(1);

            }
        });


        builder.setNegativeButton(
                mContext.getResources().getString(R.string.NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }
        );


        //Button Three : Neutral
        builder.setNeutralButton(mContext.getResources().getString(R.string.rate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(mContext, "Neutral button Clicked!", Toast.LENGTH_LONG).show();
                //dialog.cancel();
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+mContext.getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+mContext.getPackageName())));
                }

            }
        });


        AlertDialog diag = builder.create();
        diag.show();

    }
}
