package fsi.studymyselft.nguyenthanhthi.chatapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser.ListUserActivity;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Dialog;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.MessageRecent;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;
import fsi.studymyselft.nguyenthanhthi.chatapp.other.DrawableHelper;

/**
 * Created by thanhthi on 04/05/2018.
 */

public class ListUserAdapter extends BaseAdapter {

    private static final String TAG = "ListUserAdapter";

    private ArrayList<ListUserActivity.Item> items;

    private LayoutInflater inflater;

    private LinearLayout messageRecentLayout;
    private TextView txtAvatar, txtEmail, txtMessageRecent, txtPosition;


    public ListUserAdapter(Context context, ArrayList<ListUserActivity.Item> items) {
        inflater = LayoutInflater.from(context);
        this.items = items;
    }

    public ArrayList<ListUserActivity.Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<ListUserActivity.Item> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ListUserActivity.Item getItem(int position) {
        return items.get(position);
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

        txtAvatar = convertView.findViewById(R.id.txtAvatar);
        txtEmail = convertView.findViewById(R.id.txtEmail);
        txtMessageRecent = convertView.findViewById(R.id.txtRecentMessage);
        txtPosition = convertView.findViewById(R.id.txtPosition);
        messageRecentLayout = convertView.findViewById(R.id.message_recent_layout);

        User user = items.get(position).getUser();
        Message recentMessage = items.get(position).getRecentMessage();

        txtAvatar.setText(user.getEmail().substring(0, 1).toUpperCase());
        txtEmail.setText(user.getEmail());
        txtMessageRecent.setText(recentMessage.getText());

        //set color background for avatar
        DrawableHelper.withContext(convertView.getContext())
                .customColor(getRandomColor())
                .withDrawable(R.drawable.bg_avatar)
                .customTint()
                .applyToBackground(txtAvatar);

        return convertView;
    }

    private String getRandomColor() {
        String colorResult;
        Random random = new Random();
        int color = Color.argb(155, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        colorResult = "#" + Integer.toHexString(color);
        return colorResult;
    }
}