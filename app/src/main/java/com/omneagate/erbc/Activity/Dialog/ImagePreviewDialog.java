package com.omneagate.erbc.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.omneagate.erbc.R;

/**
 * Created by user1 on 26/5/16.
 */
public class ImagePreviewDialog extends Dialog implements
        View.OnClickListener {


    private final Context context;
    String title;
    Bitmap images_bitmap;

    /*Constructor class for this dialog*/
    public ImagePreviewDialog(Context _context, Bitmap bitmap_img) {
        super(_context);
        context = _context;
        images_bitmap = bitmap_img;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.imageviewdialog);
        setCancelable(false);
        Button okButton = (Button) findViewById(R.id.buttonclose);
        ImageView images = (ImageView) findViewById(R.id.imageView3);
        images.setImageBitmap(images_bitmap);
        okButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonclose:
                dismiss();
                break;
       }
   }
}