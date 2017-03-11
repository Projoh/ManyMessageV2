package android.mayassin.com.manymessagev2;

import java.util.ArrayList;

/**
 * Created by moham on 3/9/2017.
 */

public class GroupOfContacts {
    public ArrayList<Contact> contacts = new ArrayList<Contact>();

    public GroupOfContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
    public GroupOfContacts() {
    }
}
