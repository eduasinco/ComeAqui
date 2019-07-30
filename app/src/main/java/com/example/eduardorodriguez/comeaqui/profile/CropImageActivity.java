package com.example.eduardorodriguez.comeaqui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.FoodPost;
import com.example.eduardorodriguez.comeaqui.R;
import com.yalantis.ucrop.UCrop;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CropImageActivity extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        image = findViewById(R.id.image);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("is_camera") != null) {
            boolean is_camera = b.getBoolean("is_camera");
            if(is_camera){
            } else {
                openGallery();
            }

        }
    }

    public static final int PICK_IMAGE = 1;
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                if (selectedImage != null)
                    startCrop(selectedImage);

                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);

            } catch (IOException e){
                e.printStackTrace();
            }
        } else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            Uri imageResultCrop = UCrop.getOutput(data);
            if(imageResultCrop != null){
                image.setImageURI(imageResultCrop);
            }
        }
    }

    private void startCrop(Uri uri){
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), "MyImages")))
                .withAspectRatio(1, 1)
                .withMaxResultSize(450, 450)
                .withOptions(getCropOptions())
                .start(this);
    }

    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        // options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Image Crop");

        return options;
    }
}
