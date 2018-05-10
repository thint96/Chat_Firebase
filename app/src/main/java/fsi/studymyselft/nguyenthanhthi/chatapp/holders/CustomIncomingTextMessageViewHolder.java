package fsi.studymyselft.nguyenthanhthi.chatapp.holders;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;

import fsi.studymyselft.nguyenthanhthi.chatapp.R;
import fsi.studymyselft.nguyenthanhthi.chatapp.data.Message;

/**
 * Created by thanhthi on 08/05/2018.
 */

public class CustomIncomingTextMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private View onlineIndicator;

    public CustomIncomingTextMessageViewHolder(View itemView) {
        super(itemView);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

//        boolean isOnline = message.getUser().isOnline();
        boolean isOnline = false;
        if (isOnline) {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online);
        }
        else {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline);
        }
    }
}
