package com.sensustech.iconthemer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sensustech.iconthemer.R;
import com.sensustech.iconthemer.fragments.PhotoFragment;
import com.sensustech.iconthemer.utils.CropImageViewOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import static java.security.AccessController.getContext;

public class CropActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener  {

    private CropImageView mCropImageView;
    private CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Bundle extras = getIntent().getExtras();
        Uri selectedImage = Uri.parse(extras.getString("imageUri"));
        mCropImageView = findViewById(R.id.cropImageView);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
        updateCurrentCropViewOptions();
        if (savedInstanceState == null) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (selectedImage != null){
                Cursor cursor= getContentResolver().query(selectedImage,
                        filePathColumn,null,null,null);
                if(cursor!=null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    Bitmap finalBitmap = BitmapFactory.decodeFile(picturePath);
                    try {
                        ExifInterface exif = new ExifInterface(cursor.getString(columnIndex));
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        Matrix matrix = new Matrix();
                        if (orientation == 6) {
                            matrix.postRotate(90);
                        } else if (orientation == 3) {
                            matrix.postRotate(180);
                        } else if (orientation == 8) {
                            matrix.postRotate(270);
                        }
                    }
                    catch (Exception e) {
                    }
                    mCropImageView.setImageUriAsync(selectedImage);
                    cursor.close();
                }
            }
        }
    }

    /** Set the options of the crop image view to the given values. */
    public void setCropImageViewOptions(CropImageViewOptions options) {
        mCropImageView.setScaleType(options.scaleType);
        mCropImageView.setCropShape(options.cropShape);
        mCropImageView.setGuidelines(options.guidelines);
        mCropImageView.setAspectRatio(options.aspectRatio.first, options.aspectRatio.second);
        mCropImageView.setFixedAspectRatio(options.fixAspectRatio);
        mCropImageView.setMultiTouchEnabled(options.multitouch);
        mCropImageView.setShowCropOverlay(options.showCropOverlay);
        mCropImageView.setShowProgressBar(options.showProgressBar);
        mCropImageView.setAutoZoomEnabled(options.autoZoomEnabled);
        mCropImageView.setMaxZoom(options.maxZoomLevel);
        mCropImageView.setFlippedHorizontally(options.flipHorizontally);
        mCropImageView.setFlippedVertically(options.flipVertically);
    }

    public void updateCurrentCropViewOptions() {
        CropImageViewOptions options = new CropImageViewOptions();
        options.scaleType = mCropImageView.getScaleType();
        options.cropShape = mCropImageView.getCropShape();
        options.guidelines = mCropImageView.getGuidelines();
        options.aspectRatio = mCropImageView.getAspectRatio();
        options.fixAspectRatio = mCropImageView.isFixAspectRatio();
        options.showCropOverlay = mCropImageView.isShowCropOverlay();
        options.showProgressBar = mCropImageView.isShowProgressBar();
        options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
        options.maxZoomLevel = mCropImageView.getMaxZoom();
        options.flipHorizontally = mCropImageView.isFlippedHorizontally();
        options.flipVertically = mCropImageView.isFlippedVertically();
        setCurrentOptions(options);
    }

    public void setCurrentOptions(CropImageViewOptions options) {
        mCropImageViewOptions = options;
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(this, "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
//            Intent intent = new Intent(this, CropResultActivity.class);
//            Intent intent = new Intent(this, AppActivity.class);
//            intent.putExtra("SAMPLE_SIZE", result.getSampleSize());
            Bitmap bitmap = null;
            Intent intent = new Intent();
            if (result.getUri() != null) {
                intent.putExtra("URI", result.getUri());
            } else {
                if (mCropImageView.getCropShape() == CropImageView.CropShape.OVAL) {
                    bitmap = CropImage.toOvalBitmap(result.getBitmap());
                }
                if (mCropImageView.getCropShape() == CropImageView.CropShape.ROUNDED_RECTANGLE) {
                    bitmap = CropImage.toRoundedRectBitmap(result.getBitmap());
                }
                if (mCropImageView.getCropShape() == CropImageView.CropShape.RECTANGLE) {
                    bitmap = result.getBitmap();
                }
            }
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 20, bStream);
            byte[] byteArray = bStream.toByteArray();
            intent.putExtra("image", byteArray);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(
                    this,
                    "Image crop failed: " + result.getError().getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void backClick(View view) {
        finish();
    }

    public void onCropClick(View view) {
        mCropImageView.getCroppedImageAsync();
    }

    public void onRotateClick(View view) {
        mCropImageView.rotateImage(90);
    }

    public void onRoundedRectClick(View view) {
        mCropImageViewOptions.fixAspectRatio = true;
        mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
        mCropImageViewOptions.cropShape = CropImageView.CropShape.ROUNDED_RECTANGLE;
        setCropImageViewOptions(mCropImageViewOptions);
    }

    public void onSquareClick(View view) {
        mCropImageViewOptions.fixAspectRatio = true;
        mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
        mCropImageViewOptions.cropShape = CropImageView.CropShape.RECTANGLE;
        setCropImageViewOptions(mCropImageViewOptions);
    }

    public void onRectClick(View view) {
        mCropImageViewOptions.fixAspectRatio = false;
        mCropImageViewOptions.cropShape = CropImageView.CropShape.RECTANGLE;
        setCropImageViewOptions(mCropImageViewOptions);
    }

    public void onCircleClick(View view) {
        mCropImageViewOptions.fixAspectRatio = true;
        mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
        mCropImageViewOptions.cropShape = CropImageView.CropShape.OVAL;
        setCropImageViewOptions(mCropImageViewOptions);
    }

    public void onOvalClick(View view) {
        mCropImageViewOptions.fixAspectRatio = false;
        mCropImageViewOptions.cropShape = CropImageView.CropShape.OVAL;
        setCropImageViewOptions(mCropImageViewOptions);
    }
}



