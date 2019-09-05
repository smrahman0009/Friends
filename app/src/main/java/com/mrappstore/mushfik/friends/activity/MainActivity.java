package com.mrappstore.mushfik.friends.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mrappstore.mushfik.friends.R;

public class MainActivity extends AppCompatActivity {
    Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this,"Log out successfully",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }
                }
        );


    }
}
