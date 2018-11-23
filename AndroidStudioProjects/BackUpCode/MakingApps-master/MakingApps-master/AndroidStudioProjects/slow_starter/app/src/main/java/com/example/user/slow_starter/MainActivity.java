package com.example.user.slow_starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView view = (RecyclerView)findViewById(R.id.main_recyclerview);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        view.setLayoutManager(layoutManager);

        Adapter adapter = new Adapter();
        view.setAdapter(adapter);
    }
}
