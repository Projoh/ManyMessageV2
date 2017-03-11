package androidapps.mayassin.com.manymessage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by moham on 3/11/2017.
 */

public class SessionManager {
    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("sessionApp", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveGroup(String groupName, GroupOfContacts groupOfContacts) {
        Gson gson = new Gson();
        String contactsJson = gson.toJson(groupOfContacts);
        editor.putString(groupName, contactsJson);
        String currentGroups = pref.getString("savedGroups", "");
        if(currentGroups == "") {
            editor.putString("savedGroups", groupName+"\n");
        } else {
            editor.putString("savedGroups", currentGroups+groupName+"\n");
        }
        editor.commit();
    }

    public String[] getAllGroups() {
        String currentGroups = pref.getString("savedGroups", "");
        return currentGroups.split("\\r?\\n");
    }

    public ArrayList<Contact> getGroup(String groupName) {
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(groupName, ""), GroupOfContacts.class).contacts;
    }

    public void deleteGroup(String groupname) {
        String newGroups = pref.getString("savedGroups", "").replace(groupname+"\n", "");
        editor.putString("savedGroups",newGroups);
        editor.remove(groupname);
        editor.commit();
    }

    public void saveMessage(String messageName, CustomMessage customMessage) {
        Gson gson = new Gson();
        String messageJson = gson.toJson(customMessage);
        editor.putString(messageName, messageJson);
        editor.commit();
        String currentMessages = pref.getString("savedMessages", "");
        if (currentMessages == "") {
            editor.putString("savedMessages", messageName+"\n");
        } else {
            editor.putString("savedMessages", currentMessages+messageName+"\n");
        }
        editor.commit();
    }

    public String[] getAllMessages() {
        String currentMessages = pref.getString("savedMessages", "");
        return currentMessages.split("\\r?\\n");
    }

    public CustomMessage getMessage(String messageName) {
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(messageName, ""), CustomMessage.class);
    }

    public void deleteMessage(String messageName) {
        String newMessages = pref.getString("savedMessages", "").replace(messageName+"\n", "");
        editor.putString("savedMessages",newMessages);
        editor.remove(messageName);
        editor.commit();
    }
}
