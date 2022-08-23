package com.sensustech.iconthemer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.activities.CropActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PhotoFragment extends Fragment {
    private ImageView app_icon;
    ConstraintLayout b_photo;
    ConstraintLayout b_gallery;
    private Uri imageUri;
    private Toast toast;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    static Bitmap mImage;

    public PhotoFragment() {
        // Required empty public constructor
    }
    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo, container, false);
        app_icon = getActivity().findViewById(R.id.selected_app_icon);
        b_photo = root.findViewById(R.id.b_camera);
        b_gallery = root.findViewById(R.id.b_gallery);
        b_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
            }
        });
        b_gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
            }
        });
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String fileName = "new-photo-name.jpg";
                // Create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(android.provider.MediaStore.Images.Media.TITLE, fileName);
                values.put(android.provider.MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 0);
            } else {
//                showAToast("denied");
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            } else {
//                showAToast("denied");
            }
        }
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Intent crop_intent = new Intent(getContext(), CropActivity.class);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            crop_intent.putExtra("imageUri", imageUri.toString());
            startActivityForResult(crop_intent, 3);
        }
        if (requestCode == 1 &&resultCode == Activity.RESULT_OK) {
            Uri selected_image =  data.getData();
            crop_intent.putExtra("imageUri", selected_image.toString());
            startActivityForResult(crop_intent, 3);
        }
        if (requestCode == 3) {
            if (mImage != null) {
                app_icon.setImageBitmap(mImage);
            }
            try {
                byte[] byteArray = data.getByteArrayExtra("image");
                Bitmap cropped_image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                app_icon.setImageBitmap(cropped_image);
            }
            catch (Exception e) {
            }
        }
    }
}
