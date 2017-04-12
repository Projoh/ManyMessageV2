package androidapps.mayassin.com.manymessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by moham on 3/9/2017.
 */

public class RecipientsFragment extends Fragment {
    private static final String UPDATE_CONTACTS_REQUEST = "updateContacts";
    private static final String REMOVE_CONTACTS_REQUEST = "removeSelectedContacts";
    private static final String SELECT_ALL_CONTACTS = "selectAllContacts";
    private static final String FILTER_CONTACTS = "filterContacts";
    private View view;
    private BroadcastReceiver newContactsReciever, removeContactsReciever, selectAllContactsReciever, filterContactsReceiver;
    private RecipientsInterface recpInterface;
    private ArrayList<Contact> allContacts = new ArrayList<Contact>();
    private RecyclerView recyleView;
    private RecycleViewAdapter adapter;
    private boolean selectedAllContacts, fabsShowing = true;
    private VerticalRecyclerViewFastScroller fastScroller;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_recipients, container, false);
        intialize();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof RecipientsInterface){
            recpInterface = (RecipientsInterface)context;
        }
    }

    private void intialize() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Message Recipients");
        setUpRecylerView();
        setUpRecievers();
    }

    private void setUpRecylerView() {
        recyleView = (RecyclerView) view.findViewById(R.id.recyler_view_recipients);
        recyleView.setLayoutManager(new LinearLayoutManager(getContext()));
        fastScroller = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(recyleView);
        recyleView.setOnScrollListener(fastScroller.getOnScrollListener());
        adapter = new RecycleViewAdapter(getContext(), allContacts);
        recyleView.setAdapter(adapter);
        recyleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    if(fabsShowing) {
                        recpInterface.hideFABs();
                        fabsShowing = false;
                    }
                } else if (dy <0) {
                    if (!fabsShowing) {
                        recpInterface.showFABs();
                        fabsShowing = true;
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
    private void checkFABsAndShow() {
        if(!allContacts.isEmpty()) {
            recpInterface.showFABs();
            fastScroller.setVisibility(View.VISIBLE);
        } else {
            recpInterface.hideFABs();
            fastScroller.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(recpInterface.getSelectedContacts() != null) {
            allContacts = recpInterface.getSelectedContacts();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        allContacts = recpInterface.getSelectedContacts();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        recpInterface.setSelectedContacts(allContacts);
    }

    @Override
    public void onStop() {
        super.onStop();
        recpInterface.setSelectedContacts(allContacts);
    }

    private void setUpRecievers() {
        newContactsReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                popUpBox(intent.getStringExtra("product_id"), 0);
                allContacts = recpInterface.getSelectedContacts();
                adapter = new RecycleViewAdapter(getContext(), allContacts);
                recyleView.setAdapter(adapter);
                selectedAllContacts = false;
                checkFABsAndShow();
            }
        };

        removeContactsReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<Contact> contactsToRemove = new ArrayList<>();
                for(Contact contact : allContacts) {
                    if(contact.isSelected()) {
                        contactsToRemove.add(contact);
                    }
                }
                allContacts.removeAll(contactsToRemove);
                checkFABsAndShow();
                selectedAllContacts = false;
                adapter.notifyDataSetChanged();
            }
        };
        selectAllContactsReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for(Contact contact : allContacts) {
                    contact.setSelected(!selectedAllContacts);
                }
                selectedAllContacts = !selectedAllContacts;
                adapter.notifyDataSetChanged();
            }
        };

        filterContactsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               //
                adapter.getFilter().filter(intent.getStringExtra("filter_text").replace("\n", ""));
                selectedAllContacts = false;
            }
        };

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(newContactsReciever, new IntentFilter(UPDATE_CONTACTS_REQUEST));

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(removeContactsReciever, new IntentFilter(REMOVE_CONTACTS_REQUEST));

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(selectAllContactsReciever, new IntentFilter(SELECT_ALL_CONTACTS));

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(filterContactsReceiver, new IntentFilter(FILTER_CONTACTS));
    }
}
