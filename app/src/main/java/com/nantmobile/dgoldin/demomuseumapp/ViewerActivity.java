package com.nantmobile.dgoldin.demomuseumapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import javax.xml.transform.Result;


public class ViewerActivity extends ActionBarActivity
{
    LinearLayout contentWebviewLayout;
    LinearLayout videoWebviewLayout;
    LinearLayout viewerParentLayout;
    WebView videoWebView;
    WebView contentWebView;
    Bundle bundle;
    String webUrl;
    String videoUrl;
    SeekBar seekBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        contentWebviewLayout = (LinearLayout) findViewById(R.id.contentWebViewLayout);
        videoWebviewLayout = (LinearLayout) findViewById(R.id.videoWebviewLayout);
        viewerParentLayout = (LinearLayout) findViewById(R.id.viewerParentLayout);
        getIntent().getExtras().getString("webUrl");
        bundle = getIntent().getExtras();
        webUrl = bundle.getString("webUrl");
        videoUrl = bundle.getString("videoUrl");
        try
        {
            videoUrl = videoUrl.substring(videoUrl.indexOf("=") + 1);
        }catch(RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                float relativePrograss = (float) ((progress == 0) ? 1 : progress);
                relativePrograss = (float) ((progress == 100) ? 99 : progress);
                //((LinearLayout.LayoutParams) contentWebviewLayout.getLayoutParams()).weight = (float)relativePrograss;
                //((LinearLayout.LayoutParams) videoWebviewLayout.getLayoutParams()).weight = (float) (1-relativePrograss);
                contentWebviewLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, (float)(100-relativePrograss)));
                contentWebviewLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, (float)relativePrograss));
                viewerParentLayout.refreshDrawableState();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        videoWebView = (WebView) findViewById( R.id.video_webview);
        videoWebView.setWebChromeClient(new WebChromeClient());
        videoWebView.getSettings().setJavaScriptEnabled(true);
        videoWebView.setInitialScale(100);
        videoWebView.getSettings().setBuiltInZoomControls(true);



        String playVideo= "<html><body>Youtube video .. <br> <iframe class=\"youtube-player\" type=\"text/html\" width=\"640\" height=\"385\" src=\"http://www.youtube.com/embed/"+videoUrl+"\" frameborder=\"0\"></body></html>";

        videoWebView.loadData(playVideo, "text/html", "utf-8");

        contentWebView = (WebView) findViewById( R.id.content_webview);
        //contentWebView.setWebChromeClient(new WebChromeClient());
        contentWebView.setWebViewClient(new WebViewClient());
        contentWebView.getSettings().setJavaScriptEnabled(true);
        contentWebView.setInitialScale(100);
        contentWebView.getSettings().setBuiltInZoomControls(true);

        contentWebView.loadUrl(webUrl);


    }





}
