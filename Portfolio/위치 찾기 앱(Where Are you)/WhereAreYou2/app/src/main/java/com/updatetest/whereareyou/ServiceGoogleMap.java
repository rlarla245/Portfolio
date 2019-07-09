package com.updatetest.whereareyou;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.updatetest.whereareyou.Models.LocationModel;

public class ServiceGoogleMap extends Service {
    // 위도, 경도
    // 자료형이 더블이라는 걸 명심합니다.
    Double latitude = 0.0;
    Double longitude = 0.0;

    // 내 uid
    String myUid;

    // 위치 입력하기 위한 변수 설정
    LocationManager locationManager;

    // 다시 시작될 때
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 내 uid 불러오기
        try {
            // 내 uid 불러 오기
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println("service uid 확인: " + myUid);
            System.out.println("service 확인: onStartCommand 접근 완료");

            // loca 매니저 설정
            locationManager = (LocationManager) ServiceGoogleMap.this.getSystemService(Context.LOCATION_SERVICE);

            // gps 확인
            onGPSCheck();

            // 스레드 호출 및 실행
            LocationThread thread = new LocationThread();
            thread.start();

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                System.out.println("확인: 반복문");

                                // 5초씩 끊습니다.
                                // 강제로 sleep 메소드 사용
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    System.out.println("service 에러: " + e.getMessage());
                                    e.printStackTrace();
                                }

                                // locationManager 설정
                                locationManager = (LocationManager) ServiceGoogleMap.this.getSystemService(Context.LOCATION_SERVICE);

                                // 허가 확인을 하지 않으면 해당 메소드들을 사용할 수 없습니다.
                                if (ContextCompat.checkSelfPermission(ServiceGoogleMap.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(ServiceGoogleMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    // 어떤 방식으로 위치를 불러올지, 시간 간격, 몇 미터마다 갱신할 것인지, 리스너를 파라미터로 받습니다,
                                    // 어디에 연결되었니? GPS || NETWORK
                                    // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100f, this);
                                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        // 최근 위치가 없을 경우
                                        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
                                            System.out.println("service 확인: 최근 위치 없음");
                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ServiceGoogleMap.this);
                                            try {
                                                // onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                                            } catch (NullPointerException e) {

                                            }
                                        }

                                        // 최근 위치가 있을 경우
                                        else {
                                            // 위치 변경 시 호출
                                            System.out.println("service 확인 최근 위치: " + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ServiceGoogleMap.this);
                                            try {
                                                // (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                                            } catch (NullPointerException e) {

                                            }
                                        }
                                    }

                                    // 네트워크 방식을 통한 위치 호출은 정확도가 급격히 떨어진다는 단점이 존재합니다.
                                    else {
                                        if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null) {
                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ServiceGoogleMap.this);
                                            try {
                                                // onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                                            } catch (NullPointerException e) {

                                            }
                                        } else {
                                            // 위치 변경 시 호출
                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ServiceGoogleMap.this);
                                            try {
                                                // onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                                            } catch (NullPointerException e) {

                                            }
                                            System.out.println("service 확인 기지국 위치: " + GoogleMapActivity.recentLocation);
                                        }
                                    }
                                }

                                // 권한이 없을 경우 -> 사실상 불가할텐데?
                                else {
                                    Toast.makeText(ServiceGoogleMap.this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                                }

                                // 앱이 포그라운드 상태에 있을 때
                                if (!GoogleMapActivity.isAppIsInBackground(getApplicationContext())) {
                                    System.out.println("ServiceGoogleMap 확인: 지도 프레그먼트 포그라운드 -> 서비스 종료");
                                    break;
                                }

                                if (!LoginActivity.isAppIsInBackground(getApplicationContext())) {
                                    System.out.println("ServiceGoogleMap 확인: 로그인 액티비티 포그라운드 -> 서비스 종료");
                                    break;
                                }
                            }
                        }
                    });
                }
            })
                    // 스레드 시작
                    .start();
            */

            // 서비스 종료를 막습니다.
            return START_STICKY;
        }

        catch (NullPointerException e) {
            Toast.makeText(this, "계정 오류 발생. 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
            // uid가 null인 경우
            // 서비스가 오류날 경우 바로 종료하도록
            return START_STICKY;
        }
    }

    // gps on/off 확인
    public void onGPSCheck() {
        // GPS가 켜져있는지 체크
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }
    }

    // 서비스 제거
    @Override
    public void onDestroy() {
        super.onDestroy();

        // System.out.println("ServiceGoogleMap 확인: 서비스가 종료됩니다.");
        // thread.stop();
        // thread = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Service와 Activity 사이에서
        // 통신(데이터를 주고받을)할 때 사용하는 메소드
        // 필요가 없으면 return null;
        return null;
    }

    // 스레드 설정
    private class LocationThread extends Thread implements LocationListener {
        private Handler handler = null;

        // 생성자를 통해 locationManager를 호출합니다.
        public LocationThread() {
            // locationManager 설정
            locationManager = (LocationManager) ServiceGoogleMap.this.getSystemService(Context.LOCATION_SERVICE);
        }

        @Override
        public void run() {
            super.run();
            // why?
            Looper.prepare();
            handler = new Handler();

            // 위치 요청
            try {
                // gps 제공자
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // gps 위치 요청
                    System.out.println("ServiceGoogleMap 확인: gps 위치를 불러옵니다.");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }

                // 기지국 제공자
                else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // 기지국 위치 요청
                    System.out.println("ServiceGoogleMap 확인: 기지국 위치를 불러옵니다.");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                }

                // 그 외. 위치 제공 불가
                else {
                    Toast.makeText(ServiceGoogleMap.this, "위치를 요청할 수 없습니다. 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                    System.out.println("ServiceGoogleMap 확인: 위치를 요청할 수 없습니다.");
                }
            }

            // 권한 에러
            catch (SecurityException e) {
                //
                Toast.makeText(ServiceGoogleMap.this, "허가를 받지 못해 위치값을 받아오지 못합니다.", Toast.LENGTH_SHORT).show();
            }

            // 반복?
            Looper.loop();
        }

        @Override
        public void onLocationChanged(final Location location) {
            System.out.println("ServiceGoogleMap 확인: onLocationChanged 메소드 접근 완료");

            // 현재 위도와 경도 값을 받아옵니다.
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // 데이터 모델에 맞게 포맷합니다.
            final LocationModel locationModel = new LocationModel();
            locationModel.latitude = latitude;
            locationModel.longitude = longitude;
            locationModel.uid = myUid;
            locationModel.timestamp = ServerValue.TIMESTAMP;

            // 위치 데이터를 입력합니다.
            FirebaseDatabase.getInstance().getReference().child("locations").child(myUid)
                    .setValue(locationModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // 위치를 찍었으니 변동 사항이 생기면 다시 찍어야 합니다.
                            LocationThread locationThread = new LocationThread();
                            locationThread.start();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ServiceGoogleMap.this, "위치 입력에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
            // 중지?
            handler.getLooper().quit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    // 위치 변경 시
    /*
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("service 확인: onLocationChangerd 메소드 접근 완료");

        // 현재 위도와 경도 값을 받아옵니다.
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // 데이터 모델에 맞게 포맷합니다.
        final LocationModel locationModel = new LocationModel();
        locationModel.latitude = latitude;
        locationModel.longitude = longitude;
        locationModel.uid = myUid;
        locationModel.timestamp = ServerValue.TIMESTAMP;

        // 위치 데이터를 입력합니다.
        FirebaseDatabase.getInstance().getReference().child("locations").child(myUid)
                .setValue(locationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ServiceGoogleMap.this, "위치 입력에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    */
}
