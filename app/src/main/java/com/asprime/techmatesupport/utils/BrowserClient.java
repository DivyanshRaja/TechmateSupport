package com.asprime.techmatesupport.utils;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
