package com.example.nishant.myapplication.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nishant.myapplication.MapsActivity;
import com.example.nishant.myapplication.News;
import com.example.nishant.myapplication.Overspeed;
import com.example.nishant.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import io.socket.client.Socket;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Account extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    Polyline polylineFinal;
    LatLng startpoint;
    private Socket mSocket;
    public static DrawerLayout drawer;
    public static Toolbar toolbar = null;
    Boolean f = false;


    public Account() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"RestrictedApi", "MissingPermission"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        Log.d("data",String.valueOf(mapFragment));
        mapFragment.getMapAsync(Account.this);

        return v;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location)
    {
        if (!f) {
            startpoint = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(startpoint));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            loadNearByPlaces(String.valueOf(startpoint.latitude), String.valueOf(startpoint.longitude));
            f = true;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        startpoint = new LatLng(28.610819,77.373954);
//
//        mMap.setMyLocationEnabled(false);
//        mMap.getUiSettings().setCompassEnabled(false);
//        mMap.getUiSettings().setZoomControlsEnabled(false);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(startpoint));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//
//        loadNearByPlaces(String.valueOf(startpoint.latitude),String.valueOf(startpoint.longitude));
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


    }
    private void loadNearByPlaces(String latitude, String longitude) {


        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(1000);
        googlePlacesUrl.append("&types=").append("lodging");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyBnCtz4CuPtcZ-87zXLsYvH1BrkTTJ9eyw");

        Volley.newRequestQueue(getActivity() ).add(new JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                parseLocationResult(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("volleyerror",error.toString());
            }
        }));

    }

    private void parseLocationResult(JSONObject result) {

        String id, place_id, placeName = null, reference, icon, vicinity = null;
        double latitude, longitude;
        mMap.clear();



        LatLng latLng1 = startpoint;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions1.position(latLng1);
        mMap.addMarker(markerOptions1);

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString("status").equalsIgnoreCase("OK")) {


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    id = place.getString("id");
                    place_id = place.getString("place_id");
                    if (!place.isNull("name")) {
                        placeName = place.getString("name");
                    }
                    if (!place.isNull("vicinity")) {
                        vicinity = place.getString("vicinity");
                    }
                    latitude = place.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    longitude = place.getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    reference = place.getString("reference");
                    icon = place.getString("icon");


                    Drawable dr = getResources().getDrawable(R.drawable.mot);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 25, 25, true));
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(d);


                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);
                    markerOptions.position(latLng);
                    markerOptions.icon(markerIcon);
                    markerOptions.title(placeName);
                    mMap.addMarker(markerOptions);


                    final String finalPlaceName = placeName;
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            if (polylineFinal!=null) {
                                polylineFinal.remove();
                            }
                            Double distance = CalculationByDistance(startpoint,marker.getPosition());
                            marker.setTitle(marker.getTitle() +" ( "+String.valueOf(String.valueOf(BigDecimal.valueOf(Double.parseDouble(String.valueOf(distance))).setScale(2, RoundingMode.HALF_UP).doubleValue()))+"Km )");
                            PolylineOptions polylineOptions = new PolylineOptions();

                            polylineOptions.add(startpoint)
                                    .add((marker.getPosition()));

                            polylineFinal = mMap.addPolyline(polylineOptions);

                            return false;
                        }
                    });


                }

            } else if (result.getString("status").equalsIgnoreCase("zero_result")) {
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e("TAG", "parseLocationResult: Error=" + e.getMessage());
        }
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
