package com.example.nishant.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.layers.BasicInfoWindow;
import com.mmi.layers.Marker;
import com.mmi.layers.PathOverlay;
import com.mmi.util.GeoPoint;

import java.util.ArrayList;

public class Overspeed extends Fragment {

    MapView mMapView;
    MapmyIndiaMapView  mapMyIndiaMapView;
    Button min_speed,max_speed;

    String speed[] = {"40","40","60","80","70","60"};
    String carspeed[] = {"30","30","90","60","30","60"};
    Button left_arrow,right_arrow;
    Marker moving_icon ;
    Marker m;
    Marker marker,marker1;
    int get =0;

    Dialog dialog;

    public Overspeed() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_overspeed, container, false);

        min_speed = v.findViewById(R.id.min_speed);
        max_speed = v.findViewById(R.id.max_speed);
        right_arrow = v.findViewById(R.id.right_arrow);
        left_arrow = v.findViewById(R.id.left_arrow);

        max_speed.setText(speed[get].toString());
        min_speed.setText(carspeed[get].toString());

        mapMyIndiaMapView = (MapmyIndiaMapView)  v.findViewById(R.id.map);
        mMapView = mapMyIndiaMapView.getMapView();

        GeoPoint geoPoint= new GeoPoint(28.487391089497, 77.091162353754);
        mMapView.setZoom(16);




        final ArrayList geoPoints = new ArrayList<>();

        PathOverlay pathOverlay = new PathOverlay(getActivity());

        GeoPoint geoPoint1= new GeoPoint(28.481306328566, 77.093037217855);
        final GeoPoint geoPoint2= new GeoPoint(28.483205349129,77.09480881691);
        GeoPoint geoPoint3= new GeoPoint(28.483431673325,77.091890573502);
        GeoPoint geoPoint4= new GeoPoint(28.487062224323,77.090238332748);
        GeoPoint geoPoint5= new GeoPoint(28.487486566292,77.090667486191);
        GeoPoint geoPoint6= new GeoPoint(28.487391089497,77.091162353754);
        geoPoints.add(geoPoint1);
        geoPoints.add(geoPoint2);
        geoPoints.add(geoPoint3);
        geoPoints.add(geoPoint4);
        geoPoints.add(geoPoint5);
        geoPoints.add(geoPoint6);

        pathOverlay.setColor(getResources().getColor(R.color.colorPrimaryDark));
        pathOverlay.setWidth(10);
        pathOverlay.setPoints(geoPoints);

        mMapView.getOverlays().add(pathOverlay);
        mMapView.invalidate();

        BasicInfoWindow infoWindow = new BasicInfoWindow(R.layout.tooltip, mMapView);
        infoWindow.setTipColor(getResources().getColor(R.color.colorAccent));
        marker= new Marker(mMapView);
        marker1 = new Marker(mMapView);
        moving_icon = new Marker(mMapView);
        moving_icon.setIcon(getResources().getDrawable(R.drawable.ic_directions_car_black_24dp));

        marker.setPosition(geoPoint1);
        marker1.setPosition((GeoPoint) geoPoints.get(5));
        marker.setInfoWindow(infoWindow);
        marker1.setInfoWindow(infoWindow);
        mMapView.getOverlays().add(marker);
        mMapView.getOverlays().add(marker1);
        mMapView.invalidate();



        dialog = new Dialog(getActivity());

        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (get != 0) {
                    get--;
                    max_speed.setText(speed[get].toString());
                    min_speed.setText(carspeed[get].toString());
                    mMapView.setCenter((GeoPoint) geoPoints.get(get));


                    moving_icon.remove(mMapView);
                    marker= new Marker(mMapView);
                    moving_icon.setPosition((GeoPoint) geoPoints.get(get));
                    mMapView.getOverlays().add(moving_icon);
                    check();

                }

            }
        });

        right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (get <= 4) {
                    get++;
                    max_speed.setText(speed[get].toString());
                    min_speed.setText(carspeed[get].toString());
                    mMapView.setCenter((GeoPoint) geoPoints.get(get));

                    moving_icon.remove(mMapView);
                    marker= new Marker(mMapView);
                    moving_icon.setPosition((GeoPoint) geoPoints.get(get));
                    mMapView.getOverlays().add(moving_icon);
                    check();
                }
            }
        });

        mMapView.setCenter(geoPoint6);
        return v;
    }
    public void check(){

        if(Integer.parseInt(min_speed.getText().toString())>Integer.parseInt(max_speed.getText().toString())){

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Speed Limit Exceed")
                    .setContentText("Please slow down your vehicle. You have exceeded your maximum limit.");

            notificationManager.notify(notificationId, mBuilder.build());
        }
        else{
            dialog.dismiss();
        }
    }
}
