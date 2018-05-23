package fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat.holders;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;

import fsi.studymyselft.nguyenthanhthi.chatapp.data.model.Message;

/**
 * Created by thanhthi on 08/05/2018.
 */

public class CustomOutcomingTextMessageViewHolder extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    public CustomOutcomingTextMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        time.setText("Sent " + time.getText());
    }
}
