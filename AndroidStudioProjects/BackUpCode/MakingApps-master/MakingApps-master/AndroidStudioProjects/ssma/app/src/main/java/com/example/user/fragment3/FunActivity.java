package com.example.user.fragment3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.fragment3.GoogleMapActivities.googleMapActivity;
import com.example.user.fragment3.MainFragments.CurriculumFragment;
import com.example.user.fragment3.MainFragments.MissionFragment;
import com.example.user.fragment3.MainFragments.ProfessorFragment;
import com.example.user.fragment3.MainFragments.VisionFragment;
import com.example.user.fragment3.NavigationFragments.FirstPeoples;
import com.example.user.fragment3.NavigationFragments.Secondpeoples;
import com.example.user.fragment3.NavigationFragments.developer_information;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FunActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 10;
    private static final int GALLERY_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // 툴바 설정 및 액션바, 프로젝트 이름 제거
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 이메일 버튼 누를 시 Toast 기능 활용
        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FunActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 내비게이션 뷰 내 이메일 주소를 넣어봅시다.
        TextView navigaionview_emailaddress = (TextView)findViewById(R.id.email_address);
        navigaionview_emailaddress.setText("반갑습니다, SSMA입니다");

        // 툴바, 사이드 바 활용 위한 drawerlayout 설정
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.second_drawerlayout);

        // 툴바에 사이드 바 입력하는 단계
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // 사이드 바 동기화
        actionBarDrawerToggle.syncState();

        // 사이드 바에 들어갈 레이아웃 설정
        final NavigationView navigationView = (NavigationView)findViewById(R.id.second_navigationview);

        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FunActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 각 내비게이션 프레그먼트 마다 레이아웃 설정 - addToBackStavk(이름.class.getName() 주의!
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.first_peoples) {
                    Intent first_peoples = new Intent(navigationView.getContext(), FirstPeoples.class);
                    startActivity(first_peoples);
                }

                if (item.getItemId() == R.id.second_peoples) {
                    Intent second_peoples = new Intent(navigationView.getContext(), Secondpeoples.class);
                    startActivity(second_peoples);                }

                if (item.getItemId() == R.id.third_peoples) {
                    Toast.makeText(FunActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fourth_peoples) {
                    Toast.makeText(FunActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fifth_peoples) {
                    Toast.makeText(FunActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fun_activity) {
                    Intent fun_intent = new Intent(navigationView.getContext(), FunActivity.class);
                    startActivity(fun_intent);
                }

                if (item.getItemId() == R.id.developr_information) {
                    Intent developer_intent = new Intent(navigationView.getContext(), developer_information.class);
                    startActivity(developer_intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // 내비게이션 뷰에 있는 이메일 버튼 누를 시 Toast 활성화
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FunActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 하단 부 프레그먼트 생성(둘러싼 레이아웃이 LinearLayout)
        LinearLayout professor_button1 = (LinearLayout)findViewById(R.id.button1);
        LinearLayout vision_button2 = (LinearLayout)findViewById(R.id.button2);
        LinearLayout mission_button3 = (LinearLayout)findViewById(R.id.button3);
        LinearLayout curriculum_button4 = (LinearLayout)findViewById(R.id.button4);
        LinearLayout googlemap_button5 = (LinearLayout)findViewById(R.id.button5);

        // 각 레이아웃 뷰를 누르면 FramgLayout을 각 프레그먼트로 바꿈(애니메이션 효과도 줌)
        professor_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new ProfessorFragment()).addToBackStack(ProfessorFragment.class.getName()).commit();
            }
        });

        vision_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new VisionFragment()).addToBackStack(VisionFragment.class.getName()).commit();
            }
        });

        mission_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new MissionFragment()).addToBackStack(MissionFragment.class.getName()).commit();
            }
        });

        curriculum_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new CurriculumFragment()).addToBackStack(CurriculumFragment.class.getName()).commit();
            }
        });

        googlemap_button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), googleMapActivity.class);
                startActivity(intent);
            }
        });

        // 카메라 기능을 사용하기 위해 권한 불러오기
        requiredPermission();

        // 카메라 버튼 불러오기
        Button camera_button = (Button)findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean camera = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                boolean write_camera = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (camera && write_camera) {
                    // 사진 찍는 메소드
                    takePicture();
                } else {
                    Toast.makeText(FunActivity.this, "카메라 기능 권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 찍은 사진을 저장하는 버튼 호출
        Button save_button = (Button)findViewById(R.id.save_camera);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryAddPic();
            }
        });

        // 사진 불러오는 버튼 호출
        Button get_button = (Button)findViewById(R.id.get_photo);
        get_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickupPicture();
            }
        });
    }

    // 사진 불러오는 메소드
    void PickupPicture() {
        Intent get_intent = new Intent(Intent.ACTION_PICK);
        get_intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        get_intent.setType("image/*");
        startActivityForResult(get_intent, GALLERY_CODE);
    }

    // 사진 저장 기능 메소드
    void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.user.fragment3.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CAMERA_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 이미지 뷰에 사진 보이게 하기

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CODE) {
            ImageView imageView = (ImageView)findViewById(R.id.photo_imageview);
            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ImageView imageview2 = (ImageView)findViewById(R.id.get_photo_imageview);
            imageview2.setImageURI(uri);
        }
    }


    // 권한이 필요한 기능들을 담습니다.

    void requiredPermission() {
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionNeeded = new ArrayList<>();

        for (String permission : permissions) {
            // 승인 거부된 권한이 있을 경우 권한 필요 리스트에 넣어 줍니다.
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                listPermissionNeeded.add(permission);
            }

            // 권한 받지 못한 것들이 있을 경우
            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), 1 );
            }
        }
    }

    // 사진 저장(생성)+ 코드
    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        // 사진 저장 시 아무런 표시가 뜨지 않으므로 Toast 기능을 호출합니다.
        Toast.makeText(this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
