package com.mrappstore.mushfik.friends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mrappstore.mushfik.friends.R;
import com.mrappstore.mushfik.friends.fragment.FriendsFragment;
import com.mrappstore.mushfik.friends.fragment.NewsFeedFragment;
import com.mrappstore.mushfik.friends.fragment.NotificationFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.framelayout)
    FrameLayout framelayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;


    NewsFeedFragment newsFeedFragment;
    NotificationFragment notificationFragment;
    FriendsFragment friendsFragment;

    @BindView(R.id.search)
    ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ///// SET USERS TOOLBAR TO DISPLAY ///////////////////////
        setSupportActionBar(toolbar);

        /////////////// DISABLE DISPLAYING APP DEFAULT TITLE BAR //////////////////
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ////////////// SET BOTTOM NAVIGATION BAR //////////////////
        bottomNavigation.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigation.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigation.setItemTextColor(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        bottomNavigation.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));


        /////////////////////// INITILIAZE FRAGMENT OBJECT ///////////////////////

        newsFeedFragment = new NewsFeedFragment();
        notificationFragment = new NotificationFragment();
        friendsFragment = new FriendsFragment();

        setFragment(newsFeedFragment);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.newsfeed_fragment:
                        setFragment(newsFeedFragment);
                        break;
                    case R.id.profile_fragment:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class)
                                .putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        break;
                    case R.id.profile_friends:
                        setFragment(friendsFragment);
                        break;
                    case R.id.profile_notification:
                        setFragment(notificationFragment);
                        break;
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));

            }
        });

    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }


    @OnClick(R.id.search)
    public void onViewClicked() {
        startActivity(new Intent(MainActivity.this,SearchActivity.class));

    }
}
