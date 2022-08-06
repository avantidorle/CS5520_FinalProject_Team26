package edu.neu.madcourse.cs5520_finalproject_team26;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.neu.madcourse.cs5520_finalproject_team26.models.User;

public class RecepientNamesAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<User> userNamesList = null;
    private List<User> userArrayList;

    public RecepientNamesAdapter(Context context, List<User> userArrayList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.userNamesList = new ArrayList<>(userArrayList);
        this.userArrayList = new ArrayList<>(userArrayList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return userNamesList.size();
    }

    @Override
    public String getItem(int position) {
        return userNamesList.get(position).getUsername();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.recepient_name_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = view.findViewById(R.id.tv_recepientName_lan);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(userNamesList.get(position).getUsername());
        return view;
    }

    // Filter Class
    public void filter(String charText, ArrayList<User> users) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.d("logging filter",charText);
        userNamesList.clear();
        if (charText.length() == 0) {
            userNamesList.addAll(users);
        } else {
            Log.d("logging filter", String.valueOf(users.size()));
            for (User u : users) {
                Log.d("logging filter",u.getUsername());
                if (u.getUsername().toLowerCase(Locale.getDefault()).contains(charText)) {
                    userNamesList.add(u);
                }
            }
        }
        notifyDataSetChanged();
    }

}