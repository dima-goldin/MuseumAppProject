package com.nantmobile.dgoldin.demomuseumapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Handler;


public class LandingPageActivity extends ActionBarActivity
implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    private SQLUtils db;
    private ListView mDrawerListView;
    private DrawerListAdapter mAdapter;
    private ArrayList<Entity> entities;
    
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                                            getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                                               R.id.navigation_drawer,
                                               (DrawerLayout) findViewById(R.id.drawer_layout));


        logMe("onCreate");


        new Thread(){
            @Override
            public void run()
            {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                db = new SQLUtils(getApplicationContext());
            }
        }.run();

        entities = new ArrayList<>();
        mDrawerListView = (ListView) findViewById(R.id.navigation_drawer);
        addDrawerItems();


    }



    private void addDrawerItems() {
        logMe("addDrawerItems");
        mAdapter = new DrawerListAdapter(this ,entities);
        mDrawerListView.setAdapter(mAdapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Entity entity = (Entity) parent.getItemAtPosition(position);
                String name = entity.getName();
                String url_d = entity.getUrl_d();
                String url_v = entity.getUrl_v();

                Toast.makeText(LandingPageActivity.this, name, Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getApplicationContext(), ViewerActivity.class);
                intent.putExtra("webUrl", url_d);
                intent.putExtra("videoUrl", url_v);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                mAdapter.clear();
                entities = db.getAllData();
                mAdapter.addAll(entities);
            }

        };
        thread.run();


    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }
    
    public void onSectionAttached(int number)
    {
        mTitle = "My Art";
//        switch (number)
//        {
//            case 1:
//                mTitle = getString(R.string.title_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
//            case 3:
//                mTitle = getString(R.string.title_section3);
//                break;
//        }
    }
    
    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.landing_page, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        
        public PlaceholderFragment()
        {
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);
            return rootView;
        }
        
        @Override
        public void onAttach(Activity activity)
        {
            super.onAttach(activity);
            ((LandingPageActivity) activity).onSectionAttached(
                                                                      getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    public void gotoRecognizeActivity(View v)
    {
        Intent intent = new Intent(getApplicationContext(),RecognizeActivity.class);
        startActivity(intent);
        v.setFocusableInTouchMode(false);
        v.setFocusable(false);
    }



    private void logMe(String str)
    {
        Log.d("Dima: ", str);
    }

}
