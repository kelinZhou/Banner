package com.kelin.bannerdemo.guide;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kelin.banner.BannerEntry;
import com.kelin.banner.SimpleBannerEntry;
import com.kelin.banner.view.BannerView;
import com.kelin.bannerdemo.MainActivity;
import com.kelin.bannerdemo.R;
import com.kelin.transformer.AlphaPageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * **描述:** 利用BannerView实现引导页。
 * <p>
 * **创建人:** kelin
 * <p>
 * **创建时间:** 2019-11-19  14:02
 * <p>
 * **版本:** v 1.0.0
 */
public class GuideActivity extends AppCompatActivity {

    private List<GuidePageEntry> guideEntries = new ArrayList<>(3);

    private View btnExperienceNow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_guide);
        btnExperienceNow = findViewById(R.id.btnExperienceNow);
        guideEntries.add(new GuidePageEntry(R.drawable.guide_p1_img, R.drawable.guide_p1_word));
        guideEntries.add(new GuidePageEntry(R.drawable.guide_p2_img, R.drawable.guide_p2_word));
        guideEntries.add(new GuidePageEntry(R.drawable.guide_p3_img, R.drawable.guide_p3_word));

        btnExperienceNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.start(GuideActivity.this);
                finish();
            }
        });

        BannerView bvGuide = findViewById(R.id.bvGuide);
        bvGuide.setPageTransformer(true, new AlphaPageTransformer());
        bvGuide.setEntries(guideEntries, false);
        bvGuide.setOnPageChangedListener(new BannerView.OnPageChangeListener() {
            @Override
            public void onPageSelected(BannerEntry entry, int index) {
                switch (index) {
                    case 2:
                        btnExperienceNow.setEnabled(true);
                        ViewCompat.animate(btnExperienceNow).alpha(1.0F)
                                .setDuration(500).setListener(new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(View view) {
                                btnExperienceNow.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(View view) {
                                btnExperienceNow.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(View view) {

                            }
                        }).start();
                        btnExperienceNow.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        btnExperienceNow.setEnabled(false);
                        ViewCompat.animate(btnExperienceNow).alpha(0F)
                                .setDuration(500)
                                .setListener(new ViewPropertyAnimatorListener() {
                                    @Override
                                    public void onAnimationStart(View view) {

                                    }

                                    @Override
                                    public void onAnimationEnd(View view) {
                                        btnExperienceNow.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onAnimationCancel(View view) {

                                    }
                                }).start();
                        float alpha = btnExperienceNow.getAlpha();
                        if (alpha != 0f) {
                            btnExperienceNow.setAlpha(0F);
                        }
                        break;
                    default:
                        float a = btnExperienceNow.getAlpha();
                        btnExperienceNow.setVisibility(View.INVISIBLE);
                        if (a != 0f) {
                            btnExperienceNow.setAlpha(0F);
                        }
                }
            }

            @Override
            public void onPageScrolled(int index, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public class GuidePageEntry extends SimpleBannerEntry<String> {
        private int imageRes;
        private int wordsRes;

        /**
         * BannerEntry构造器。
         */
        GuidePageEntry(@DrawableRes int imageRes, @DrawableRes int wordsRes) {
            super("");
            this.imageRes = imageRes;
            this.wordsRes = wordsRes;
        }

        @NonNull
        @Override
        public View onCreateView(ViewGroup parent) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_guide_page, parent, false);
            ((ImageView) rootView.findViewById(R.id.ivImage)).setImageResource(imageRes);
            ((ImageView) rootView.findViewById(R.id.ivWords)).setImageResource(wordsRes);
            return rootView;
        }
    }
}
