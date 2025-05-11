package com.nad.blurviewdemo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nad.blurview.NadBlurIndicator;
import com.nad.blurview.NadBlurView;
import com.nad.blurview.NadBlurViewRounded;
import com.nad.blurview.config.BlurConfig;

public class MainActivity extends AppCompatActivity {


    private NadBlurViewRounded nadBlurViewRounded;
    private NadBlurIndicator nadBlurIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View decorView = getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();


        nadBlurViewRounded = findViewById(R.id.nadBlurViewRounded);
        nadBlurIndicator = findViewById(R.id.nadBlurIndicator);
        nadBlurViewRounded.attachToRoot(rootView);

        // Konfigurasi global menggunakan builder
        BlurConfig config = new BlurConfig.Builder()
                .setBlurRoot(rootView) // sumber blur (biasanya root)
                .setBlurTarget(rootView) // target yang akan diburamkan
                .setBlurRadius(18f)
                .setOverlayColor(Color.parseColor("#99FFFFFF"))
                .setClipToOutline(true)
                .setFallbackEnabled(true)
                .build();
        nadBlurViewRounded.configure(config);
        nadBlurIndicator.configure(config);

    }
}