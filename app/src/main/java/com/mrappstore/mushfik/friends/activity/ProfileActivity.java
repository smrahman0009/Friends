package com.mrappstore.mushfik.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mrappstore.mushfik.friends.R;
import com.mrappstore.mushfik.friends.adapter.ProfileViewPagerAdapter;
import com.mrappstore.mushfik.friends.api.ApiClient;
import com.mrappstore.mushfik.friends.api.services.UserInterface;
import com.mrappstore.mushfik.friends.model.User;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

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
    String profileUrl="",coverUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///////// FOR HIDING STATUS BAR //////////////
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        uid = getIntent().getStringExtra("uid");

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
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(uid)){
            // UID  is matched, we are going to load our own profile
            CURRENT_STATE = 5;
            profileOptionBtn.setText("Edit Profile");
            loadProfile();
        }
        else {
            // load other people here

        }
    }

    private void loadProfile() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String,String> params = new HashMap<>();
        params.put("userId",FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call<User> call = userInterface.loadownprofile(params);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

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
                Toast.makeText(ProfileActivity.this,"Something went wrong! .." +
                        "Please try again.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
