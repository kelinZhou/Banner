# Banner
###### 基于ViewPger封装的轮播图，支持无限轮播、支持自定义动画、支持自定义时长。高度可自定义UI。
* * *

## 简介
基于ViewPage的的封装，UI样式完全由自己控制。可自定义各种动画。可简单实现各种轮播图效果，支持对轮播图的各种事件监听，例如：点击、长按、页面选中等。
## 效果图
![Banner](materials/gif_banner.gif)

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
    compile 'com.github.kelinZhou:Banner:1.0.0'
}
```

## 使用
#### 使用比较简单，请参考Demo。

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