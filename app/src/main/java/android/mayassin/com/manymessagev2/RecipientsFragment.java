package android.mayassin.com.manymessagev2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by moham on 3/9/2017.
 */

public class RecipientsFragment extends Fragment {
    private static final String UPDATE_CONTACTS_REQUEST = "updateContacts";
    private View view;
    private BroadcastReceiver contactsReciever;
    private RecipientsInterface recpInterface;
    private ArrayList<Contact> allContacts = new ArrayList<Contact>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_recipients, container, false);
        intialize();
        return view;
    }

    private void setRecpInterface(RecipientsInterface recp) {
        recpInterface = recp;
    }
    private void intialize() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Message Recipients");
        setUpContactsReciever();
    }

    private void setUpContactsReciever() {
        contactsReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                popUpBox(intent.getStringExtra("product_id"), 0);
                allContacts.addAll(recpInterface.getSelectedContacts());
            }
        };
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(contactsReciever, new IntentFilter(UPDATE_CONTACTS_REQUEST));
    }
}
