package android.mayassin.com.manymessagev2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by moham on 3/11/2017.
 */

public class SavedContactsManager {
    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    public SavedContactsManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("sessionApp", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveGroup(String groupName, GroupOfContacts groupOfContacts) {
        Gson gson = new Gson();
        String contactsJson = gson.toJson(groupOfContacts);
        editor.putString(groupName, contactsJson);
        String currentGroups = pref.getString("groupNames", "");
        if(currentGroups == "") {
            editor.putString("groupNames", groupName);
            editor.commit();
        } else {
            editor.putString("groupNames", currentGroups+"\n"+groupName);
            editor.commit();
        }
    }

    public String[] getAllGroups() {
        String currentGroups = pref.getString("groupNames", "");
        return currentGroups.split("\\r?\\n");
    }

    public ArrayList<Contact> getGroup(String groupName) {
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(groupName, ""), GroupOfContacts.class).contacts;
    }

    public void deleteGroups() {
        editor.clear();
        editor.commit();
    }
}
