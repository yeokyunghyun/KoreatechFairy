package com.example.koreatechfairy4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DetailNotifyActivity extends AppCompatActivity {

    private WebView webView;
    private ImageButton imgBtn_detail_back;
    private TextView tv_detail_title, tv_detail_author, tv_detail_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_notify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailNotify), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBtn_detail_back = findViewById(R.id.imgBtn_detail_back);

        imgBtn_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailNotifyActivity.this, NotifyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        tv_detail_title = findViewById(R.id.tv_detail_title);
        tv_detail_author = findViewById(R.id.tv_detail_author);
        tv_detail_date = findViewById(R.id.tv_detail_date);

        tv_detail_title.setText(getIntent().getStringExtra("title"));
        tv_detail_author.setText(getIntent().getStringExtra("author"));
        tv_detail_date.setText(getIntent().getStringExtra("date"));

        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        String contentHtml = getIntent().getStringExtra("html");
        String baseUrl = getIntent().getStringExtra("baseUrl");
        ArrayList<String> imgUrls = getIntent().getStringArrayListExtra("imgUrls");

        String fullHtml = contentHtml;

        if (imgUrls != null) {
            StringBuilder imagesHtml = new StringBuilder();
            for (String url : imgUrls) {
                // 각 이미지 URL에 대해 <img> 태그를 생성
                imagesHtml.append("<img src=\"").append(url).append("\" style=\"width:100%;\" />");
            }
            fullHtml = "<html><head></head><body>" + contentHtml + imagesHtml.toString() + "</body></html>";
        }

        webView.loadDataWithBaseURL(baseUrl, fullHtml, "text/html", "utf-8", null);
    }
}