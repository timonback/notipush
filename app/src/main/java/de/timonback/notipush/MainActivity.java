package de.timonback.notipush;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.timonback.notipush.component.PrefsFragment;
import de.timonback.notipush.component.notification.NotificationFragment;
import de.timonback.notipush.component.preference.SettingsFragment;
import de.timonback.notipush.service.notification.NotificationService;
import de.timonback.notipush.service.notification.NotificationSettings;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_ID = "FRAGMENT_ID";

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView leftNavigationView = (NavigationView) findViewById(R.id.nav_left_view);
        leftNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle Left navigation view item clicks here.
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_home:
                        updateMainContent(new SettingsFragment(), getResources().getString(R.string.nav_home));
                        break;
                    case R.id.nav_chat:
                        String topic = NotificationSettings.getInstance(getApplicationContext()).getCurrentTopic();
                        updateMainContent(new NotificationFragment(), getResources().getString(R.string.nav_chat) + " - " + topic);
                        break;
                    case R.id.nav_preferences:
                        updateMainContent(new PrefsFragment(), getResources().getString(R.string.nav_preferences));
                        break;
                    case R.id.nav_about:
                        updateMainContent(new AboutFragment(), getResources().getString(R.string.nav_about));
                        break;
                    default:
                        Log.w(TAG, "unhandled navigation id");
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        NavigationView rightNavigationView = (NavigationView) findViewById(R.id.nav_right_view);
        rightNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle Right navigation view item clicks here.
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_menu_chat:
                        String topic = item.getTitle().toString();
                        NotificationSettings.getInstance(getApplicationContext()).setCurrentTopic(topic);

                        if (getFragmentManager().findFragmentByTag(FRAGMENT_ID) instanceof NotificationFragment) {
                            String title = getResources().getString(R.string.nav_chat) + " - " + topic;
                            setTitle(title);
                        }
                        break;
                    default:
                        Log.w(TAG, "unhandled navigation id");
                }

                drawer.closeDrawer(GravityCompat.END); /*Important Line*/
                return true;
            }
        });

        NotificationService.getInstance().addChangeListener(new NotificationService.ChangeListener() {
            @Override
            public void update() {
                NavigationView rightNavigationView = (NavigationView) findViewById(R.id.nav_right_view);
                Menu rightNavigationChats = rightNavigationView.getMenu().getItem(0).getSubMenu();
                rightNavigationChats.clear();
                for (String topic : NotificationService.getInstance().getTopics()) {
                    MenuItem item = rightNavigationChats.add(R.id.nav_right_view_chats, R.id.nav_menu_chat, Menu.NONE, topic);
                    item.setIcon(R.drawable.ic_toc);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_openRight) {
            drawer.openDrawer(GravityCompat.END);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMainContent(Fragment fragment, String title) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment, FRAGMENT_ID)
                .commit();

        setTitle(title);
    }
}
