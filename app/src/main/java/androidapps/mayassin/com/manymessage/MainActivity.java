package androidapps.mayassin.com.manymessage;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements RecipientsInterface, ComposeInterface{

    private static final String UPDATE_CONTACTS_REQUEST = "updateContacts";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 21;
    private static final int MY_PERMISSIONS_SEND_SMS = 22;
    private static final int REQUEST_FOR_CONTACTS_FROM_CONTACT_BOOK = 1;
    private static final String REMOVE_CONTACTS_REQUEST = "removeSelectedContacts";
    private static final String SELECT_ALL_CONTACTS = "selectAllContacts";
    private static final String FILTER_CONTACTS = "filterContacts";

    private static final String SEND_MESSAGE = "sendMessage";
    private static final String DELETE_MESSAGE = "deleteMessage";
    private static final String CACHED_MESSAGE = "cachedMessage";
    private static final String SAVE_MESSAGE = "saveMessage";

    private ViewPager viewPager;
    private View parentView;
    private BottomBar bottomBar;
    private RecyclerView recyleView;
    private SessionManager sessionManager;
    private RecycleViewAdapter adapter;
    private Menu menu;
    private FloatingActionMenu addContactsMenuButton;
    private com.github.clans.fab.FloatingActionButton deleteContactsButton,selectAllContactsButton,
            selectFromContactsButton,selectFromSavedContactsButton,saveCurrentContactsButton,
            composeSendButton,composeDeleteButton,composeSaveButton,composeCachedButton;
    private ArrayList<Contact> allContacts = new ArrayList<Contact>();
    private CustomMessage customMessage = new CustomMessage();

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
    private void requestSENDSMSPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_SEND_SMS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FOR_CONTACTS_FROM_CONTACT_BOOK) {
            if(resultCode == RESULT_OK) {
                // GSON to objects, Populate the recylerview
                Gson gson = new Gson();
                if(allContacts.isEmpty())
                    allContacts.addAll(gson.fromJson(data.getStringExtra("selected_contacts"), GroupOfContacts.class).contacts);
                else {
                    addNewContacts(gson.fromJson(data.getStringExtra("selected_contacts"), GroupOfContacts.class).contacts);
                }
                updateContactsRecylerView();
            }
        }
    }

    private void addNewContacts(ArrayList<Contact> newContactsToAdd) {
        for(Contact newContact: newContactsToAdd) {
            boolean add = true;
            for(Contact currentcontact : allContacts) {
                if(currentcontact.firstName.equals(newContact.firstName) &&
                        currentcontact.lastName.equals(newContact.lastName) &&
                        currentcontact.phoneNumber.equals(newContact.phoneNumber)) {
                    add = false;
                    break;
                }
            }
            if(add) allContacts.add(newContact);
        }
    }


    private void updateContactsRecylerView() {
        Intent i = new Intent(UPDATE_CONTACTS_REQUEST);
        i.putExtra("success", true);
        LocalBroadcastManager.getInstance(MainActivity.this)
                .sendBroadcast(i);
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
        if(requestCode == MY_PERMISSIONS_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                sendTextMessage();
            } else {
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("Permission Denied")
                        .positiveColor(getResources().getColor(R.color.colorPrimary))
                        .content("This app cannot send text messages without the SMS permission. Please grant it when prompted")
                        .positiveText("OKAY")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                requestSENDSMSPermission();
                            }
                        })
                        .show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                hideFABs();
                Intent i = new Intent(FILTER_CONTACTS);
                i.putExtra("success", true);
                i.putExtra("filter_text", newText);
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(i);
                return false;
            }
        });
        return true;
    }


    private void intialize() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        sessionManager = new SessionManager(this);
        intializeFABs();
        intializeViewPager();
        intializeBottomBar();
        setRecipFABClickListeners();
        setComposeFABClickListeners();

    }

    private void setComposeFABClickListeners() {
        composeCachedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CACHED_MESSAGE);
                i.putExtra("success", true);
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(i);
            }
        });
        composeSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SAVE_MESSAGE);
                i.putExtra("success", true);
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(i);
            }
        });
        composeSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SEND_MESSAGE);
                i.putExtra("success", true);
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(i);
            }
        });
        composeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DELETE_MESSAGE);
                i.putExtra("success", true);
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(i);
            }
        });
    }

    private void setRecipFABClickListeners() {
        selectFromContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactsMenuButton.close(true);
                Intent i = new Intent(MainActivity.this, SelectContactsActivity.class);
                startActivityForResult(i, REQUEST_FOR_CONTACTS_FROM_CONTACT_BOOK);
            }
        });
        deleteContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title("Remove Selected Contacts?")
                        .content("You are about to remove all the selected contacts from the message recipients." +
                                " You will have to add them back manually if you want them back.")
                        .positiveText("REMOVE")
                        .negativeText("NEVERMIND")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent i = new Intent(REMOVE_CONTACTS_REQUEST);
                                i.putExtra("success", true);
                                LocalBroadcastManager.getInstance(MainActivity.this)
                                        .sendBroadcast(i);
                            }
                        })
                        .show();
            }
        });
        selectAllContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SELECT_ALL_CONTACTS);
                i.putExtra("success", true);
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(i);
            }
        });

        saveCurrentContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title("Save the current Recipients into a group?")
                        .content("Are you sure you want to save the current contacts into a group for later use?")
                        .positiveText("YES")
                        .negativeText("NEVERMIND")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new MaterialDialog.Builder(MainActivity.this)
                                        .title("Group name?")
                                        .content("What do you want to name this group of Contacts?")
                                        .inputType(InputType.TYPE_CLASS_TEXT)
                                        .input("Group Name", "", new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                String groupName = input.toString().replace("\n", "");
                                                sessionManager.saveGroup(groupName, new GroupOfContacts(allContacts));
                                            }
                                        }).show();
                            }
                        })
                        .show();
            }
        });

        selectFromSavedContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactsMenuButton.close(true);
                final String[] allGroups = sessionManager.getAllGroups();
                if(allGroups.length == 0 || allGroups[0].isEmpty()) {
                    // Snackbar saying no saved groups, explain how to save
                    showSnackBar(MainActivity.this, "You have no saved groups! Create a group and save it using the save button.");

                    return;
                }
                new MaterialDialog.Builder(MainActivity.this)
                        .title("Select from saved Groups")
                        .items(allGroups)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                final ArrayList<Contact> tempContacts = sessionManager.getGroup(allGroups[which]);
                                final String groupName = allGroups[which];
                                dialog.dismiss();
                                new MaterialDialog.Builder(MainActivity.this)
                                        .title(groupName)
                                        .items(getCurrentContactNames(tempContacts))
                                        .positiveText("OKAY")
                                        .negativeText("NEVERMIND")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                addNewContacts(tempContacts);
                                                updateContactsRecylerView();
                                                dialog.dismiss();
                                            }
                                        })
                                        .neutralText("DELETE")
                                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                new MaterialDialog.Builder(MainActivity.this)
                                                        .title("DELETE GROUP " + groupName +"?")
                                                        .content("Are you sure you want to delete the group " + groupName +", this action cannot be undone.")
                                                        .positiveText("YES")
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                sessionManager.deleteGroup(groupName);
                                                            }
                                                        })
                                                        .negativeText("NEVERMIND")
                                                        .show();
                                                dialog.dismiss();
                                            }
                                        })
                                        .autoDismiss(false)
                                        .show();
                            }
                        })
                        .show();
            }
        });
    }
    private ArrayList<String> getCurrentContactNames(ArrayList<Contact> allContacts) {
        ArrayList<String> currentContactNames = new ArrayList<>();
        for(Contact contact : allContacts) {
            currentContactNames.add(contact.firstName +" "+ contact.lastName + "("+contact.phoneNumber+")");
        }
        return currentContactNames;
    }

    private void intializeFABs() {
        addContactsMenuButton = (FloatingActionMenu) findViewById(R.id.menu_add_contacts);
        deleteContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_delete_contacts);
        selectAllContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_selectall_contacts);
        selectFromContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_recp_contacts);
        selectFromSavedContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_recp_saved);
        saveCurrentContactsButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_save_contacts);
        composeDeleteButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_delete_message);
        composeSendButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_send_message);
        composeSaveButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_save_message);
        composeCachedButton = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_cached_message);
        deleteContactsButton.hide(false);
        selectAllContactsButton.hide(false);
        saveCurrentContactsButton.hide(false);
        composeSaveButton.hide(false);
        composeDeleteButton.hide(false);
        composeSendButton.hide(false);
        composeCachedButton.hide(false);
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
                    MenuItem searchItem = menu.findItem(R.id.search);
                    searchItem.setVisible(true);
                    hideComposeFAB();
                } else {
                    bottomBar.selectTabAtPosition(1);
//                    hideFABs();
                    getSupportActionBar().setTitle("Compose Message");
                    MenuItem searchItem = menu.findItem(R.id.search);
                    searchItem.collapseActionView();
                    searchItem.setVisible(false);
                    showComposeFAB();
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
            checkFABsAndShow();
        } else {
            addContactsMenuButton.hideMenu(false);
            hideFABs();
        }
    }

    private void checkFABsAndShow() {
        if(!allContacts.isEmpty()) {
            showFABs();
        } else {
            hideFABs();
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
        saveCurrentContactsButton.show(true);
    }

    @Override
    public void hideFABs() {
        deleteContactsButton.hide(true);
        saveCurrentContactsButton.hide(true);
        selectAllContactsButton.hide(true);
        addContactsMenuButton.close(true);
    }

    @Override
    public ArrayList<Contact> getSelectedContacts() {
        return allContacts;
    }

    @Override
    public void showComposeFAB() {
        composeDeleteButton.show(true);
        composeSaveButton.show(true);
        composeSendButton.show(true);
        composeCachedButton.show(true);
    }

    @Override
    public void hideComposeFAB() {
        composeSaveButton.hide(false);
        composeDeleteButton.hide(false);
        composeSendButton.hide(false);
        composeCachedButton.hide(false);
    }

    @Override
    public void sendDataFromCompose(final CustomMessage customMessage) {
        this.customMessage = customMessage;
        if(allContacts.isEmpty()) {
            showSnackBar(this, "No contacts selected!");
            return;
        }
        new MaterialDialog.Builder(this)
                .title("Are you sure you wish to send this message to " + allContacts.size() + " contacts?")
                .content("This action cannot be undone.")
                .positiveText("YES")
                .negativeText("NEVERMIND")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Random random = new Random();
                        Contact randomContact = allContacts.get((int)(Math.random()*allContacts.size()));
                        String previewMessage = customMessage.getMessage().replaceAll("fname", randomContact.firstName)
                                .replaceAll("lname", randomContact.lastName)
                                .replaceAll("variable1", customMessage.getVariableOne())
                                .replaceAll("variable2", customMessage.getVaribaleTwo());
                        new MaterialDialog.Builder(MainActivity.this)
                                .title("Message Preview:")
                                .content(previewMessage)
                                .positiveText("OKAY")
                                .negativeText("NEVERMIND")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        checkPermissionAndSendText();
                                    }
                                })
                                .show();
                    }
                })
                .show();
    }

    private void checkPermissionAndSendText() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendTextMessage();
        } else {
            requestSENDSMSPermission();
        }
    }

    private void sendTextMessage() {
        for(Contact contact : allContacts) {
            String finalMessage = customMessage.getMessage().replaceAll("fname", contact.firstName)
                    .replaceAll("lname", contact.lastName)
                    .replaceAll("variable1", customMessage.getVariableOne())
                    .replaceAll("variable2", customMessage.getVaribaleTwo());
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(finalMessage);
            smsManager.sendMultipartTextMessage(contact.phoneNumber, null, parts, null, null);
        }
        showSnackBar(this, "Sent text message to " + allContacts.size() + " contacts.");

    }

    @Override
    public void showSnackBar(Activity activity, String message){
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
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
