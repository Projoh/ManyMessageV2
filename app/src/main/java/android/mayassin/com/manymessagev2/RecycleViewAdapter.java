package android.mayassin.com.manymessagev2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * Created by moham on 3/9/2017.
 */

public class RecycleViewAdapter  extends RecyclerView.Adapter<RecycleViewAdapter.CustomViewHolder> implements Filterable {

    private ArrayList<Contact> allContacts = new ArrayList<Contact>();
    private Context mContext;

    public RecycleViewAdapter(Context context, ArrayList<Contact> allContacts) {
        mContext = context;
        this.allContacts = allContacts;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public RecycleViewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CustomViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
