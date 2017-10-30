package com.kelin.bannerdemo;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        MultiTypeAdapter adapter = new MultiTypeAdapter(recyclerView);
        ItemAdapter<List<ImageBannerEntry>> banner1Adapter = new ItemAdapter<>(BannerHolder.class, getImageBannerEntries());
        ItemAdapter<List<TitleImageBannerEntry>> banner2Adapter = new ItemAdapter<>(BannerHolder2.class, getTitleImageBannerEntries());
        ItemAdapter<List<TitleImageBannerEntry>> banner3Adapter = new ItemAdapter<>(BannerHolder3.class, getTitleImageBannerEntry());
        banner1Adapter.setEventInterceptor(getItemEventInterceptor());
        banner2Adapter.setEventInterceptor(getItemEventInterceptor());
        banner3Adapter.setEventInterceptor(getItemEventInterceptor());
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
        items.add(new ImageBannerEntry("大话西游：“炸毛韬”引诱老妖", "更新至50集", "http://m.qiyipic.com/common/lego/20171026/dd116655c96d4a249253167727ed37c8.jpg"));
        items.add(new ImageBannerEntry("天使之路：藏风大片遇高反危机", "10-29期", "http://m.qiyipic.com/common/lego/20171029/c9c3800f35f84f1398b89740f80d8aa6.jpg"));
        items.add(new ImageBannerEntry("星空海2：陆漓设局害惨吴居蓝", "更新至30集", "http://m.qiyipic.com/common/lego/20171023/bd84e15d8dd44d7c9674218de30ac75c.jpg"));
        items.add(new ImageBannerEntry("中国职业脱口秀大赛：狂笑首播", "10-28期", "http://m.qiyipic.com/common/lego/20171028/f1b872de43e649ddbf624b1451ebf95e.jpg"));
        items.add(new ImageBannerEntry("奇秀好音乐，你身边的音乐真人秀", null, "http://pic2.qiyipic.com/common/20171027/cdc6210c26e24f08940d36a5eb918c34.jpg"));
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

    @NonNull
    private List<TitleImageBannerEntry> getTitleImageBannerEntry() {
        List<TitleImageBannerEntry> items2 = new ArrayList<>();
        items2.add(new TitleImageBannerEntry("中国新歌声：E神赞藏语Rap", R.drawable.img_banner01, "我是第一页"));
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
