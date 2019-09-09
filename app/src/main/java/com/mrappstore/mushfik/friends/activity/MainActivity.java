package com.mrappstore.mushfik.friends.activity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mrappstore.mushfik.friends.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.search)
    ImageView search;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.framelayout)
    FrameLayout framelayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ///// SET USERS TOOLBAR TO DISPLAY ///////////////////////
        setSupportActionBar(toolbar);

        /////////////// DISABLE DISPLAYING APP DEFAULT TITLE BAR //////////////////
        getSupportActionBar().setDisplayShowTitleEnabled(false);


//
//        ////////////// SET BOTTOM NAVIGATION BAR //////////////////
        bottomNavigation.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigation.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigation.setItemTextColor(ContextCompat.getColorStateList(bottomNavigation.getContext(),R.color.nav_item_colors));
        bottomNavigation.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigation.getContext(),R.color.nav_item_colors));

    }
}
