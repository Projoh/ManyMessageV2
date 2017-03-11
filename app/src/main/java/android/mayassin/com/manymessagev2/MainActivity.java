package android.mayassin.com.manymessagev2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipientsInterface {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 21;
    private static final int REQUEST_FOR_CONTACTS_FROM_CONTACT_BOOK = 1;
    private ViewPager viewPager;
    private BottomBar bottomBar;
    private RecyclerView recyleView;
    private RecycleViewAdapter adapter;
    private FloatingActionMenu addContactsMenuButton;
    private com.github.clans.fab.FloatingActionButton deleteContactsButton,selectAllContactsButton,
            selectFromContactsButton,selectFromSavedContactsButton;
    private ArrayList<Contact> allContacts = new ArrayList<Contact>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            intialize();
        } else {
            requestContactReadPermission();
        }

    }

    private void requestContactReadPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FOR_CONTACTS_FROM_CONTACT_BOOK) {
            if(resultCode == RESULT_OK) {
                // GSON to objects, Populate the recylerview
                Gson gson = new Gson();

                allContacts = gson.fromJson(data.getStringExtra("selected_contacts"), GroupOfContacts.class).contacts;
                sendToRecipeintsFragment();
            }
        }
    }

    private void sendToRecipeintsFragment() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                intialize();
            } else {
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Permission Denied")
                        .positiveColor(getResources().getColor(R.color.colorPrimary))
                        .content("This app is almost useless without contact permission. Please grant it when prompted")
                        .positiveText("OKAY")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                requestContactReadPermission();
                            }
                        })
                        .show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    private void intialize() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        intializeFABs();
        intializeViewPager();
        intializeBottomBar();
        initializeFabListeners();
    }

    private void initializeFabListeners() {
        selectFromContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SelectContactsActivity.class);
                startActivityForResult(i, REQUEST_FOR_CONTACTS_FROM_CONTACT_BOOK);
                addContactsMenuButton.close(true);
            }
        });
    }

    private void intializeFABs() {
        addContactsMenuButton = (FloatingActionMenu) findViewById(R.id.menu_add_contacts);
        deleteContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_delete_contacts);
        selectAllContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_selectall_contacts);
        selectFromContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_recp_contacts);
        selectFromSavedContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_recp_saved);
        deleteContactsButton.hide(false);
        selectAllContactsButton.hide(false);
    }

    private void intializeViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toggleFAB(position);
                if(position == 0) {
                    bottomBar.selectTabAtPosition(0);
                    getSupportActionBar().setTitle("Message Recipients");
                } else {
                    bottomBar.selectTabAtPosition(1);
//                    hideFABs();
                    getSupportActionBar().setTitle("Compose Message");
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void toggleFAB(int position) {
        if(position == 0) {
            addContactsMenuButton.showMenu(true);
        } else {
            addContactsMenuButton.hideMenu(true);
        }
    }

    private void intializeBottomBar() {
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_compose) {
                    viewPager.setCurrentItem(1, true);
                }
                if (tabId == R.id.tab_contacts) {
                    viewPager.setCurrentItem(0, true);
                }
            }
        });
    }

    @Override
    public void showFABs() {
        deleteContactsButton.show(true);
        selectAllContactsButton.show(true);
    }

    @Override
    public void hideFABs() {
        deleteContactsButton.hide(true);
        selectAllContactsButton.hide(true);
    }

    @Override
    public ArrayList<Contact> getSelectedContacts() {
        return allContacts;
    }


    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                default:
                case 0:
                    fragment = new RecipientsFragment();
                    return fragment;
                case 1:
                    fragment = new ComposeFragment();
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                case 0:
                    return "Recipients";
                case 1:
                    return "Compose";
            }
        }
    }

}
