package club.ccit.home.ui;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

import club.ccit.basic.BaseViewDataActivity;
import club.ccit.home.databinding.ActivityWebviewBinding;

public class WebViewActivity extends BaseViewDataActivity<ActivityWebviewBinding> {

    @Override
    protected void onCreate() {
        super.onCreate();

        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl(Objects.requireNonNull(getIntent().getStringExtra("url")));
        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

        });
    }
}
