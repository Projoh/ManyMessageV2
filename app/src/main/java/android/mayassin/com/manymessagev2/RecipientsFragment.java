package android.mayassin.com.manymessagev2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by moham on 3/9/2017.
 */

public class RecipientsFragment extends Fragment {
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_recipients, container, false);
        intialize();
        return view;
    }

    private void intialize() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Message Recipients");
    }
}
