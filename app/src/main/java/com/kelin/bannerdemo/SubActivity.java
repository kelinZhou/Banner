package com.kelin.bannerdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * 创建人 kelin
 * 创建时间 2017/7/26  下午3:22
 * 版本 v 1.0.0
 */

public class SubActivity extends AppCompatActivity {

    private static final String KEY_CONTENT_VALUE = "content_value";

    public static void start(Context context, String data) {
        Intent intent = new Intent(context, SubActivity.class);
        intent.putExtra(KEY_CONTENT_VALUE, data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        TextView tvContent = (TextView) findViewById(R.id.tv_content);
        tvContent.setText(String.format("收到数据：%s", getIntent().getStringExtra(KEY_CONTENT_VALUE)));
    }
}
