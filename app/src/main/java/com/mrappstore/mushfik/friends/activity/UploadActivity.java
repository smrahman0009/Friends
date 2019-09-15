package com.mrappstore.mushfik.friends.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.mrappstore.mushfik.friends.R;
import com.mrappstore.mushfik.friends.api.ApiClient;
import com.mrappstore.mushfik.friends.api.services.UserInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    @BindView(R.id.privacy_spinner)
    Spinner privacySpinner;

    @BindView(R.id.postBtnTxt)
    TextView postBtnTxt;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.dialogAvatar)
    CircleImageView dialogAvatar;

    @BindView(R.id.status_edit)
    EditText statusEdit;

    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.add_image)
    Button addImage;

    String imageUploadUrl = "";
    boolean isImageSelected = false;
    ProgressDialog progressDialog;
    File compressedImageFile = null;
    int privacyLevel = 0;

    /*
    0 -> Friends
    1 -> Only Me
    2 -> Public
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);

        //////// SET UP TOOLBAR ///////////////
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        getSupportActionBar().setTitle("");

        ///////////// BACK TO THE PREVIOUS ACTIVITY //////////////////////
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //////////// SET UP PROGRESS DIALOG /////////////////
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");

        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                privacyLevel = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                privacyLevel = 0;
            }
        });

        ////////////// UPLOAD IMAGE ////////////////////////////////
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(UploadActivity.this)
                        .folderMode(true)
                        .single().start();
            }
        });

        //////////// POST BUTTON ////////////////
        postBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });
    }

    private void uploadPost() {
        String status = statusEdit.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ///////////// is post button is clicked then we need to check both the status and post field.
        //////////// if both are absent. then we won't proceed.

        if (status.trim().length() > 0 || isImageSelected){

            progressDialog.show();

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            builder.addFormDataPart("post",status);
            builder.addFormDataPart("postUserId",userId);
            builder.addFormDataPart("privacy",privacyLevel+"");

            if (isImageSelected){
                builder.addFormDataPart("isImageSelected","1");
                builder.addFormDataPart("file",compressedImageFile.getName(), RequestBody.create(
                        MediaType.parse("multipart/form-data"),compressedImageFile
                ));
            }
            else {
                builder.addFormDataPart("isImageSelected","0");
            }

            MultipartBody multipartBody = builder.build();
            UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
            Call<Integer> call = userInterface.uploadStatus(multipartBody);
            call.enqueue(new Callback<Integer>() {

                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    progressDialog.dismiss();
                    if (response.body() != null && response.body() == 1){
                        Toast.makeText(UploadActivity.this,"Post is successfully uploaded" +
                                "",Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(UploadActivity.this,MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(UploadActivity.this,"Post is not successful" +
                                "",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Toast.makeText(UploadActivity.this,"Something went wrong." +
                            "Please try again..",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        }
        else {
            Toast.makeText(UploadActivity.this,"Please write your post first.",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);

            try {
                compressedImageFile = new Compressor(this)
                        .setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));

                isImageSelected = true;
                Picasso.with(UploadActivity.this).load(new File(selectedImage.getPath())).placeholder(R.drawable.default_image_placeholder);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
