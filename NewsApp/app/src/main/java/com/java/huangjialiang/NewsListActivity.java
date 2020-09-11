package com.java.huangjialiang;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.java.huangjialiang.ui.dashboard.DashboardFragment;
import com.java.huangjialiang.ui.home.HomeFragment;
import com.java.huangjialiang.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class NewsListActivity extends AppCompatActivity  {
    private Fragment currentFragment;
    BottomNavigationView navView;
    HomeFragment homeFragment;
    DashboardFragment dashboardFragment;
    NotificationsFragment notificationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        navView = findViewById(R.id.nav_view);
        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationsFragment = new NotificationsFragment();
        //初始化第一个fragment
        switchFragment(homeFragment).commit();

        navView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        switchFragment(homeFragment).commit();
                        break;
                    case R.id.navigation_dashboard:
                        switchFragment(dashboardFragment).commit();
                        break;
                    case R.id.navigation_notifications:
                        switchFragment(notificationsFragment).commit();
                        break;
                }
                return true;
            }
        });
    }

    private FragmentTransaction switchFragment(Fragment targetFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!targetFragment.isAdded()){
            if(currentFragment!=null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.frag_container, targetFragment, "CHANNEL");
        }else{
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }

    @Override
    public void onBackPressed() {
        Log.i("NewsListActivity","Handle BackPressed");
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            Log.i("NewsListActivity","Handle Pop");
        } else {
            super.onBackPressed();
        }
    }
}