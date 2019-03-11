package com.example.nishant.myapplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nishant.myapplication.Fragment.Account;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MapsActivity extends FragmentActivity  {

    private GoogleMap mMap;

    Polyline polylineFinal;
    LatLng startpoint;
    private Socket mSocket;
    public static DrawerLayout drawer;
    public static Toolbar toolbar = null;

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;


    Intent mServiceIntent;
    private UploadingService mSensorService;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        mSensorService = new UploadingService(MapsActivity.this);
        mServiceIntent = new Intent(MapsActivity.this, mSensorService.getClass());


        if (!isMyServiceRunning(mSensorService.getClass())) {
            Log.d("enterign","entering");
            startService(mServiceIntent);
        }



        frameLayout = findViewById(R.id.frag_container);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
//        bottomNavigationView.getMenu().getItem(0).setCheckable(true);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        Account frgs = new Account();
        FragmentTransaction fragmentTransactions =
                getSupportFragmentManager().beginTransaction();
        fragmentTransactions.replace(R.id.frag_container, frgs);
        fragmentTransactions.commit();
        bottomNavigationView.getMenu().getItem(1).setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @SuppressLint("RestrictedApi")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.motels:

                                Account frgs = new Account();
                                FragmentTransaction fragmentTransactions =
                                        getSupportFragmentManager().beginTransaction();
                                fragmentTransactions.replace(R.id.frag_container, frgs);
                                fragmentTransactions.commit();
                                bottomNavigationView.getMenu().getItem(1).setChecked(true);


                                return true;
                            case R.id.overspeed:

                                Overspeed frgs1 = new Overspeed();
                                FragmentTransaction fragmentTransactions1 =
                                        getSupportFragmentManager().beginTransaction();
                                fragmentTransactions1.replace(R.id.frag_container, frgs1);
                                fragmentTransactions1.commit();
                                bottomNavigationView.getMenu().getItem(2).setChecked(true);


                                return true;
                            case R.id.news:

                                News frgs2 = new News();
                                FragmentTransaction fragmentTransactions2 =
                                        getSupportFragmentManager().beginTransaction();
                                fragmentTransactions2.replace(R.id.frag_container, frgs2);
                                fragmentTransactions2.commit();
                                bottomNavigationView.getMenu().getItem(0).setChecked(true);

                                return true;

                        }
                        return false;
                    }
                });

    }


static class BottomNavigationViewHelper {
    @SuppressLint("RestrictedApi")
    static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");

            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);

                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }

}

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }




}
