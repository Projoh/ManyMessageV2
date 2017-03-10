package android.mayassin.com.manymessagev2;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by moham on 3/9/2017.
 */

public class SelectContactsActivity extends AppCompatActivity {
    private ArrayList<Contact> allContacts = new ArrayList<Contact>();
    private ArrayList<Contact> checkedList = new ArrayList<Contact>();
    private RecyclerView recyleView;
    private RecycleViewAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        pullAllContacts();
    }

    private void pullAllContacts() {
        try {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contact contact = new Contact(name, phoneNumber);
                allContacts.add(contact);
            }
            setUpRecyleViewer();
            phones.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpRecyleViewer() {
        recyleView = (RecyclerView) findViewById(R.id.recyler_view);
        adapter = new RecycleViewAdapter(getApplicationContext(), allContacts);

    }
}
