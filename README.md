# Banner
###### 基于ViewPager封装的轮播图，支持无限轮播、支持自定义动画、支持自定义时长。随意布局。
* * *

## 简介
基于ViewPage的的封装，UI样式完全由自己控制。可自定义各种动画。可简单实现各种轮播图效果，支持对轮播图的各种事件监听，例如：点击、长按、页面选中等。

## 下载
###### 第一步：添加 JitPack 仓库到你项目根目录的 gradle 文件中。
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
###### 第二步：添加这个依赖。
```
dependencies {
    compile 'com.github.kelinZhou:Banner:1.1.1'
}
```

## 效果图
#### 样式一：标题等信息跟随页面滚动。
![Banner](materials/gif_banner_one.gif)
#### 样式二：标题固定，圆点指示器。
指示器可以在XML中配置，翻页过程中无需做任何处理。支持配置点的颜色点的大小和选中时点的颜色和大小以及点与点之间的间距。

![Banner](materials/gif_banner_two.gif)
#### 样式三：标题固定，数字指示器。
指示器可以在XML中配置，翻页过程中无需做任何处理。支持配置当前页字体颜色总页数字体颜色分隔符以及分割符字体颜色。

![Banner](materials/gif_banner_three.gif)

## 使用
#### 数据模型。
Banner中每一页的数据模型都必须实现```BannerEntry```接口，以下是接口中的所有方法：

| 方法名称 | 说明 | 返回值 |
|---------|-----|--------|
|```View onCreateView(ViewGroup parent);```|创建当前页面的布局视图。改方法只有第一加载视图进入页面的时候才会调用。也就是说视图有复用机制。|返回创建好的View对象。|
|```CharSequence getTitle();```|获取标题。|返回当前页面的标题内容，也可以返回空，如果你当前页面没有标题的话。|
|```CharSequence getSubTitle();```|获取子标题。|返回当前页面的子标题内容，也可以返回空，如果你当前页面没有子标题的话。|
|```VALUE getValue();```|获取当前页面的数据。改方法为辅助方法，是为了方便使用者调用而提供的，Api本身并没有任何调用。如果你不需要该方法可以空实现。|返回你所需要的任何对象|
#### XML中使用。
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                xmlns:tools="http://schemas.android.com/tools"
                android:layout_width="match_parent"
                android:layout_height="175dp"
                android:orientation="vertical"
                android:paddingBottom="6dp"
                android:paddingTop="6dp">

    <com.kelin.banner.view.BannerView
        android:id="@+id/vp_view_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        <!--当Banner中的图片只有一张时的处理方式-->
        app:singlePageMode="canNotPaging|noIndicator"
        <!--为BannerView指定指示器，只要是BannerIndicator的子类都可以-->
        app:bannerIndicator="@+id/biv_indicator"
        <!--为BannerView指定用来显示标题的控件-->
        app:titleView="@+id/tv_title"
        <!--为BannerView指定用来显示副标题的控件-->
        <!--app:subTitleView=""-->
        <!--为BannerView设置翻页间隔-->
        app:pagingIntervalTime="3000"
        <!--为BannerView设置翻页时长减速倍数（是ViewPager时长的几倍）-->
        app:decelerateMultiple="4"
        <!--为BannerView指定动画差值器-->
        <!--app:interpolator=""-->
        android:background="#FFF"/>

    <LinearLayout android:layout_width="match_parent"
                  android:layout_height="wrap_content"
                  android:layout_alignParentBottom="true"
                  android:background="#8000"
                  android:gravity="center_vertical"
                  android:orientation="horizontal"
                  android:padding="6dp">

        <!--用来显示标题的控件-->
        <TextView
            android:id="@+id/tv_title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:layout_weight="1"
            android:maxWidth="300dp"
            android:paddingBottom="6dp"
            android:paddingLeft="12dp"
            android:paddingRight="12dp"
            android:paddingTop="6dp"
            android:textColor="@android:color/white"
            android:textSize="15sp"
            android:textStyle="bold"
            tools:text="我是标题！"/>

        <!--Banner的圆点型指示器-->
        <com.kelin.banner.view.PointIndicatorView
            android:id="@+id/biv_indicator"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            <!--设置总页数，这个参数设置了也是没有意义的，最总会以BannerView的总页数为准。配置
            自定义属性只是为了能再布局文件中看到效果-->
            app:totalCount="4"
            <!--圆点的半径-->
            app:pointRadius="3dp"
            <!--选中时(也就是当前页)圆点的半径-->
            app:selectedPointRadius="4dp"
            <!--圆点与圆点之间的间距-->
            app:pointSpacing="4dp"
            <!--圆点的颜色-->
            app:pointColor="#5fff"
            <!--选中时(也就是当前页)圆点的颜色-->
            app:selectedPointColor="@android:color/white"/>
    </LinearLayout>
