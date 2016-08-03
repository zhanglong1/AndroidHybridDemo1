package com.zlsam.learnhybrid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView mWebView;
    private Activity mActivity;
    private String mUrl;
    private Button mNewPageBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity = this;
        mUrl = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(mUrl)) {
            Toast.makeText(this, "Url is invalid!", Toast.LENGTH_SHORT).show();
            mUrl = "file:///android_asset/page1.html";
        }
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        // Instances
        mNewPageBtn = (Button) findViewById(R.id.btn_new_page);
        mWebView = (WebView) findViewById(R.id.webview);

        // Init button
        mNewPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MainActivity.class);
                intent.putExtra("url", "http://www.cnblogs.com/kangyi/p/4364252.html");
                startActivity(intent);
            }
        });

        // Init WebView
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                mActivity.setProgress(progress * 1000);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(mActivity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        mWebView.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void startActivity(final String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        intent.putExtra("url", url);
                        mActivity.startActivity(intent);
                    }
                });
            }

        }, "Android");
        mWebView.loadUrl(mUrl);
    }
}
