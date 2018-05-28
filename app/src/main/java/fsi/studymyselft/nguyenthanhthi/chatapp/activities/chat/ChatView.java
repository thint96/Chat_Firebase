package fsi.studymyselft.nguyenthanhthi.chatapp.activities.chat;

import fsi.studymyselft.nguyenthanhthi.chatapp.activities.BaseMainView;

/**
 * Created by thanhthi on 28/05/2018.
 */

public interface ChatView extends BaseMainView {

    void deleteMessage();

    void copyToClipBoard();

    void initMessageAdapter();

    void showMessagesList();
}
