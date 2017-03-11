package androidapps.mayassin.com.manymessage;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by moham on 3/9/2017.
 */

public class SelectContactsActivity extends AppCompatActivity {
    private ArrayList<Contact> allContacts = new ArrayList<Contact>();
    private ArrayList<Contact> checkedList = new ArrayList<Contact>();
    private RecyclerView recyleView;
    private RecycleViewAdapter adapter;
    private boolean selectedAllContacts;
    private com.github.clans.fab.FloatingActionButton sendSelected,selectAll;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setUpFABs();
        setSupportActionBar(myToolbar);
        pullAllContacts();
    }

    private void setUpFABs() {
        sendSelected = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_add_selected_contacts);
        selectAll = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_select_all_contacts);
        setUpFABListeners();
    }

    private void setUpFABListeners() {
        sendSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do are you sure dialog pop up now
                ArrayList<Contact> selectedContacts = new ArrayList<Contact>();
                ArrayList<String> selectedContactsNames = new ArrayList<String>();
                for(Contact contact : allContacts) {
                    if(contact.isSelected()) {
                        selectedContacts.add(contact);
                        selectedContactsNames.add(contact.firstName + " " + contact.lastName);
                    }
                }
                if(selectedContacts.isEmpty()) {
                    new MaterialDialog.Builder(SelectContactsActivity.this)
                            .title("No Contacts Selected")
                            .content("You selected no contacts. Click on any contact to select them, or click on select all at the bottom.")
                            .positiveText("OKAY")
                            .show();
                    return;
                }
                final ArrayList<Contact> sendingContacts = selectedContacts;
                new MaterialDialog.Builder(SelectContactsActivity.this)
                        .title("Are you sure you want to add these contacts?")
                        .items(selectedContactsNames)
                        .positiveText("YES")
                        .negativeText("NO")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // GSON then send to intent
                                for(Contact sendingContact:  sendingContacts) sendingContact.setSelected(false);
                                Gson gson = new Gson();
                                GroupOfContacts groupOfContacts = new GroupOfContacts();
                                groupOfContacts.contacts = sendingContacts;
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("selected_contacts", gson.toJson(groupOfContacts));
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })

                        .autoDismiss(false)
                        .show();
            }
        });
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Contact contact : allContacts) {
                    contact.setSelected(!selectedAllContacts);
                }
                selectedAllContacts = !selectedAllContacts;
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.replace("\n", ""));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(recyleView.getWindowToken(), 0);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.replace("\n", ""));
                return false;
            }
        });
        return true;
    }

    private void pullAllContacts() {
        try {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contact contact = new Contact(name, phoneNumber);
                boolean add = true;
                for(Contact contacttest : allContacts) {
                    if(contact.firstName.equals(contacttest.firstName) &&
                            contact.lastName.equals(contacttest.lastName) &&
                            contact.phoneNumber.equals(contacttest.phoneNumber)) {
                        add = false;
                        break;
                    }
                }
                if(add) allContacts.add(contact);
            }
            phones.close();
            setUpRecyleViewer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpRecyleViewer() {
        recyleView = (RecyclerView) findViewById(R.id.recyler_view);
        recyleView.setLayoutManager(new LinearLayoutManager(this));
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(recyleView);
        recyleView.setOnScrollListener(fastScroller.getOnScrollListener());
        adapter = new RecycleViewAdapter(getApplicationContext(), allContacts);
        recyleView.setAdapter(adapter);
        recyleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(recyleView.getWindowToken(), 0);
                return false;
            }
        });
        recyleView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 )
                {
                    if(selectAll.isShown()) {
                        selectAll.hide(true);
                        sendSelected.hide(true);
                    }
                } else if (dy <0) {
                    // Scroll Up
                    if (selectAll.isHidden()) {
                        selectAll.show(true);
                        sendSelected.show(true);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}
