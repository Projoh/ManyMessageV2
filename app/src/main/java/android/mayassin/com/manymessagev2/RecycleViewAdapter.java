package android.mayassin.com.manymessagev2;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moham on 3/9/2017.
 */

public class RecycleViewAdapter  extends RecyclerView.Adapter<RecycleViewAdapter.CustomViewHolder> implements Filterable{

    private ArrayList<Contact> allContacts = new ArrayList<Contact>();
    private ArrayList<Contact> allContactsFilter = new ArrayList<Contact>();
    private Context mContext;
    private TextSearchFilter mTextFilter = new TextSearchFilter();

    public RecycleViewAdapter(Context context, ArrayList<Contact> allContacts) {
        mContext = context;
        this.allContacts = allContacts;
        this.allContactsFilter = allContacts;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.CustomViewHolder holder, int position) {
        Contact contact = allContacts.get(position);
        char firstLetter = contact.firstName.charAt(0);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int generatedColor = generator.getColor(firstLetter);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                    .bold()
                    .toUpperCase()
                .endConfig()
                .buildRound(String.valueOf(firstLetter), generatedColor);

        holder.name.setText(contact.firstName+" "+contact.lastName);
        holder.phoneNumber.setText(contact.phoneNumber);
        holder.circularBackground.setImageDrawable(drawable);
        holder.checkMarkImage.setVisibility(View.GONE);
        holder.listLayout.setBackgroundColor(Color.WHITE);
        if(contact.isSelected()) {
            holder.checkBox.setSelected(true);
            holder.checkMarkImage.setVisibility(View.VISIBLE);
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound(" ", Color.GRAY);
            holder.circularBackground.setImageDrawable(drawable2);
            holder.listLayout.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return (null != allContacts ? allContacts.size() : 0);
    }



    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView name,phoneNumber;
        protected CheckBox checkBox;
        protected ImageView circularBackground,checkMarkImage;
        protected View listLayout;


        public CustomViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.contact_name_text_view);
            this.phoneNumber = (TextView) itemView.findViewById(R.id.contact_number_text_view);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.contact_checkbox);
            this.circularBackground = (ImageView) itemView.findViewById(R.id.contact_image_view);
            this.checkMarkImage = (ImageView) itemView.findViewById(R.id.checkmark_image_view);
            this.listLayout = itemView.findViewById(R.id.list_item_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Contact contact = allContacts.get(getAdapterPosition());
            if(contact.isSelected()) {
                deselectContact(contact);
            } else {
                selectContact(contact);
            }
        }

        private void selectContact(Contact contact) {
            contact.setSelected(true);
            checkMarkImage.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(" ", Color.GRAY);
            circularBackground.setImageDrawable(drawable);
            listLayout.setBackgroundColor(Color.LTGRAY);
        }

        private void deselectContact(Contact contact) {
            char firstLetter = contact.firstName.charAt(0);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int generatedColor = generator.getColor(firstLetter);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRound(String.valueOf(firstLetter), generatedColor);
            contact.setSelected(false);
            checkMarkImage.setVisibility(View.GONE);
            circularBackground.setImageDrawable(drawable);
            listLayout.setBackgroundColor(Color.WHITE);
        }


    }

    @Override
    public Filter getFilter() {
        return mTextFilter;
    }
    private class TextSearchFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Contact> list = allContactsFilter;

            int count = list.size();
            final ArrayList<Contact> nlist = new ArrayList<Contact>(count);

            Contact filterableProduct;

            for (int i = 0; i < count; i++) {
                filterableProduct = list.get(i);
                if (filterableProduct.firstName.toLowerCase().contains(filterString) || filterableProduct.lastName.toLowerCase().contains(filterString) || filterableProduct.phoneNumber.toLowerCase().contains(filterString)) {
                    nlist.add(filterableProduct);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allContacts = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
        }
    }


    }
