package com.example.eduardorodriguez.comeaqui.profile.edit_profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.eduardorodriguez.comeaqui.R;
import com.example.eduardorodriguez.comeaqui.server.PatchAsyncTask;
import com.yalantis.ucrop.UCrop;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CropImageActivity extends AppCompatActivity {

    ImageView image;
    Button save;
    Button discard;
    String SAMPLE_CROP_IMAGE_NAME = "SampleCropImage";

    static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int PICK_IMAGE = 1;

    boolean isBackGround;

    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        image = findViewById(R.id.image);
        save = findViewById(R.id.save);
        discard = findViewById(R.id.discard);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null && b.get("is_camera") != null) {
            boolean is_camera = b.getBoolean("is_camera");
            isBackGround = b.getBoolean("is_back_ground");
            if(is_camera){
                openCamera();
            } else {
                openGallery();
            }
        }
        discard.setOnClickListener(v -> finish());
        save.setOnClickListener(v -> {
            saveImage();
        });

    }

    private void saveImage(){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            if (bitmap != null){
                PatchAsyncTask putTask = new PatchAsyncTask(getResources().getString(R.string.server) + "/edit_profile/");
                putTask.imageBitmap = bitmap;

                if (isBackGround){
                    putTask.execute("background_photo", "", "true").get(15, TimeUnit.SECONDS);
                    finish();
                }else {
                    putTask.execute("profile_photo", "", "true").get(15, TimeUnit.SECONDS);
                    finish();
                }

            }
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(){
        Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(m_intent, REQUEST_IMAGE_CAPTURE);
    }
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            if (selectedImage != null)
                startCrop(selectedImage);

        } else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            imageUri = UCrop.getOutput(data);
            if(imageUri != null){
                image.setImageURI(imageUri);
            }
        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            File file = createImageFile();
            if (file != null) {
                FileOutputStream fout;
                try {
                    fout = new FileOutputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ((Bitmap) data.getExtras().get("data")).compress(Bitmap.CompressFormat.PNG, 70, fout);
                    fout.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(file);
                startCrop(uri);
            }
        }
    }

    private void startCrop(Uri uri){
        String destinationFileName = SAMPLE_CROP_IMAGE_NAME;
        destinationFileName += ".jpg";

        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)))
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
        options.setToolbarColor(Color.parseColor("#ffffff"));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Image Crop");

        return options;
    }

    public File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        String root = getDir("my_sub_dir",Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root + "/Img");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
        try {
            mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mFileTemp;
    }
}
