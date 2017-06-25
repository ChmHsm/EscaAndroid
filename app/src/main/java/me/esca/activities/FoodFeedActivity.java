package me.esca.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
        setNetworkStateBarVisibility(false);

        new NetworkRequestState();

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

    public void setNetworkStateBarVisibility(boolean visible){

        TextView connected = (TextView)networkStatusBar.findViewById(R.id.network_state_text_view2);
        TextView disconnected = (TextView)networkStatusBar.findViewById(R.id.network_state_text_view);
        networkStatusBar.setVisibility(View.VISIBLE);
        if(visible) {
            connected.setVisibility(View.VISIBLE);
            disconnected.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    networkStatusBar.setVisibility(View.GONE);
                }
            }, 1000);
        }
        else {
            connected.setVisibility(View.GONE);
            disconnected.setVisibility(View.VISIBLE);
        }
    }

    private class NetworkRequestState {
        public NetworkRequestState() {
            NetworkRequest.Builder requestBuilder = new NetworkRequest.Builder();
            requestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            requestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
            requestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            NetworkRequest networkRequest = requestBuilder.build();
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNetworkStateBarVisibility(true);
                        }
                    });
                }

                @Override
                public void onLosing(Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                }
                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNetworkStateBarVisibility(false);
                        }
                    });

                }
                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }
                @Override
                public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                }
            });
        }
    }

}
