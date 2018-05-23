package com.example.user.lastviewpager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPager_Fragment extends Fragment {
    public static ViewPager_Fragment newInstance(int image) {

        Bundle args = new Bundle();
        args.putInt("image", image);

        ViewPager_Fragment fragment = new ViewPager_Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_item, container, false);

        ImageView imageView = (view).findViewById(R.id.viespager_imageview1);
        imageView.setImageResource(getArguments().getInt("image"));

        return view;
    }
}
