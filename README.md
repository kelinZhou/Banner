# Banner
[![](https://jitpack.io/v/kelinZhou/Banner.svg)](https://jitpack.io/#kelinZhou/Banner)

###### 基于ViewPager封装的轮播图，支持无限轮播、支持自定义动画、支持自定义时长。随意布局。用来做引导页也很方便哦。
* * *

## 简介
基于ViewPage的的封装，UI样式完全由自己控制。可自定义各种动画。可简单实现各种轮播图效果，支持对轮播图的各种事件监听，例如：点击、长按、页面选中等。

修复了ViewPage配合RecyclerView使用时的两大bug（1.Banner重新出现在屏幕上后第一次轮播没有动画，动画执行一半时上拉再下拉动画会卡在那里）。

配合RecyclerView使用时当BannerView所在的ViewHolder被移除屏幕后轮播会自动停止，重新出现后轮播会自动开始，无需用代码进行任何操作。

## 更新
#### 2.9.1 为SlideShowMoreLayout组件增加setEnable方法及属性，使其可以动态禁用及启用。
```xml
android:enabled="false"
```
```kotlin
ssmShowMore.isEnable = false  //默认为true
```

#### 2.9.0 新增SlideShowMoreLayout组件，使Banner支持左滑加载更多。
布局中使用
```xml
<com.kelin.banner.view.SlideShowMoreLayout
    android:id="@+id/ssmShowMore"
    android:layout_width="match_parent"
    android:layout_height="175dp"
    android:background="#FFF"
    android:orientation="horizontal"
    android:paddingTop="6dp"
    android:paddingBottom="6dp"
    app:arrowIcon="@drawable/ic_test_arrow"
    app:releaseShowMoreText="赶快松手吧"
    app:slideShowMoreText="你再滑一点">

    <com.kelin.banner.view.BannerView
        android:id="@+id/vp_view_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:decelerateMultiple="4"
        app:loopMode="infiniteLoop"
        app:pagingIntervalTime="3000"
        app:singlePageMode="canNotPaging|noIndicator" />
</com.kelin.banner.view.SlideShowMoreLayout>
```
在代码中设置监听。
java
```java
((SlideShowMoreLayout) findViewById(R.id.ssmShowMore)).setOnShowMoreListener(() => {
    Toast.makeText(MainActivity.this, "加载更多", Toast.LENGTH_SHORT).show();
});
```
kotlin
```kotlin
ssmShowMore.setOnShowMoreListener{
    Toast.makeText(MainActivity.this, "加载更多", Toast.LENGTH_SHORT).show()
}
```

#### 2.8.4 对外暴露notifyRefresh()方法，并支持对页面滚动选中事件的单独监听。

#### 2.8.2 SimpleBannerEntry默认实现生命周期方法。
  SimpleBannerEntry默认实现```void onBindData(@NonNull View entryView);```方法和```void unbindData(@NonNull View entryView);```

#### 2.8.0 为BannerEntry增加生命周期方法：
  1. 增加```void onBindData(@NonNull View entryView);```方法，用于处理页面每次由不可见变为可见时的逻辑。
  2. 增加```void unbindData(@NonNull View entryView);```方法，用于处理页面每次需要释放资源的逻辑。
    

#### 2.7.1 迁移至Androidx。
从2.7.1开始全面迁移至Androidx，如果您的项目还没有迁移请使用2.7.0或2.7.0之前的版本。

#### 2.7.0 解决Issues。
1.原点指示器增加空心设置功能。
    您可以在布局文件中使用```app:hollowStyle```属性进行设置。```normal```表示只有未选中的点为空心。
    ```selected```表示只有选中的点为空心。```normal|selected```表示所有点都为空心。您还可以使用```app:strokeSize```属性设置空心时圆环的粗细，不过``app:strokeSize```属性会导致实心圆变大。

2.将BannerEntry中的same方法更名为theSame并在SimpleBannerEntry做了默认实现，如果您觉得实现same方法比较麻烦可以直接继承SimpleBannerEntry但同时你要重写getImageUrl方法将你的url返回。

3.将demo中的转场动画效果转移到库中，从此依赖Banner库之后就可以直接使用了，无需再去demo中拷贝。

#### 2.6.0 修复selectCenterPage之后indicatorView没有被同步设置的bug。
bannerView.selectCenterPage 方法有一个重载可以设置偏移值，这个值的作用是设置在选中第一页之后在向后偏移多少页。本次更新就是修复了在偏移之后indicatorView并没有更新到相应的页面指示的bug。

#### 2.5.9 解决有可能导致清单文件merge出错的问题。
解决有可能导致清单文件merge出错，提示添加tools:replace="android:label"的问题。
    
#### 2.5.6 优化同时展示多页时的交互体验。

#### 2.5.5 缓存机制优化。
修复Banner数据源数量发生变化时Banner刷新后依然会有之前的缓存的问题。

#### 2.5.4 修复数据源变更后调用setEntries不能使BannerView刷新的bug。
真正做到了再调用BannerVeiw.setEntries()方法后如果数据源变更了(根据BannerEntry的same方法)则刷新View否则不刷新View。从而减少不必要的View绘制。

## 体验
[点击下载](https://fir.im/rnam)或扫码下载DemoApk

![DemoApk](materials/apk_download.png)

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
    implementation 'com.github.kelinZhou:Banner:2.8.1'
}
```

## 效果图
#### 引导页效果。
![Guide Page](materials/gif_banner_guide_demo.gif)
#### Banner样式一：标题等信息跟随页面滚动。
![Banner](materials/gif_banner_one_1.gif)
#### Banner样式二：标题固定，圆点指示器。
指示器可以在XML中配置，翻页过程中无需做任何处理。支持配置点的颜色点的大小和选中时点的颜色和大小以及点与点之间的间距。
![Banner](materials/gif_banner_two.gif)
#### Banner样式三：标题固定，数字指示器。
指示器可以在XML中配置，翻页过程中无需做任何处理。支持配置当前页字体颜色总页数字体颜色分隔符以及分割符字体颜色。

![Banner](materials/gif_banner_three.gif)

#### 各种效果。
![Banner](materials/gif_banner_all.gif)

## 使用
#### 数据模型。
Banner中每一页的数据模型都必须是```BannerEntry```接口的子类，以下是接口中的所有方法：

| 方法名称 | 说明 | 返回值 |
|---------|-----|--------|
|```View onCreateView(ViewGroup parent);```|创建当前页面的布局视图。改方法只有第一加载视图进入页面的时候才会调用。也就是说视图有复用机制。|返回创建好的View对象。|
|```CharSequence getTitle();```|获取标题。|返回当前页面的标题内容，也可以返回空，如果你当前页面没有标题的话。|
|```CharSequence getSubTitle();```|获取子标题。|返回当前页面的子标题内容，也可以返回空，如果你当前页面没有子标题的话。|
|```VALUE getValue();```|获取当前页面的数据。该方法为辅助方法，是为了方便开发者调用而提供的，Api本身并没有任何调用。如果你不需要该方法可以空实现。|返回你所需要的任何对象|
|```boolean same(BannerEntry newEntry);```|比较两个模型是否相同（代码中的文档注释中写的比较详细，这里就不多说了。注释比较长，具体可以参考文档注释。）该返回值决定了参数中的对象是否与this中所有需要展示在UI视图上的字段一致，如果一直返回true，否则返回false。该方法也是当配合RecyclerView使用时在bindView方法中不停调用```setEntries```方法而不会导致重复加载数据源的重要依据|该返回值决定了参数中的对象是否与this中所有需要展示在UI视图上的字段一致，如果一直返回true，否则返回false。|
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
        app:singlePageMode="canNotPaging|noIndicator"
        app:bannerIndicator="@+id/biv_indicator"
        app:titleView="@+id/tv_title"
        app:pagingIntervalTime="3000"
        app:decelerateMultiple="4"
        android:background="#FFF"/>

    <LinearLayout android:layout_width="match_parent"
                  android:layout_height="wrap_content"
                  style="@style/style_banner_title_layout">

        <!--用来显示标题的控件-->
        <TextView
            android:id="@+id/tv_title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            style="@style/style_banner_title"
            tools:text="我是标题！"/>

        <!--Banner的圆点型指示器-->
        <com.kelin.banner.view.PointIndicatorView
            android:id="@+id/biv_indicator"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:pointRadius="3dp"
            app:selectedPointRadius="4dp"
            app:pointSpacing="4dp"/>
    </LinearLayout>
</RelativeLayout>
```

#### BannerView的自定义属性
```app:pagingIntervalTime``` 翻页间隔时长,用来配置每次自动翻页之间所间隔的时间，单位为毫秒。例如你想每5秒自动翻页一次，那么该属性应该为5000。

```app:decelerateMultiple``` 翻页动画减速倍数，因为BannerView是继承自ViewPager，所以每次自动翻页所需要的时长都是较短的，你可以通过设置该属性来配置减速倍数，也就是你希望每次自动翻页所需要的时长是ViewPager原时长的多少倍。

```app:bannerIndicator``` 为BannerView指定指示器，只要是实现了Pageable接口的View都可以，该库中所提供的所以指示器都实现了Pageable接口，如果你不满住于我提供的指示器控件，那么你可以自己动手写只要实现Pageable接口就可以配合BannerView使用。例如样例中的该属性的值为：@+id/biv_indicator

```app:titleView``` 为BannerView指定用来显示标题的控件，通过该属性配置标题控件后你就不需要监听BannerView的切换，然后再为标题控件赋值。只要你配置了该属性我会自动为你赋值。前提是你要配置的控件必须是TextView或其子类。例如样例中的该属性的值为：@+id/tv_title

```app:subTitleView``` 为BannerView指定用来显示副标题的控件，通过该属性配置标题控件后你就不需要监听BannerView的切换，然后再为副标题控件赋值。只要你配置了该属性我会自动为你赋值。前提是你要配置的控件必须是TextView或其子类。例如该属性的值为：@+id/tv_sub_title

```app:interpolator``` 翻页动画差值器，可以通过该属性配置自动翻页动画的动画差值器。例如该属性的值为：@android:anim/bounce_interpolator

```app:singlePageMode``` 因为支持无限轮播，那么只有一张图片的时候是否还需要无限轮播？这个属性就是用来配置当Banner中的图片只有一张时的处理方式的。该属性是一个flag属性，一共有以下两个值:

1. ```noIndicator``` 表示如果只有一张图片则没有指示器。也就是说无论你是否设置了指示器，如果只有一张图片的话那么指示器都是不显示的。但是依然是支持无限轮播的。
2. ```canNotPaging``` 表示如果只有一张图片则不可以轮播。但是如你设置了指示器的话，指示器依然会显示。

    *上面两个属性可以同时配置，中间用"|"符号链接，例如上面代码中的配置。这样的话如果只有一张图片则既不会轮播而且无论你是否设置了指示器则都不会显示。*

    *以上所说的只有一张图片是指通过```BannerView```的```setEntries```方法设置数据源时数据源集合的```size()```等于1。所说的设置只是器是指在XML代码中为BannerView配置```app:bannerIndicator```属性或者通过代码```BannerView.setIndicatorView(@NonNull BannerIndicator indicatorView)```为BannerView设置指示器。*

```app:loopMode``` 配置轮播模式，该属性为枚举属性，有以下三个值可以配置：

1. ```infiniteLoop``` 无限循环轮播。
2. ```fromCoverToCover``` 从第一页轮播到最后一页，然后停止轮播。
3. ```fromCoverToCoverLoop``` 从第一页轮播到最后一页，然后再会到第一页后再轮播到最后一页，一直重复。

```app:touchPauseEnable``` 用来配置触摸暂停轮播是否可用，BannerView默认在被触摸时是会暂停自动轮播的，如果你不希望在BannerView被触摸后被暂停自动轮播这可以为该属性赋值为：false

#### PointIndicatorView的自定义属性
```app:totalCount``` 一共有多少个点（也就是总页数），如果是配合BannerView使用的则以BannerView的页数为准。这个属性最大的用途就是在写布局文件时可以及时看到效果，方便调试UI。

```android:gravity``` 设置偏移。只支持以下值的单一配置及合理组合:

Gravity#TOP、Gravity#BOTTOM、View.NO_IDGravity#LEFT、View.NO_IDGravity#RIGHT、View.NO_IDGravity#START、View.NO_IDGravity#END、View.NO_IDGravity#CENTER、View.NO_IDGravity#CENTER_VERTICAL、View.NO_IDGravity#CENTER_HORIZONTAL。

*可以同时配置多个值，多个值之间用"|"(或)符号连接。但是不支持View.NO_IDGravity#FILL、View.NO_IDGravity#FILL_VERTICAL、View.NO_IDGravity#RELATIVE_HORIZONTAL_GRAVITY_MASK、以及View.NO_IDGravity#FILL_HORIZONTAL等类似配置。*

```app:pointSpacing``` 点与点之间的间距，默认为最小的点的直径。

```app:pointRadius``` 点的半径。默认为3dp。

```app:selectedPointRadius``` 选中时点的半径，默认与pointRadius属性的值一直，如果你为pointRadius属性赋值5dp，那么该值的默认值就是5dp。

```app:pointColor``` 点的颜色。默认为25%透明度的白色。

```app:selectedPointColor``` 选中时点的颜色，默认为白色。

```app:strokeSize``` 设置边框的粗细，正常情况下你不需要设置该参数，除非你使用了空心样式(```app:hollowStyle```)。

```app:hollowStyle``` 设置点为空心样式(圆环样式)。你可以将该参数设置为```normal```，表示未被选中的点是空心，选中后为实心。也可以设置为```selected```，表示未被选中的点是实心，选中后为空心。如果你希望所有的点都为空心，那么你可以设置为```normal|selected```，即两个值同时使用，中间用|符号分隔。

#### NumberIndicatorView的自定义属性
```app:totalCount``` 一共有多少个点（也就是总页数），如果是配合BannerView使用的则以BannerView的页数为准。这个属性最大的用途就是在写布局文件时可以及时看到效果，方便调试UI。

```android:gravity``` 设置偏移。只支持以下值的单一配置及合理组合:

```android:textSize``` 字体大小。

```android:textColor``` 字体颜色。

```app:separator``` 分隔符号。例如：/

```app:separatorTextColor``` 分割符文本颜色。

```app:currentPageTextColor``` 当前页码文本颜色。

```app:totalPageTextColor``` 总页码文本颜色。


#### 代码中使用。
```
    //找到BannerView控件。
    BannerView bannerView = findViewById(R.id.vp_view_pager);
    //设置自定义翻页动画改变器，也可以不设置。如果没有设置，则使用ViewPager默认的翻页动画。
    bannerView.setPageTransformer(true, new ScrollPaintingPageTransformer());
    //getData()方法是从网络上获取数据。这里只是伪代码。
    List<TitleImageBannerEntry> bannerEntries = getData();
    //设置数据源并开始轮播。如果不希望启动轮播则调用两个参数的方法：bannerView.setEntries(bannerEntries, false); 你也可以通过bannerView.start();的方式启动轮播。
    bannerView.setEntries(bannerEntries);
```
#### 设置监听。
**页面点击监听**
```
bannerView.setOnPageClickListener(new BannerView.OnPasetOnPageClickListenergeClickListener() {
    @Override
    protected void onPageClick(BannerEntry entry, int index) {
        //某个页面被单击后执行，entry就是这个页面的数据模型。index是页面索引，从0开始。
    }
});
```
**页面长按监听**
```
bannerView.setOnPageLongClickListener(new BannerView.OnPageLongClickListener() {
    @Override
    public void onPageLongClick(BannerEntry entry, int index) {
        //某个页面被长按后执行，entry就是这个页面的数据模型。index是页面索引，从0开始。
    }
});
```
**页面改变监听**
```
bannerView.setOnPageChangedListener(new BannerView.OnPageChangeListener() {
    @Override
    public void onPageSelected(BannerEntry entry, int index) {
        //某个页面被选中后执行，entry就是这个页面的数据模型。index是页面索引，从0开始。
    }

    @Override
    public void onPageScrolled(int index, float positionOffset, int positionOffsetPixels) {
        //页面滑动中执行，这个与ViewPage的回调一致。
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //页面滑动的状态被改变时执行，也是与ViewPager的回调一致。
    }
});
```

* * *
### License
```
Copyright 2016 kelin410@163.com

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