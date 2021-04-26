package com.example.videouploader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import com.example.videouploader.adapter.WelcomeAdapter;

import me.relex.circleindicator.CircleIndicator3;

public class Welcome extends AppCompatActivity {

    private WelcomeAdapter welcomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setUpViewPager();

        getWindow().setStatusBarColor(Color.BLACK);
    }

    private void setUpViewPager() {
        //addDummyQuestions();
        ViewPager2 welcomeViewPager = findViewById(R.id.welcomeViewPager);

        welcomeViewPager.setPadding(50, 80, 50, 80);
        welcomeViewPager.setClipToPadding(false);
        welcomeViewPager.setClipChildren(false);
        welcomeViewPager.setOffscreenPageLimit(3);

        welcomeAdapter = new WelcomeAdapter();
        welcomeViewPager.setAdapter(welcomeAdapter);



        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleX(0.85f + r * 0.15f);
        });

        //Indicator
        CircleIndicator3 viewPagerIndicator = findViewById(R.id.welcomeIndicator);
        viewPagerIndicator.setViewPager(welcomeViewPager);
        // optional
        welcomeAdapter.registerAdapterDataObserver(viewPagerIndicator.getAdapterDataObserver());

        welcomeViewPager.setPageTransformer(compositePageTransformer);
        ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                   new Handler(Looper.myLooper()).postDelayed(() -> showDialog(),2000);
                }
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        };
        welcomeViewPager.registerOnPageChangeCallback(onPageChangeCallback);


    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void showDialog() {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.info_layout);
        TextView textView = d.findViewById(R.id.infoTv);
        Button b = d.findViewById(R.id.dismissButton);
        d.show();
        textView.setText("Welcome to "+getResources().getString(R.string.app_name));
        textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_video_trans),null,null,null);
        b.setText("Glad to be a part of the gang");
        b.setOnClickListener(v->d.dismiss());
        d.setOnDismissListener(dialog -> {
            startActivity(new Intent(Welcome.this,MainActivity.class));
            finish();
        });
    }

}