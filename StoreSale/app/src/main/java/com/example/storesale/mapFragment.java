package com.example.storesale;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class mapFragment extends Fragment {
    private GpsTracker gpsTracker;
    TextView tv_location;
    Button btn_myLocation;
    MapView mapView;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    MapPOIItem currLoc;
    ViewGroup mapViewContainer;
    public mapFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = new MapView(this.getContext());
        mapViewContainer = (ViewGroup) i.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        currLoc = new MapPOIItem();

        if (checkLocationServicesStatus()) {
            checkRunTimePermission();
        } else {
            showDialogForLocationServiceSetting();
        }
        tv_location = i.findViewById(R.id.tv_location);
        try {
            startLocationService();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btn_myLocation = i.findViewById(R.id.btn_myLocation);
        btn_myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startLocationService();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return i;
    }
    class Location //위도 경도를 담을 수 있는 클래스
    {
        double latitude;
        double longtitude;

        public Location(double lat, double lgt) {
            this.latitude = lat;
            this.longtitude = lgt;
        }

        public double getLong() {
            return longtitude;
        }

        public double getLat() {
            return latitude;
        }
    }


    public void startLocationService() throws IOException {
        gpsTracker = new GpsTracker(this.getContext());
        double latitude = gpsTracker.getLatitude();
        double longtitude = gpsTracker.getLongitude();
        if (mapView.findPOIItemByTag(0) != null) {
            mapView.removePOIItem(mapView.findPOIItemByTag(0));
        }
        mapView.setZoomLevel(1, true);
        setMarker(currLoc, MapPoint.mapPointWithGeoCoord(latitude, longtitude), 0);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longtitude), true);
        String address = getCurrentAddress(latitude, longtitude); //주소로 변환
        tv_location.setText(address);
        setCvsMaker(latitude,longtitude);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {

                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(getContext(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    this.getActivity().finish();


                } else {

                    Toast.makeText(getContext(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this.getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this.getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setMarker_cvs(Location[] locations,String[] storeName) {
        MapPOIItem marker[] = new MapPOIItem[locations.length];//locations의 개수만큼 마커를 생성
        MapPoint point[] = new MapPoint[locations.length];
        for (int i = 0; i < locations.length; i++) {
            point[i] = MapPoint.mapPointWithGeoCoord(locations[i].getLat(), locations[i].getLong()); //맵포인트 생성
        }
        for (int i = 0; i < locations.length; i++) {

            setMarker(marker[i], point[i], i + 1,storeName[i]);
        }
    }

    private void setMarker(MapPOIItem marker, MapPoint point, int i,String storeName) {
        marker.setItemName(storeName);
        marker.setTag(i);
        marker.setMapPoint(point);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
    }
    private void setMarker(MapPOIItem marker, MapPoint point, int i) {
        marker.setTag(i);
        marker.setMapPoint(point);
        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        mapView.addPOIItem(marker);
    }

    private void setCvsMaker(double x,double y) //서버와 송수신하고, print까지 한다.
    {

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bun = msg.getData();
                String data = bun.getString("HTML_DATA");
                JSONObject json = null;
                JSONArray jsonArray=null;
                String [] storeName=null;
//                String [] storeAddress=null;
//                String [] distance=null;
                Location[] loc=null;
//                mapViewContainer.removeView(mapView);
//                mapViewContainer.addView(mapView);
                try {
                    json = new JSONObject(data);
                    jsonArray = (JSONArray) json.get("documents");
                    loc=new Location[jsonArray.length()]; //위도 경도
                    storeName=new String[jsonArray.length()]; //가게 이름
//                    storeAddress=new String[json.length()];// 주소
//                    for(int i=0;i< jsonArray.length();i++){
//                        JSONObject storeObject = (JSONObject) jsonArray.get(i);
//                        loc[i]= new Location(storeObject.getDouble("y"),storeObject.getDouble("x"));
//                        storeName[i]=storeObject.getString("place_name");
//                    }
//                    setMarker_cvs(loc,storeName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        new Thread() {
            public void run() {
                String result="";
                try {
                    result=connection(x,y);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bundle bun = new Bundle();
                bun.putString("HTML_DATA", result);
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();

    }
    private String connection(double x,double y) throws IOException {
        int findRad = 250;
        String groupCd = "CS2";
        String domain = String.format("https://dapi.kakao.com/v2/local/search/category.json?category_group_code=%s&x=%f&y=%f&radius=%d",groupCd,y,x, findRad);
        URL url = new URL(domain);
        String key = getResources().getString(R.string.Restkey); //API 키
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", String.format("KakaoAK %s", key));
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

}