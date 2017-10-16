package com.kelin.bannerdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kelin.banner.BannerEntry;
import com.kelin.banner.view.BannerView;
import com.kelin.recycleradapter.ItemAdapter;
import com.kelin.recycleradapter.MultiTypeAdapter;
import com.kelin.recycleradapter.interfaces.EventBindInterceptor;
import com.kelin.recycleradapter.interfaces.LayoutItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private EventBindInterceptor mEventInterceptor;
    private BannerView.OnBannerEventListener mOnBannerEventListener;

    private static final int[] colors = new int[]{Color.BLACK, Color.GREEN, Color.BLUE, Color.DKGRAY, Color.RED};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        MultiTypeAdapter adapter = new MultiTypeAdapter(recyclerView);
        ItemAdapter<List<ImageBannerEntry>> banner1Adapter = new ItemAdapter<>(BannerHolder.class, getImageBannerEntries());
        ItemAdapter<List<TitleImageBannerEntry>> banner2Adapter = new ItemAdapter<>(BannerHolder2.class, getTitleImageBannerEntries());
        ItemAdapter<List<TitleImageBannerEntry>> banner3Adapter = new ItemAdapter<>(BannerHolder3.class, getTitleImageBannerEntries());
        banner1Adapter.setEventInterceptor(getItemEventInterceptor());
        banner2Adapter.setEventInterceptor(getItemEventInterceptor());
        adapter.addAdapter(banner1Adapter, banner2Adapter, banner3Adapter, new ItemAdapter<>(getStringList(), ItemHolder.class));
        recyclerView.setAdapter(adapter);
    }

    private EventBindInterceptor getItemEventInterceptor() {
        if (mEventInterceptor == null) {
            mEventInterceptor = new EventBindInterceptor() {
                @Override
                public boolean onInterceptor(View view, LayoutItem layoutItem) {
                    if (view.getId() == R.id.vp_view_pager) {
                        BannerView bannerView = (BannerView) view;
                        bannerView.setOnBannerEventListener(getBannerEventListener());
                        return true;
                    }
                    return false;
                }
            };
        }
        return mEventInterceptor;
    }

    @NonNull
    private BannerView.OnBannerEventListener getBannerEventListener() {
        if (mOnBannerEventListener == null) {
            mOnBannerEventListener = new BannerView.OnBannerEventListener() {
                @Override
                protected void onPageClick(BannerEntry entry, int index) {
                    if (entry instanceof ImageBannerEntry) {
                        SubActivity.start(MainActivity.this, ((ImageBannerEntry) entry).getValue());
                    } else if (entry instanceof TitleImageBannerEntry) {
                        SubActivity.start(MainActivity.this, ((TitleImageBannerEntry) entry).getValue());
                    }
                }
            };
        }
        return mOnBannerEventListener;
    }

    @NonNull
    private List<ImageBannerEntry> getImageBannerEntries() {
        List<ImageBannerEntry> items = new ArrayList<>();
        items.add(new ImageBannerEntry("中国新歌声：E神赞藏语Rap", R.drawable.img_banner01));
        items.add(new ImageBannerEntry("中国有嘻哈:热狗公演霸气嗨唱", R.drawable.img_banner02));
        items.add(new ImageBannerEntry("爱笑会议室：三生三世虐恋情缘", R.drawable.img_banner03));
        items.add(new ImageBannerEntry("开心剧乐部：吴京上演战狼故事", R.drawable.img_banner04));
        return items;
    }

    @NonNull
    private List<TitleImageBannerEntry> getTitleImageBannerEntries() {
        List<TitleImageBannerEntry> items2 = new ArrayList<>();
        items2.add(new TitleImageBannerEntry("中国新歌声：E神赞藏语Rap", R.drawable.img_banner01, "我是第一页"));
        items2.add(new TitleImageBannerEntry("中国有嘻哈:热狗公演霸气嗨唱", R.drawable.img_banner02, "我是第二页"));
        items2.add(new TitleImageBannerEntry("爱笑会议室：三生三世虐恋情缘", R.drawable.img_banner03, "我是第三页"));
        items2.add(new TitleImageBannerEntry("开心剧乐部：吴京上演战狼故事", R.drawable.img_banner04, "我是第四页"));
        return items2;
    }

    public List<String> getStringList() {
        List<String> list = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            list.add("我是条目" + i);
        }
        return list;
    }
}
