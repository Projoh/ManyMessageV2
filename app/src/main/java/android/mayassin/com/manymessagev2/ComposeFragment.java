package android.mayassin.com.manymessagev2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by moham on 3/9/2017.
 */

public class ComposeFragment extends Fragment {
    private static final String SEND_MESSAGE = "sendMessage";
    private static final String DELETE_MESSAGE = "deleteMessage";
    private static final String CACHED_MESSAGE = "cachedMessage";
    private static final String SAVE_MESSAGE = "saveMessage";

    private View view;
    private View messageLayout;
    private TextView variableOne, variableTwo, messageBody;
    MaterialDialog messageDialog, variableOneDialog, variableTwoDialog;
    private CustomMessage customMessage = new CustomMessage();
    private ComposeInterface compInterface;
    private BroadcastReceiver sendMessageReciever, deleteMessageReciever, cachedMessageReciever, saveMessageReciever;
    private SessionManager session;

    private static String initialText = "Hey fname, I'm trying to contact the lname family. We are having a new event that involves variable1 and is sponsored by variable2. What do you think?";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_compose, container, false);
        intialize();
        return view;
    }

    private void intialize() {
        session = new SessionManager(getContext());
        intializeTextViews();
        intializeClickListeners();
        intializeDialogs();
        intializeRecieverData();
        intializeRecievers();
        resetComposeData();
    }

    private void intializeRecieverData() {
        sendMessageReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //
                compInterface.sendDataFromCompose(customMessage);
            }
        };
        deleteMessageReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new MaterialDialog.Builder(getContext())
                        .title("Delete all edits on this page?")
                        .content("This action cannot be undone.")
                        .positiveText("YES")
                        .negativeText("NEVERMIND")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                resetComposeData();
                            }
                        })
                        .show();
            }
        };
        cachedMessageReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //
                final String[] allMessages = session.getAllMessages();
                if(allMessages.length == 0 || allMessages[0].isEmpty()) {
                    compInterface.showSnackBar(getActivity(), "You have no saved messages! Save one using the save button.");
                    return;
                }

                new MaterialDialog.Builder(getContext())
                        .title("Saved Messages")
                        .items(allMessages)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                final String messageName = allMessages[which];
                                new MaterialDialog.Builder(getContext())
                                        .title("Select "+ messageName+"?")
                                        .content("This action cannot be undone.")
                                        .positiveText("YES")
                                        .negativeText("NEVERMIND")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                customMessage = session.getMessage(messageName);
                                                setMessageText(customMessage.getMessage());
                                                setVariableOneText(customMessage.getVariableOne());
                                                setVariableTwoText(customMessage.getVaribaleTwo());
                                            }
                                        })
                                        .show();
                            }
                        })
                        .show();
            }
        };
        saveMessageReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //
                new MaterialDialog.Builder(getContext())
                        .title("Save the current Message and variables to your device?")
                        .content("Are you sure you want to save this custom message for later use?")
                        .positiveText("YES")
                        .negativeText("NEVERMIND")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new MaterialDialog.Builder(getContext())
                                        .title("Message name?")
                                        .content("What would you like to save this custom message as?")
                                        .inputType(InputType.TYPE_CLASS_TEXT)
                                        .input("Club Meeting Message", "", new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                // Snackbar saying you saved it
                                                session.saveMessage(input.toString(), customMessage);
                                            }
                                        }).show();
                            }
                        })
                        .show();
            }
        };
    }

    private void resetComposeData() {
        setMessageText(initialText);
        setVariableOneText("Custom Text for the word variable1 in the Message Text.");
        setVariableTwoText("Custom Text for the word variable2 in the Message Text.");
    }

    private void intializeRecievers() {
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(sendMessageReciever, new IntentFilter(SEND_MESSAGE));

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(deleteMessageReciever, new IntentFilter(DELETE_MESSAGE));

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(cachedMessageReciever, new IntentFilter(CACHED_MESSAGE));

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(saveMessageReciever, new IntentFilter(SAVE_MESSAGE));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ComposeInterface){
            compInterface = (ComposeInterface)context;
        }
    }



    private void setMessageText(String message) {
        // Set the string
        // Set the textview with html
        String textViewString = message.replaceAll("fname","<font color='blue'>fname</font>")
                .replaceAll("lname","<font color='blue'>lname</font>")
                .replaceAll("variable1","<font color='blue'>variable1</font>")
                .replaceAll("variable2","<font color='blue'>variable2</font>");
        messageBody.setText(Html.fromHtml(textViewString));
        customMessage.setMessage(message);
    }

    private void intializeClickListeners() {
        messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.show();
            }
        });

        variableOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                variableOneDialog.show();
            }
        });
        variableTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                variableTwoDialog.show();
            }
        });
    }

    private void intializeTextViews() {
        messageLayout = view.findViewById(R.id.message_layout);
        variableOne = (TextView) view.findViewById(R.id.custom_text_one);
        variableTwo = (TextView) view.findViewById(R.id.custom_text_two);
        messageBody = (TextView) view.findViewById(R.id.message_body_text_view);

    }

    public void setVariableOneText(String variableOneText) {
        customMessage.setVariableOne(variableOneText);
        variableOne.setText(variableOneText);
    }

    public void setVariableTwoText(String variableTwoText) {
        customMessage.setVaribaleTwo(variableTwoText);
        variableTwo.setText(variableTwoText);
    }

    private void intializeDialogs() {
        messageDialog = new MaterialDialog.Builder(getContext())
                .title("Type message!")
                .content("Type your message(fname and lname are resevered for contact first and last names respectively)")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Hey fname, long time no talk, where have you been? Hanging out with the lname family?", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if(input.length() == 0) return;
                        setMessageText(input.toString());
                    }
                }).build();

        variableOneDialog = new MaterialDialog.Builder(getContext())
                .title("Type what your custom variable equals!!")
                .content("Whenever you type variable1 in your Message, it will appear as this:")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Major Sports Academy", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        setVariableOneText(input.toString());
                    }
                }).build();

        variableTwoDialog = new MaterialDialog.Builder(getContext())
                .title("Type what your custom variable equals!!")
                .content("Whenever you type variable2 in your Message, it will appear as this:")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("University of South Florida", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        setVariableTwoText(input.toString());
                    }
                }).build();
    }
}
