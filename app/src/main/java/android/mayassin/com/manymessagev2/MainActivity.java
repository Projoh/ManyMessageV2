package android.mayassin.com.manymessagev2;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.clans.fab.FloatingActionMenu;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity implements DeleteSaveFABInterface {

    private ViewPager viewPager;
    private BottomBar bottomBar;
    private FloatingActionMenu addContactsMenuButton;
    private com.github.clans.fab.FloatingActionButton deleteContactsButton,selectAllContactsButton,
            selectFromContactsButton,selectFromSavedContactsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intialize();
    }

    private void intialize() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        intializeFABs();
        intializeViewPager();
        intializeBottomBar();
    }

    private void intializeFABs() {
        addContactsMenuButton = (FloatingActionMenu) findViewById(R.id.menu_add_contacts);
        deleteContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_delete_contacts);
        selectAllContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_selectall_contacts);
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
                    getSupportActionBar().setTitle("Message Recipients");
                    showFABs();
                } else {
                    hideFABs();
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
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                    viewPager.setCurrentItem(1, true);
                }
                if (tabId == R.id.tab_contacts) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
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
