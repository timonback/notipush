package de.timonback.notipush;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import de.timonback.notipush.component.EmptyFragment;
import de.timonback.notipush.component.notification.NotificationFragment;
import de.timonback.notipush.component.preference.PreferenceFragment;
import de.timonback.notipush.util.Utils;
import de.timonback.notipush.util.drawer.DrawerListAdapter;
import de.timonback.notipush.util.drawer.NavItem;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    ArrayList<NavItem> mNavItems = new ArrayList<>();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavItems.add(new NavItem("Home", "Meetup destination", EmptyFragment.class.getName(), R.drawable.chat_message_arrow));
        mNavItems.add(new NavItem("Notifications", "View the notifications", NotificationFragment.class.getName(), R.drawable.chat_message_arrow));
        mNavItems.add(new NavItem("Preferences", "Change your preferences", PreferenceFragment.class.getName(), R.drawable.chat_message_arrow));
        mNavItems.add(new NavItem("About", "Get to know about us", AboutFragment.class.getName(), R.drawable.chat_message_arrow));

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    // Called when invalidateOptionsMenu() is invoked
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    /*
    * Called when a particular item from the navigation drawer
    * is selected.
    * */
    private void selectItemFromDrawer(int position) {
        NavItem item = mNavItems.get(position);

        Fragment fragment = null;
        try {
            fragment = (Fragment) Utils.instanceClass(item.getClassname());

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(item.getTitle());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }
}
