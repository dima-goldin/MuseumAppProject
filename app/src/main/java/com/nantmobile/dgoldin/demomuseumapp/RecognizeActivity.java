package com.nantmobile.dgoldin.demomuseumapp;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import idsdk.api.*;
import idsdk.api.Error;
import idsdk.api.ar.ARPackage;
import idsdk.api.ar.ARView;
import idsdk.api.model.*;
import idsdk.api.model.Entity;
import idsdk.api.recognition.RecognitionResponse;
import idsdk.api.recognition.Recognizer;
import idsdk.api.recognition.Source;


public class RecognizeActivity extends ActionBarActivity
{
    private String CLIENT_ID;
    private String CLIENT_SECRET;

    private static final String TAG = RecognizeActivity.class.getSimpleName();

    SQLUtils db;
    ProgressBar progressBar;
    private Button torch;
    private Button facing;
    private Button example;
    private Button visualFeedback;
    private ARView arView;
    private ObjectAnimator animation;
    private Properties properties;

    Button recognize;
    Button stop;
    TextView title;

    static Engine engine;
    Camera camera;
    private Recognizer recognizer;


    String webUrl;
    String videoUrl;
    String artName;
    Bitmap artPic;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        loadProperties("museumApp.properties");
        //
        db = new SQLUtils(getApplicationContext());

        title = (TextView) findViewById(R.id.TitleTV);
        title.setText(getDescription());

        torch = (Button) findViewById(R.id.TorchBT);
        torch.setOnClickListener(onClickListener);

        facing = (Button) findViewById(R.id.FacingBT);
        facing.setOnClickListener(onClickListener);

        recognize = (Button) findViewById(R.id.RecognizeBT);
        recognize.setOnClickListener(onClickListener);

        stop = (Button) findViewById(R.id.StopRecognitionBT);
        stop.setOnClickListener(onClickListener);

        example = (Button) findViewById(R.id.ExampleBT);
        example.setOnClickListener(onClickListener);

        visualFeedback = (Button) findViewById(R.id.VisualFeedbackBT);
        visualFeedback.setOnClickListener(onClickListener);
        visualFeedback.setVisibility(View.INVISIBLE);

