package com.java.huangjialiang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {
    Button newsListBtn;
    ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsListBtn = findViewById(R.id.newsListBtn);
        newsListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewsListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        viewFlipper = findViewById(R.id.start_view_filpper);
        viewFlipper.addView(getImageView(R.mipmap.start_1));
        viewFlipper.addView(getImageView(R.mipmap.start_2));
        viewFlipper.addView(getImageView(R.mipmap.start_3));
        // 设置动画
        viewFlipper.setInAnimation(this, R.anim.left_in);
        viewFlipper.setOutAnimation(this, R.anim.left_out);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.startFlipping();
    }

    private ImageView getImageView(int resID){
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resID);
        imageView.setAlpha(0.7f);
        return imageView;
    }
}