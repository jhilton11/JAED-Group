package com.appify.jaedgroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.appify.jaedgroup.fragments.ContactUsFragment;
import com.appify.jaedgroup.fragments.InvestmentFragment;
import com.appify.jaedgroup.fragments.RealEstateFragment;
import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements RealEstateFragment.OnFragmentRealEstateListener {
    private BottomNavigationView bottomNavBar;
    private Fragment fragment;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavBar = findViewById(R.id.bottom_nav_bar);
        manager = getSupportFragmentManager();

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.investment:
                        fragment = new InvestmentFragment();
                        break;
                    case R.id.real_estate:
                        fragment = new RealEstateFragment();
                        break;
                    case R.id.contact_us:
                        fragment = new ContactUsFragment();
                        break;
                }
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                editProfile();
                break;
            case R.id.action_log_out:
                logOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        finish();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void editProfile() {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEstateClick(Estate estate) {
        Intent intent = new Intent(this, ViewEstateActivity.class);
        intent.putExtra(Constants.ESTATE_EXTRA, estate);
        startActivity(intent);
    }
}
