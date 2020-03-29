package com.example.theauraa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    ProgressDialog dialog;
    RelativeLayout relativeLayout;
    Button noInternetRetryBtn;
    SwipeRefreshLayout swipeRefreshLayout;


    String webUrl = "https://theauraa.com";

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.myWebView);
        progressBar = (ProgressBar) findViewById(R.id.myWebProgressBar);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        relativeLayout = (RelativeLayout) findViewById(R.id.noInternetRelativeLayout);
        noInternetRetryBtn = (Button) findViewById(R.id.noInternetRetryBtn);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);

        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);

        //set swipe listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        //set retry onclick listener
        noInternetRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //return super.shouldOverrideUrlLoading(view, request);
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }
        });


        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                //make progress bar visible and set the newProgress to the progressBar Progress
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                dialog.show();
                //setTitle("Loading....");


                //on 100% complete hide progressBar
                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                    //setTitle(R.string.app_name);
                    dialog.dismiss();
                }

                super.onProgressChanged(view, newProgress);
            }


        });

        webView.getSettings().setJavaScriptEnabled(true);
        //check for internet connection
        checkConnection();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Are You Want to Exit This App ?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    }).show();

            super.onBackPressed();
        }
    }

    public void checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(wifi.isConnected()){
            loadUrl();
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);


        }else if(mobileNetwork.isConnected()){
            loadUrl();
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

        }else{
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }

    }

    public void loadUrl(){

        //load url to the web view
        webView.loadUrl(webUrl);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.previousToolBarMenuItem:
                onBackPressed();
                break;
            case R.id.refreshToolBarMenuItem:
                checkConnection();
                break;
            case R.id.nextToolBarMenuItem:
                if(webView.canGoForward()){
                    webView.goForward();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
