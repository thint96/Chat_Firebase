package fsi.studymyselft.nguyenthanhthi.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;

/**
 * Created by thanhthi on 04/05/2018.
 */

public class ListUserAdapter extends BaseAdapter {

    private ArrayList<User> users;
    private LayoutInflater inflater;

    private TextView avatar, email;

    public ListUserAdapter(Context context, ArrayList<User> users) {
        inflater = LayoutInflater.from(context);
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_users, null);
        }
        avatar = convertView.findViewById(R.id.txtAvatar);
        email = convertView.findViewById(R.id.txtEmail);

        avatar.setText(users.get(position).getEmail().substring(0, 1).toUpperCase());
        email.setText(users.get(position).getEmail());

        return convertView;
    }
}
