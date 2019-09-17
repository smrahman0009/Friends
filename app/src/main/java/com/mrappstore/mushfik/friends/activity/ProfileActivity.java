package com.mrappstore.mushfik.friends.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mrappstore.mushfik.friends.R;
import com.mrappstore.mushfik.friends.adapter.ProfileViewPagerAdapter;
import com.mrappstore.mushfik.friends.api.ApiClient;
import com.mrappstore.mushfik.friends.api.services.UserInterface;
import com.mrappstore.mushfik.friends.model.User;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    @BindView(R.id.profile_cover)
    ImageView profileCover;

    @BindView(R.id.profile_image)
    CircleImageView profileImage;

    @BindView(R.id.profile_option_btn)
    Button profileOptionBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.ViewPager_profile)
    ViewPager ViewPagerProfile;

    ProfileViewPagerAdapter profileViewPagerAdapter;

    String uid = "0";

    /*
      0 = profile is still loading
      1 = two people are friends ( unfriend )
      2 = this person has sent friend request to another friend (cancel friend request)
      3 = this person has received friend request from another friend (reject or accept request)
      4 = people are unknown ( you can send request )
      5 = own profile
     */
    int CURRENT_STATE = 0;
    int IMAGE_UPLOAD_TYPE = 0;
    String profileUrl="",coverUrl="";
    ProgressDialog progressDialog;
    private File compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///////// FOR HIDING STATUS BAR //////////////
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        uid = getIntent().getStringExtra("uid");

        /*
        As soon as the ProfileActivity opens up we want to show the progress dialog.
         */
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(),1);


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                finish();
            }
        });

        ViewPagerProfile.setAdapter(profileViewPagerAdapter);
        /*
        check the uid is own or others.
         */
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(uid)){
            // UID  is matched, we are going to load our own profile
            CURRENT_STATE = 5;
            profileOptionBtn.setText("Edit Profile");
            loadProfile();
        }
        else {
            // load other people here

        }

        profileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_STATE == 5){
                    CharSequence options[] = new CharSequence[]{"Change Cover Profile","Change Profile Picture","" +
                            "View Cover Photo","View Profile Picture"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setOnDismissListener(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0){
                                IMAGE_UPLOAD_TYPE = 1;
                                ImagePicker.create(ProfileActivity.this)
                                        .folderMode(true)
                                        .single()
                                        .toolbarFolderTitle("Choose a folder")
                                        .toolbarImageTitle("Choose an image")
                                        .start();
                                /*
                                Change cover profile
                                 */
                            }
                            else if (position == 1){
                                IMAGE_UPLOAD_TYPE = 0;
                                ImagePicker.create(ProfileActivity.this)
                                        .folderMode(true)
                                        .single()
                                        .toolbarFolderTitle("Choose a folder")
                                        .toolbarImageTitle("Choose an image")
                                        .start();
                                /*O
                                    Change profile picture
                                 */
                            }
                            else if (position == 2){
                                IMAGE_UPLOAD_TYPE = 2;
                                /*
                                View cover photo
                                 */
                            }
                            else {
                                IMAGE_UPLOAD_TYPE = 3;
                                /*
                                View Profile Picture
                                 */
                            }
                        }
                    });
                    builder.show();

                }
            }
        });


    }

    private void loadProfile() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String,String> params = new HashMap<>();
        params.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call<User> call = userInterface.loadownprofile(params);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                /*
                After loading the profile from the network. we want to dismiss the progress dialog.
                 */
                progressDialog.dismiss();
                if (response.body() != null){
                    profileUrl = response.body().getProfileUrl();
                    coverUrl = response.body().getCoverUrl();
                    collapsingToolbar.setTitle(response.body().getName());

                    if (!profileUrl.isEmpty()){
                        //////// it tries to load the image from offline / cache. and it is able to
                        /// load the image from offline then onSuccess() method called
                        //// otherwise onError() method called.
                        //// incase of failure we load the image from online.

                        Picasso.with(ProfileActivity.this).load(profileUrl).networkPolicy(NetworkPolicy.OFFLINE)
                                .into(profileImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(ProfileActivity.this).load(profileUrl).into(profileImage);

                                    }
                                });
                    }

                    if (!coverUrl.isEmpty()){
                        //////// it tries to load the image from offline / cache. and it is able to
                        /// load the image from offline then onSuccess() method called
                        //// otherwise onError() method called.
                        //// incase of failure we load the image from online.

                        Picasso.with(ProfileActivity.this).load(coverUrl).networkPolicy(NetworkPolicy.OFFLINE)
                                .into(profileCover, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(ProfileActivity.this).load(coverUrl).into(profileCover);
                                    }
                                });
                    }

                }
                else {
                    Toast.makeText(ProfileActivity.this,"Something went wrong! .." +
                            "Please try again.",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this,"Something went wrong! .." +
                        "Please try again.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

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
                uploadFile(compressedImageFile);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(final File compressedImageFile) {
        ////////// NEED QUERY //////////////////////////////

        progressDialog.setTitle("Loading...");
        progressDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("postUserId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        builder.addFormDataPart("imageUploadType",IMAGE_UPLOAD_TYPE+"");
        builder.addFormDataPart("file",compressedImageFile.getName(), RequestBody.create(
                MediaType.parse("multipart/form-data"),compressedImageFile
        ));


        MultipartBody multipartBody = builder.build();
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Call<Integer> call = userInterface.uploadImage(multipartBody);
        call.enqueue(new Callback<Integer>() {

            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                if (response.body() != null && response.body() == 1){
                    /*
                    When images successfully uploaded we want our Picasso to load out of digital images here.
                     */
                    /*
                    So we have to chekc whether the profile images uploaded or cover images uploaded.
                    if IMAGE_UPLOAD_TYPE == 0 then uploaded image is profile image.
                     */
                    if (IMAGE_UPLOAD_TYPE == 0){

                        Picasso.with(ProfileActivity.this)
                                .load(compressedImageFile)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_image_placeholder)
                                .into(profileImage, new com.squareup.picasso.Callback()
                                {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(ProfileActivity.this)
                                                .load(compressedImageFile)
                                                .placeholder(R.drawable.default_image_placeholder)
                                                .into(profileImage);
                                    }
                                });
                        Toast.makeText(ProfileActivity.this, "Profile Picture Changed Successfully", Toast.LENGTH_LONG).show();
                    }

                    else {
                        Picasso.with(ProfileActivity.this)
                                .load(compressedImageFile)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_image_placeholder)
                                .into(profileCover, new com.squareup.picasso.Callback() {

                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this)
                                        .load(compressedImageFile)
                                        .placeholder(R.drawable.default_image_placeholder)
                                        .into(profileCover);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Cover Picture Changed Successfully", Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    Toast.makeText(ProfileActivity.this,"Something went wrong..." +
                            "",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,"Something went wrong." +
                        "Please try again..",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