</RelativeLayout>
```
上面的BannerView的自定义属性```app:singlePageMode="canNotPaging|noIndicator"```需要做下说明，因为支持无限轮播，那么只有一张图片的时候是否还需要无限轮播？这个属性就是用来配置当Banner中的图片只有一张时的处理方式的。

###### 该属性是一个flag属性，一共有以下两个值：
1. ```noIndicator``` 表示如果只有一张图片则没有指示器。也就是说无论你是否设置了指示器，如果只有一张图片的话那么指示器都是不显示的。但是依然是支持无限轮播的。
2. ```canNotPaging``` 表示如果只有一张图片则不可以轮播。但是如你设置了指示器的话，指示器依然会显示。

    *上面两个属性可以同时配置，中间用"|"符号链接，例如上面代码中的配置。这样的话如果只有一张图片则既不会轮播而且无论你是否设置了指示器则都不会显示。*

    **以上所说的只有一张图片是指通过```BannerView```的```setEntries```方法设置数据源时数据源集合的```size()```等于1。所说的设置只是器是指在XML代码中为BannerView配置```app:bannerIndicator```属性或者通过代码```BannerView.setIndicatorView(@NonNull BannerIndicator indicatorView)```为BannerView设置指示器。**

#### 代码中使用。
```
    //找到BannerView控件。
    BannerView bannerView = findViewById(R.id.vp_view_pager);
    //设置自定义翻页动画改变器，也可以不设置。如果没有设置，则使用ViewPager默认的翻页动画。
    bannerView.setPageTransformer(true, new DepthPageTransformer());
    //getData()方法是从网络上获取数据。这里只是伪代码。
    List<TitleImageBannerEntry> bannerEntries = getData();
    //设置数据源
    bannerView.setEntries(bannerEntries);
    //启动轮播。
    bannerView.start();
```
#### 设置监听。
```
bannerView.setOnBannerEventListener(new BannerView.OnBannerEventListener() {
        /**
         * 页面被点击的时候执行。
         *
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        @Override
        protected void onPageClick(BannerEntry entry, int index) {
            //处理页面被点击时的逻辑。
        }

        /**
         * 页面被长按的时候执行。
         *
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        @Override
        protected void onPageLongClick(BannerEntry entry, int index) {
            super.onPageLongClick(entry, index);
            //该方法是非抽象方法，不需要关心可以不实现。
        }

        /**
         * 当页面被选中的时候调用。
         *
         * @param entry 当前页面的 {@link BannerEntry} 对象。
         * @param index 当前页面的索引。这个索引永远会在你的集合的size范围内。
         */
        @Override
        protected void onPageSelected(BannerEntry entry, int index) {
            super.onPageSelected(entry, index);
            //该方法是非抽象方法，不需要关心可以不实现。
        }

        /**
         * 当页面正在滚动中的时候执行。
         *
         * @param index                当前页面的索引。这个索引永远会在你的集合的size范围内。
         * @param positionOffset       值为(0,1)表示页面位置的偏移。
         * @param positionOffsetPixels 页面偏移的像素值。
         */
        @Override
        protected void onPageScrolled(int index, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(index, positionOffset, positionOffsetPixels);
            //该方法是非抽象方法，不需要关心可以不实现。
        }

        /**
         * 当Banner中的页面的滚动状态改变的时候被执行。
         *
         * @param state 当前的滚动状态。
         * @see BannerView#SCROLL_STATE_IDLE
         * @see BannerView#SCROLL_STATE_DRAGGING
         * @see BannerView#SCROLL_STATE_SETTLING
         */
        @Override
        protected void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            //该方法是非抽象方法，不需要关心可以不实现。
        }
});
```

* * *
### License
```
Copyright 2016 Francisco José Montiel Navarro

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```