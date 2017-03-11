package android.mayassin.com.manymessagev2;

import android.app.Activity;

/**
 * Created by moham on 3/11/2017.
 */

public interface ComposeInterface {

    void showComposeFAB();
    void hideComposeFAB();
    void sendDataFromCompose(CustomMessage customMessage);
    void showSnackBar(Activity activity, String text);
}