        arView = (ARView) findViewById(R.id.IdARView);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if (engine == null || engine.getContext() == null) {
            new StartEngine().execute();
        }


    }

    private void loadProperties(String fileName) {
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();

        try {
            InputStream inputStream = assetManager.open(fileName);
            properties = new Properties();
            properties.load(inputStream);
            Log.d(TAG,"The properties are now loaded");
            Log.d(TAG, "properties: " + properties);
        } catch (IOException e) {
            Log.d(TAG, "Failed to open microlog property file");
            e.printStackTrace();
        }

        CLIENT_ID = properties.getProperty("CLIENT_ID");
        CLIENT_SECRET = properties.getProperty("CLIENT_SECRET");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recognize, menu);
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


    public void closeActivity(Integer id)
    {
        setResult(id);
        finish();
    }


    public void recognizeImage(View v)
    {
        closeActivity(55);
    }

















    private class StartEngine extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                engine = new Engine(RecognizeActivity.this.getApplicationContext(), CLIENT_ID, CLIENT_SECRET);
            } catch (Engine.EngineException e) {
                showToast(e.getMessage());
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            startCamera();
        }
    }

    private void fetchCreative(idsdk.api.model.Entity entity) {

        progressBar.setVisibility(View.VISIBLE);
        entity.retrieveCreative(new idsdk.api.model.Entity.OnCreativeResponseListener()
        {
            @Override
            public void onPackageResponse(idsdk.api.model.Entity entity, final ARPackage arPackage)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onHtmlResponse(idsdk.api.model.Entity entity, final String url)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        startActivity(browserIntent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                });
            }

            @Override
            public void onError(idsdk.api.model.Entity entity, idsdk.api.Error error)
            {
                Log.e(TAG, "Error: " + error.getCode() + " - " + error.getMessage());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RecognizeActivity.this, getString(R.string.creative_not_available), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, new Entity.Filter());
    }


    String getDescription()
    {
        return "Image Recognizer";
    }

    void onStartRecognizer()
    {
        if (!checkEngine()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        try {
            recognizer = engine.recognize(Recognizer.Type.Image, new Source(camera));
            recognizer.setOnRecognitionListener(new Recognizer.OnRecognitionListener() {
                @Override
                public void onRecognition(Recognizer recognizer, RecognitionResponse response) {
                    List<Recognition> results = response.getResults();
                    if (results.size() == 0) {
                        showToast(getString(R.string.no_match));
                        return;
                    }
                    Entity entity = results.iterator().next().getEntity();
                    Log.i("Dima", response.toJson().toString());
                    int webUrlIndex = entity.getDescription().indexOf("webUrl:");
                    int videoUrlIndex = entity.getDescription().indexOf("videoUrl:");
                    webUrl = entity.getDescription().substring(webUrlIndex+7,videoUrlIndex);
                    videoUrl = entity.getDescription().substring(videoUrlIndex+9);
                    Log.i("Dima", webUrl);
                    Log.i("Dima", videoUrl);
                    String imageString = getImageThumbnail(entity);
                    insertEntityToDB(entity.getTitle(),webUrl,videoUrl,imageString);
                    Intent intent = new Intent(getApplicationContext(), ViewerActivity.class);
                    intent.putExtra("webUrl", webUrl);
                    intent.putExtra("videoUrl", videoUrl);
                    startActivity(intent);
                    return;

//                    entity.retrieveCreative(new Entity.OnCreativeResponseListener() {
//                        @Override
//                        public void onResponse(Entity entity, String json)
//                        {
//                            //TODO Handle the response properly (inserting to DB and opening the ViewerActivity accordingly
//                            JSONObject jsonObject;
//                            super.onResponse(entity, json);
//                            try
//                            {
//                                jsonObject = new JSONObject(json);
//                                webUrl = jsonObject.getString("webUrl");
//                                videoUrl = jsonObject.getString("videoUrl");
//                                artName = jsonObject.getString("name");
//                                byte [] encodeByte=Base64.decode(jsonObject.getString("pic"), Base64.DEFAULT);
//                               artPic = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//
//
//                            } catch (JSONException e)
//                            {
//                                e.printStackTrace();
//                            } catch (Exception e)
//                            {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//                    });
//                    onRecognized(entity);
//                    Log.d(TAG, "entity: " + entity.toJson());
                }

                private String getImageThumbnail(Entity entity)
                {
                    List<String> imageUrls = entity.getImages();
                    String path = imageUrls.get(0);

                    InputStream in =null;
                    Bitmap bmp=null;
                    int responseCode = -1;
                    try{

                        URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setDoInput(true);
                        con.connect();
                        responseCode = con.getResponseCode();
                        if(responseCode == HttpURLConnection.HTTP_OK)
                        {
                            //download
                            in = con.getInputStream();
                            bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(in),125,125);
                            in.close();

                        }

                    }
                    catch(Exception ex){
                        Log.e("Exception",ex.toString());
                    }


                    ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG,100, baos);
                    byte [] b=baos.toByteArray();
                    return Base64.encodeToString(b, Base64.DEFAULT);
                }
            });
            recognizer.setOnRecognitionErrorListener(new Recognizer.OnRecognitionErrorListener() {
                @Override
                public void onRecognitionError(Recognizer recognizer, Error error) {
                    showToast("Error:  " + error.toString());
                    Log.e(TAG, error.toString());
                }
            });
        } catch (Recognizer.RecognizerException e) {
            progressBar.setVisibility(View.INVISIBLE);
            Log.e(TAG, e.getMessage());
        }
    }

    private void insertEntityToDB(final String title,final String webUrl,final String videoUrl,final String pic)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                db.insertData(title, webUrl,videoUrl, pic);
            }
        };
        runnable.run();
    }

    void onStopRecognizer() {
        if (recognizer != null) {
            recognizer.setOnRecognitionErrorListener(null);
            recognizer.setOnRecognitionListener(null);
            recognizer = null;
        }
        progressBar.setVisibility(View.INVISIBLE);
        //stopAugmentedReality();
       //enableAugmentedReality(false);
    }

    public boolean isRecognizerStarted() {
        //return augmentedRealityPlayer != null && augmentedRealityPlayer.isStarted();
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopCamera();

        onStopRecognizer();
    }

    @Override
    public void onBackPressed() {
        if (isRecognizerStarted()) {
            onStopRecognizer();
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void startCamera() {
        if (camera == null && engine != null) {
            try {
                camera = Camera.open(engine);
                camera.startPreview(arView);
                torch.setVisibility(camera.hasTorch() ? View.VISIBLE : View.INVISIBLE);
            } catch (Camera.CameraException e) {
                Toast.makeText(this, getString(R.string.camera_not_opened), Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void stopCamera() {
        if (camera != null && camera.isConnected()) {
            try {
                camera.stopPreview();
                camera.release();
                camera = null;
            } catch (Camera.CameraException e) {
                Toast.makeText(this, getString(R.string.camera_not_opened), Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == torch) {
                onTorchToggle();
            } else if (v == facing) {
                onFacingToggle();
            } else if (v == recognize) {
                onStartRecognizer();
            } else if (v == stop) {
                onStopRecognizer();
            } else if (v == example) {
                onExample();
//            } else if (v == visualFeedback) {
//                //onVisualFeedbackToggle();
            }
        }
    };


    private void onExample() {
        Dialog exampleDialog = new Dialog(RecognizeActivity.this);
        exampleDialog.setTitle(getString(R.string.example));
        //exampleDialog.setContentView(R.layout.example_dialog);
        //TextView exampleLinkTV = (TextView) exampleDialog.findViewById(R.id.ExampleLinkTV);
        //exampleLinkTV.setMovementMethod(LinkMovementMethod.getInstance());
        exampleDialog.show();
    }

    private void onFacingToggle() {
        if (camera != null) {
            camera.setFacing(camera.getFacing() == Camera.Facing.Back ? Camera.Facing.Front : Camera.Facing.Back);
            facing.setSelected(camera.getFacing() == Camera.Facing.Back);
            torch.setVisibility(camera.hasTorch() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void onTorchToggle() {
        if (camera != null && camera.hasTorch()) {
            camera.turnTorch(!camera.isTorchOn());
            torch.setSelected(camera.isTorchOn());
        }
    }
    void onRecognized(final idsdk.api.model.Entity entity) {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
//                progressBar.setVisibility(View.INVISIBLE);
//                final Dialog resultDialog = new Dialog(RecognizeActivity.this);
//                resultDialog.setContentView(R.layout.result_dialog);
//                resultDialog.setTitle(getString(R.string.result));
//                resultDialog.show();
//
//                TextView entityId = (TextView) resultDialog.findViewById(R.id.EntityIdTV);
//                entityId.setText(entity.getId());
//
//                TextView entityTitle = (TextView) resultDialog.findViewById(R.id.EntityNameTV);
//                entityTitle.setText(entity.getTitle());
//
//                Button fetchCreative = (Button) resultDialog.findViewById(R.id.ShowCreativeBT);
//                fetchCreative.setOnClickListener(new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        if (checkEngine())
//                        {
//                            fetchCreative(entity);
//                        }
//                        resultDialog.dismiss();
//                    }
//                });
//                fetchCreative(entity);
            }
        });

        vibrate(this);
    }

    private void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(50);
        }
    }


    boolean checkEngine() {
        if (engine == null) {
            //showToast(getString(R.string.engine_not_initialized));
            showToast("Engine not initialized!");
            return false;
        }
        return true;
    }


    void showToast(final String message) {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressBar.setVisibility(View.INVISIBLE);
                Toast toast = Toast.makeText(RecognizeActivity.this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
