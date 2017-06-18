package me.esca.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;

import me.esca.R;
import me.esca.fragments.CookFragment;
import me.esca.fragments.FavoriteFragment;
import me.esca.fragments.FoodFeedFragment;
import me.esca.fragments.ProfileFragment;
import me.esca.fragments.SearchFragment;
import me.esca.utils.Connectivity;

/**
 * Created by Me on 03/06/2017.
 */

public class FoodFeedActivity extends Activity{

    private BottomNavigationView bottomNavigationView;
    private View networkStatusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_feed_activity);

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_foodfeed_navigation);

        networkStatusBar = findViewById(R.id.network_status_bar);
        if(!Connectivity.isNetworkAvailable(FoodFeedActivity.this))
            networkStatusBar.setVisibility(View.VISIBLE);
        else networkStatusBar.setVisibility(View.INVISIBLE);


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                FoodFeedFragment foodFeedFragment = new FoodFeedFragment();
                                switchToFragment(foodFeedFragment, null);
                                break;

                            case R.id.action_favorite:
                                FavoriteFragment favoriteFragment = new FavoriteFragment();
                                switchToFragment(favoriteFragment, null);
                                break;

                            case R.id.action_cook:
                                CookFragment cookFragment = new CookFragment();
                                switchToFragment(cookFragment, null);
                                break;

                            case R.id.action_search:
                                SearchFragment searchFragment = new SearchFragment();
                                switchToFragment(searchFragment, null);
                                break;

                            case R.id.action_profile:
                                ProfileFragment profileFragment = new ProfileFragment();
                                switchToFragment(profileFragment, null);
                                break;

                        }
                        return true;
                    }
                });

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }
            FoodFeedFragment foodFeedFragment = new FoodFeedFragment();
            foodFeedFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, foodFeedFragment).commit();
        }

    }

    private void switchToFragment(@NonNull Fragment fragment, @Nullable Bundle bundle){

        if(bundle != null){
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationView != null)
            if(bottomNavigationView.getSelectedItemId() != R.id.action_home){
                FoodFeedFragment foodFeedFragment = new FoodFeedFragment();
                switchToFragment(foodFeedFragment, null);
                bottomNavigationView.setSelectedItemId(R.id.action_home);
            }
            else{
                finish();
            }
    }
}
