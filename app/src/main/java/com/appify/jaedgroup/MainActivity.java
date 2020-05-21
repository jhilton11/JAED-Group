package com.appify.jaedgroup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.appify.jaedgroup.fragments.ContactUsFragment;
import com.appify.jaedgroup.fragments.InvestmentFragment;
import com.appify.jaedgroup.fragments.RealEstateFragment;
import com.appify.jaedgroup.fragments.ViewTransactionsFragment;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import co.paystack.android.Transaction;

public class MainActivity extends AppCompatActivity implements RealEstateFragment.OnFragmentRealEstateListener,
InvestmentFragment.OnFragmentInteractionListener, ViewTransactionsFragment.OnTransactionClickListener {
    private BottomNavigationView bottomNavBar;
    private Fragment fragment;
    private FragmentManager manager;
    private FragmentTransaction transaction;

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
                        menuItem.setChecked(true);
                        getSupportActionBar().setTitle("Investment");
                        break;
                    case R.id.real_estate:
                        fragment = new RealEstateFragment();
                        menuItem.setCheckable(true);
                        getSupportActionBar().setTitle("Real Estate");
                        break;
                    case R.id.transactions:
                        fragment = new ViewTransactionsFragment();
                        menuItem.setChecked(true);
                        getSupportActionBar().setTitle("Transaction History");
                        break;
                    case R.id.contact_us:
                        menuItem.setChecked(true);
                        fragment = new ContactUsFragment();
                        getSupportActionBar().setTitle("Contact us");
                        break;
                }
                transaction = manager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();
                return true;
            }
        });
        bottomNavBar.setSelectedItemId(R.id.investment);
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    public void onNewInvestmentClick() {
        Intent intent = new Intent(this, NewInvestmentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onViewInvestmentClick() {
        Fragment fragment = new ViewTransactionsFragment();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onTransactionsClick(Transaction transaction) {

    }
}
