package fsi.studymyselft.nguyenthanhthi.chatapp.activities.listUser;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.ItemListDialog;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.User;
import fsi.studymyselft.nguyenthanhthi.chatapp.other.DrawableHelper;

/**
 * Created by thanhthi on 04/05/2018.
 */

public class ListUserAdapter extends BaseAdapter {

    private static final String TAG = "ListUserAdapter";

    private ArrayList<ItemListDialog> items;

    private LayoutInflater inflater;

    private LinearLayout messageRecentLayout;
    private TextView txtAvatar, txtEmail, txtMessageRecent, txtPosition;


    public ListUserAdapter(Context context, ArrayList<ItemListDialog> items) {
        inflater = LayoutInflater.from(context);
        this.items = items;
    }

    public ArrayList<ItemListDialog> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemListDialog> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ItemListDialog getItem(int position) {
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